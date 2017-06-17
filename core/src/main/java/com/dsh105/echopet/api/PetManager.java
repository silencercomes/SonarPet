/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.echopet.api;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.dsh105.commodus.GeneralUtil;
import com.dsh105.echopet.compat.api.entity.HorseArmour;
import com.dsh105.echopet.compat.api.entity.HorseMarking;
import com.dsh105.echopet.compat.api.entity.HorseType;
import com.dsh105.echopet.compat.api.entity.HorseVariant;
import com.dsh105.echopet.compat.api.entity.IAgeablePet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SkeletonType;
import com.dsh105.echopet.compat.api.entity.ZombieType;
import com.dsh105.echopet.compat.api.entity.type.pet.IBlazePet;
import com.dsh105.echopet.compat.api.entity.type.pet.ICreeperPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IEndermanPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IGuardianPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IHorsePet;
import com.dsh105.echopet.compat.api.entity.type.pet.IMagmaCubePet;
import com.dsh105.echopet.compat.api.entity.type.pet.IOcelotPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IParrotPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IPigPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IRabbitPet;
import com.dsh105.echopet.compat.api.entity.type.pet.ISheepPet;
import com.dsh105.echopet.compat.api.entity.type.pet.ISkeletonPet;
import com.dsh105.echopet.compat.api.entity.type.pet.ISlimePet;
import com.dsh105.echopet.compat.api.entity.type.pet.IVillagerPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IWitherPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IWolfPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IZombiePet;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.api.plugin.IPetManager;
import com.dsh105.echopet.compat.api.plugin.PetStorage;
import com.dsh105.echopet.compat.api.plugin.uuid.UUIDMigration;
import com.dsh105.echopet.compat.api.util.Lang;
import com.dsh105.echopet.compat.api.util.Logger;
import com.dsh105.echopet.compat.api.util.PetUtil;
import com.dsh105.echopet.compat.api.util.ReflectionUtil;
import com.dsh105.echopet.compat.api.util.WorldUtil;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import net.techcable.sonarpet.CancelledSpawnException;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Villager.Profession;

import static com.google.common.base.Preconditions.*;


public class PetManager implements IPetManager {

    // NOTE: The reverse mapping is never used, but is present to force consistency
    private final BiMap<UUID, IPet> primaryPets = HashBiMap.create();
    private final SetMultimap<UUID, IPet> pets = HashMultimap.create();
    private void addPet(IPet pet) {
        UUID ownerId = pet.getOwnerUUID();
        if (pets.containsEntry(ownerId, pet)) {
            throw new IllegalStateException("Pet already present: " + pet);
        }
        IPet oldPrimaryPet = primaryPets.putIfAbsent(ownerId, pet);
        if (oldPrimaryPet != null) {
            throw new IllegalStateException(
                    "Can't add pet " + pet
                            + " since player already has "
                            + oldPrimaryPet
            );
        }
        checkState(pets.put(ownerId, pet));
    }
    @Override
    public void addSecondaryPet(IPet pet) {
        UUID playerId = pet.getOwnerUUID();
        IPet primaryPet = primaryPets.get(playerId);
        if (primaryPet == null) {
            throw new IllegalStateException(
                    "Can't add pet " + pet
                        + " since player doesn't already have primary pet!"
            );
        }
        if (!pets.put(playerId, pet)) {
            throw new IllegalStateException("Pet already present: " + pet);
        }
    }
    private boolean properlyRemoving = false;
    private boolean removePets(boolean makeSound, @Nullable UUID playerId, @Nullable Predicate<? super IPet> filter) {
        checkState(Bukkit.isPrimaryThread());
        final Set<IPet> targetPets;

        if (playerId != null) {
            targetPets = ImmutableSet.copyOf(pets.get(playerId));
            IPet primaryPet = primaryPets.get(playerId);
            if (!targetPets.isEmpty()) {
                checkState(primaryPet != null, "Player doesn't have primary pet: %s", playerId);
            } else {
                checkState(primaryPet == null, "Unknown primary pet: %s", primaryPet);
            }
        } else {
            targetPets = ImmutableSet.copyOf(pets.values());
        }
        properlyRemoving = true;
        try {
            boolean didRemove = false;
            if (!targetPets.isEmpty()) {
                for (IPet pet : targetPets) {
                    if (filter == null || filter.test(pet)) {
                        UUID ownerId = pet.getOwnerUUID();
                        checkState(playerId == null || playerId.equals(ownerId));
                        pet.removePet(makeSound);
                        primaryPets.remove(ownerId, pet);
                        checkState(pets.remove(ownerId, pet));
                        didRemove = true;
                    }
                }
            }
            return didRemove;
        } finally {
            properlyRemoving = false;
        }
    }

    @Override
    public Set<IPet> getPets() {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException("Pet access must be done on the main thread!");
        // NOTE: Perform copy to avoid CME
        return ImmutableSet.copyOf(pets.values());
    }

    @Override
    public IPet loadPets(Player p, boolean findDefault, boolean sendMessage, boolean checkWorldOverride) {
        if (EchoPet.getOptions().sqlOverride()) {
            IPet pet = EchoPet.getSqlManager().createPetFromDatabase(p);
            if (pet == null) {
                return null;
            } else {
                if (sendMessage) {
                    Lang.sendTo(p, Lang.DATABASE_PET_LOAD.toString().replace("%petname%", pet.getPetName()));
                }
            }
            return pet;
        } else if (EchoPet.getConfig(EchoPet.ConfigType.DATA).get("default." + UUIDMigration.getIdentificationFor(p) + ".pet.type") != null && findDefault) {
            IPet pi = this.createPetFromFile("default", p);
            if (pi == null) {
                return null;
            } else {
                if (sendMessage) {
                    Lang.sendTo(p, Lang.DEFAULT_PET_LOAD.toString().replace("%petname%", pi.getPetName()));
                }
            }
            return pi;
        } else if ((checkWorldOverride && EchoPet.getOptions().getConfig().getBoolean("multiworldLoadOverride", true)) || EchoPet.getOptions().getConfig().getBoolean("loadSavedPets", true)) {
            if (EchoPet.getConfig(EchoPet.ConfigType.DATA).get("autosave." + UUIDMigration.getIdentificationFor(p) + ".pet.type") != null) {
                IPet pi = this.createPetFromFile("autosave", p);
                if (pi == null) {
                    return null;
                } else {
                    if (sendMessage) {
                        Lang.sendTo(p, Lang.AUTOSAVE_PET_LOAD.toString().replace("%petname%", pi.getPetName()));
                    }
                }
                return pi;
            }
        }
        return null;
    }

    @Override
    public void removeAllPets() {
        removePets(true, null, (p) -> {
            saveFileData("autosave", p);
            EchoPet.getSqlManager().saveToDatabase(p, false);
            return true;
        });
    }

    @Override
    @Nullable
    public IPet createPet(Player owner, PetType petType, boolean sendMessageOnFail) {
        if (ReflectionUtil.BUKKIT_VERSION_NUMERIC == 178 && petType == PetType.HUMAN) {
            if (sendMessageOnFail) {
                Lang.sendTo(owner, Lang.HUMAN_PET_DISABLED.toString());
            }
            return null;
        }
        removePets(owner, true);
        if (!WorldUtil.allowPets(owner.getLocation())) {
            if (sendMessageOnFail) {
                Lang.sendTo(owner, Lang.PETS_DISABLED_HERE.toString().replace("%world%", WordUtils.capitalizeFully(owner.getWorld().getName())));
            }
            return null;
        }
        if (!EchoPet.getOptions().allowPetType(petType)) {
            if (sendMessageOnFail) {
                Lang.sendTo(owner, Lang.PET_TYPE_DISABLED.toString().replace("%type%", petType.toPrettyString()));
            }
            return null;
        }
        if (!petType.isSupported()) {
            if (sendMessageOnFail) {
                Lang.sendTo(owner, Lang.PET_TYPE_NOT_COMPATIBLE.toString().replace("%type%", petType.toPrettyString()));
            }
            return null;
        }
        IPet pi;
        try {
            pi = petType.getNewPetInstance(owner);
        } catch (CancelledSpawnException e) {
            if (sendMessageOnFail) {
                e.sendMessage();
            }
            return null;
        }
        addPet(pi);
        forceAllValidData(pi);
        return pi;
    }

    @Override
    public void internalOnRemove(IPet pet) {
        checkState(!pet.isSpawned(), "Pet currently spawned: %s", pet);
        Set<IPet> secondaryPets = pets.get(pet.getOwnerUUID());
        checkState(secondaryPets.contains(pet), "Unregistered pet: %s", pet);
        if (!properlyRemoving) {
            IPet primaryPet = primaryPets.get(pet.getOwnerUUID());
            if (primaryPet == null) {
                throw new IllegalStateException(noPrimaryPetErrorMessage(pet.getOwnerUUID(), secondaryPets));
            }
            checkState(pets.remove(pet.getOwnerUUID(), pet));
            if (primaryPet.equals(pet)) {
                // Remove all pets when the primary goes!
                removePets(false, pet.getOwnerUUID(), null);
            }
        }
    }

    @Override
    public IPet createPet(Player owner, PetType petType, PetType riderType) {
        if (ReflectionUtil.BUKKIT_VERSION_NUMERIC == 178 && (petType == PetType.HUMAN) || riderType == PetType.HUMAN) {
            Lang.sendTo(owner, Lang.HUMAN_PET_DISABLED.toString());
            return null;
        }
        removePets(owner, true);
        if (!WorldUtil.allowPets(owner.getLocation())) {
            Lang.sendTo(owner, Lang.PETS_DISABLED_HERE.toString().replace("%world%", WordUtils.capitalizeFully(owner.getWorld().getName())));
            return null;
        }
        if (!EchoPet.getOptions().allowPetType(petType)) {
            Lang.sendTo(owner, Lang.PET_TYPE_DISABLED.toString().replace("%type%", petType.toPrettyString()));
            return null;
        }
        if (!petType.isSupported()) {
            Lang.sendTo(owner, Lang.PET_TYPE_NOT_COMPATIBLE.toString().replace("%type%", petType.toPrettyString()));
            return null;
        }
        IPet pi;
        try {
            pi = petType.getNewPetInstance(owner);
        } catch (CancelledSpawnException e) {
            e.sendMessage();
            return null;
        }
        addPet(pi);
        pi.createRider(riderType, true);
        forceAllValidData(pi);
        return pi;
    }

    @Override
    public IPet getPet(Player player) {
        return getPet(player.getUniqueId());
    }
    public IPet getPet(UUID playerId) {
        IPet result = primaryPets.get(playerId);
        if (result == null) {
            Set<IPet> secondaryPets = pets.get(playerId);
            if (!secondaryPets.isEmpty()) {
                throw new IllegalStateException(noPrimaryPetErrorMessage(playerId, secondaryPets));
            }
        }
        return result;
    }
    private static String noPrimaryPetErrorMessage(UUID playerId, Set<IPet> secondaryPets) {
        Player player = Bukkit.getPlayer(playerId);
        String playerStr = player != null ? player.getName() : playerId.toString();
        return playerStr +
                " has no primary pet, but has secondary pets: " +
                secondaryPets.stream()
                        .map(IPet::toString)
                        .collect(Collectors.joining(",", "{", "}"));
    }

    @Override
    public IPet getPet(Entity pet) {
        for (IPet pi : getPets()) {
            IPet rider = pi.getRider();
            if (pi.getEntityPet().equals(pet) || (rider != null && rider.getEntityPet().equals(pet))) {
                return pi;
            }
            if (pi.getCraftPet().equals(pet) || (rider != null && rider.getCraftPet().equals(pet))) {
                return pi;
            }
        }
        return null;
    }

    // Force all data specified in config file and notify player.
    @Override
    public void forceAllValidData(IPet pi) {
        ArrayList<PetData> tempData = new ArrayList<>();
        for (PetData data : PetData.values()) {
            if (EchoPet.getOptions().forceData(pi.getPetType(), data)) {
                tempData.add(data);
            }
        }
        setData(pi, tempData.toArray(new PetData[tempData.size()]), true);

        ArrayList<PetData> tempRiderData = new ArrayList<>();
        if (pi.getRider() != null) {
            for (PetData data : PetData.values()) {
                if (EchoPet.getOptions().forceData(pi.getPetType(), data)) {
                    tempRiderData.add(data);
                }
            }
            setData(pi.getRider(), tempRiderData.toArray(new PetData[tempData.size()]), true);
        }

        if (EchoPet.getOptions().getConfig().getBoolean("sendForceMessage", true)) {
            String dataToString = tempRiderData.isEmpty() ? PetUtil.dataToString(tempData, tempRiderData) : PetUtil.dataToString(tempData);
            if (dataToString != null) {
                Lang.sendTo(pi.getOwner(), Lang.DATA_FORCE_MESSAGE.toString().replace("%data%", dataToString));
            }
        }
    }

    @Override
    public void updateFileData(String type, IPet pet, ArrayList<PetData> list, boolean b) {
        EchoPet.getSqlManager().saveToDatabase(pet, pet.isRider());
        String w = pet.getOwner().getWorld().getName();
        String path = type + "." + w + "." + pet.getOwnerUUID();
        for (PetData pd : list) {
            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.data." + pd.toString().toLowerCase(), b);
        }
        EchoPet.getConfig(EchoPet.ConfigType.DATA).saveConfig();
    }

    @Override
    public IPet createPetFromFile(String type, Player p) {
        if (EchoPet.getOptions().getConfig().getBoolean("loadSavedPets", true)) {
            String path = type + "." + UUIDMigration.getIdentificationFor(p);
            if (EchoPet.getConfig(EchoPet.ConfigType.DATA).get(path) != null) {
                ArrayList<PetData> data = new ArrayList<>();
                PetType petType = PetType.fromDataString(EchoPet.getConfig(EchoPet.ConfigType.DATA).getString(path + ".pet.type"), data);
                String name = EchoPet.getConfig(EchoPet.ConfigType.DATA).getString(path + ".pet.name");
                if (Strings.isNullOrEmpty(name)) {
                    name = petType.getDefaultName(p.getName());
                }
                if (!EchoPet.getOptions().allowPetType(petType)) {
                    return null;
                }
                IPet pi = this.createPet(p, petType, true);
                if (pi == null) {
                    return null;
                }
                //Pet pi = petType.getNewPetInstance(p, petType);
                //Pet pi = new Pet(p, petType);

                pi.setPetName(name);

                ConfigurationSection cs = EchoPet.getConfig(EchoPet.ConfigType.DATA).getConfigurationSection(path + ".pet.data");
                if (cs != null) {
                    for (String key : cs.getKeys(false)) {
                        if (GeneralUtil.isEnumType(PetData.class, key.toUpperCase())) {
                            PetData pd = PetData.valueOf(key.toUpperCase());
                            data.add(pd);
                        } else {
                            Logger.log(Logger.LogLevel.WARNING, "Error whilst loading data Pet Save Data for " + pi.getNameOfOwner() + ". Unknown enum type: " + key + ".", true);
                        }
                    }
                }

                if (!data.isEmpty()) {
                    setData(pi, data.toArray(new PetData[data.size()]), true);
                }

                this.loadRiderFromFile(type, pi);

                forceAllValidData(pi);
                return pi;
            }
        }
        return null;
    }

    @Override
    public void loadRiderFromFile(IPet pet) {
        this.loadRiderFromFile("autosave", pet);
    }

    @Override
    public void loadRiderFromFile(String type, IPet pet) {
        String path = type + "." + pet.getOwnerUUID();
        if (EchoPet.getConfig(EchoPet.ConfigType.DATA).get(path + ".rider.type") != null) {
            ArrayList<PetData> riderData = new ArrayList<>();
            PetType riderPetType = PetType.fromDataString(EchoPet.getConfig(EchoPet.ConfigType.DATA).getString(path + ".rider.type"), riderData);
            String riderName = EchoPet.getConfig(EchoPet.ConfigType.DATA).getString(path + ".rider.name");
            if (Strings.isNullOrEmpty(riderName)) {
                riderName = riderPetType.getDefaultName(pet.getNameOfOwner());
            }
            if (EchoPet.getOptions().allowRidersFor(pet.getPetType())) {
                IPet rider = pet.createRider(riderPetType, true);
                if (rider != null && rider.isSpawned()) {
                    rider.setPetName(riderName);
                    ConfigurationSection mcs = EchoPet.getConfig(EchoPet.ConfigType.DATA).getConfigurationSection(path + ".rider.data");
                    if (mcs != null) {
                        for (String key : mcs.getKeys(false)) {
                            if (GeneralUtil.isEnumType(PetData.class, key.toUpperCase())) {
                                PetData pd = PetData.valueOf(key.toUpperCase());
                                riderData.add(pd);
                            } else {
                                Logger.log(Logger.LogLevel.WARNING, "Error whilst loading data Pet Rider Save Data for " + pet.getNameOfOwner() + ". Unknown enum type: " + key + ".", true);
                            }
                        }
                    }
                    if (!riderData.isEmpty()) {
                        setData(pet, riderData.toArray(new PetData[riderData.size()]), true);
                    }
                }
            }
        }
    }

    @Override
    public void removePets(Player player, boolean makeDeathSound) {
        removePets(makeDeathSound, player.getUniqueId(), null);
    }

    @Override
    public void removePet(IPet pi, boolean makeDeathSound) {
        UUID ownerId = pi.getOwnerUUID();
        checkState(pets.containsEntry(ownerId, pi), "Unregistered pet: %s", pi);
        // Removing the primary pet implies removing all pets
        boolean removeAll = getPet(ownerId).equals(pi);
        removePets(makeDeathSound, ownerId, removeAll ? null : Predicate.isEqual(pi));
    }

    @Override
    public void saveFileData(String type, IPet pet) {
        clearFileData(type, pet);

        String path = type + "." + pet.getOwnerUUID();
        PetType petType = pet.getPetType();

        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.type", petType.toString());
        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.name", pet.serialisePetName());

        for (PetData pd : pet.getPetData()) {
            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.data." + pd.toString().toLowerCase(), true);
        }

        if (pet.getRider() != null) {
            PetType riderType = pet.getRider().getPetType();

            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".rider.type", riderType.toString());
            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".rider.name", pet.getRider().serialisePetName());
            for (PetData pd : pet.getRider().getPetData()) {
                EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".rider.data." + pd.toString().toLowerCase(), true);
            }
        }
        EchoPet.getConfig(EchoPet.ConfigType.DATA).saveConfig();
    }

    @Override
    public void saveFileData(String type, Player p, PetStorage UPD, PetStorage UMD) {
        clearFileData(type, p);
        PetType pt = UPD.petType;
        PetData[] data = UPD.petDataList.toArray(new PetData[UPD.petDataList.size()]);
        String petName = UPD.petName;
        if (UPD.petName == null || UPD.petName.equalsIgnoreCase("")) {
            petName = pt.getDefaultName(p.getName());
        }
        PetType riderType = UMD.petType;
        PetData[] riderData = UMD.petDataList.toArray(new PetData[UMD.petDataList.size()]);
        String riderName = UMD.petName;
        if (UMD.petName == null || UMD.petName.equalsIgnoreCase("")) {
            riderName = pt.getDefaultName(p.getName());
        }

        String path = type + "." + UUIDMigration.getIdentificationFor(p);
        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.type", pt.toString());
        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.name", petName);

        for (PetData pd : data) {
            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.data." + pd.toString().toLowerCase(), true);
        }

        if (riderType != null) {
            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".rider.type", riderType.toString());
            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".rider.name", riderName);
            for (PetData pd : riderData) {
                EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".rider.data." + pd.toString().toLowerCase(), true);
            }

        }
        EchoPet.getConfig(EchoPet.ConfigType.DATA).saveConfig();
    }

    @Override
    public void saveFileData(String type, Player p, PetStorage UPD) {
        clearFileData(type, p);
        PetType pt = UPD.petType;
        PetData[] data = UPD.petDataList.toArray(new PetData[UPD.petDataList.size()]);
        String petName = UPD.petName;

        String path = type + "." + UUIDMigration.getIdentificationFor(p);
        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.type", pt.toString());
        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.name", petName);

        for (PetData pd : data) {
            EchoPet.getConfig(EchoPet.ConfigType.DATA).set(path + ".pet.data." + pd.toString().toLowerCase(), true);
        }
        EchoPet.getConfig(EchoPet.ConfigType.DATA).saveConfig();
    }

    @Override
    public void clearAllFileData() {
        for (String key : EchoPet.getConfig(EchoPet.ConfigType.DATA).getKeys(true)) {
            if (EchoPet.getConfig(EchoPet.ConfigType.DATA).get(key) != null) {
                EchoPet.getConfig(EchoPet.ConfigType.DATA).set(key, null);
            }
        }
        EchoPet.getConfig(EchoPet.ConfigType.DATA).saveConfig();
    }

    @Override
    public void clearFileData(String type, IPet pi) {
        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(type + "." + pi.getOwnerUUID(), null);
        EchoPet.getConfig(EchoPet.ConfigType.DATA).saveConfig();
    }

    @Override
    public void clearFileData(String type, Player p) {
        EchoPet.getConfig(EchoPet.ConfigType.DATA).set(type + "." + UUIDMigration.getIdentificationFor(p), null);
        EchoPet.getConfig(EchoPet.ConfigType.DATA).saveConfig();
    }

    @Override
    public void setData(IPet pet, PetData[] data, boolean b) {
        for (PetData pd : data) {
            setData(pet, pd, b);
        }
    }

    @SuppressWarnings("ConstantConditions") // Too lazy to get rid of this crap
    @Override
    public void setData(IPet pet, PetData pd, boolean b) {
        PetType petType = pet.getPetType();
        if (petType.isDataAllowed(pd)) {
            if (petType == PetType.PARROT && pd.isType(PetData.Type.PARROT_COLOR)) {
                Parrot.Variant color = Parrot.Variant.valueOf(pd.toString());
                ((IParrotPet) pet).setParrotColor(color);
            }
            if (pd == PetData.BABY) {
                if (petType == PetType.ZOMBIE) {
                    ((IZombiePet) pet).setBaby(b);
                } else {
                    ((IAgeablePet) pet).setBaby(b);
                }
            }

            if (pd == PetData.POWER) {
                ((ICreeperPet) pet).setPowered(b);
            }

            if (pd.isType(PetData.Type.SIZE)) {
                int i = 1;
                if (pd == PetData.MEDIUM) {
                    i = 2;
                } else if (pd == PetData.LARGE) {
                    i = 4;
                }
                if (petType == PetType.SLIME) {
                    ((ISlimePet) pet).setSize(i);
                }
                if (petType == PetType.MAGMACUBE) {
                    ((IMagmaCubePet) pet).setSize(i);
                }
            }

            if (pd.isType(PetData.Type.CAT) && petType == PetType.OCELOT) {
                try {
                    org.bukkit.entity.Ocelot.Type t = org.bukkit.entity.Ocelot.Type.valueOf(pd.toString() + (pd == PetData.WILD ? "_OCELOT" : "_CAT"));
                    if (t != null) {
                        ((IOcelotPet) pet).setCatType(t);
                    }
                } catch (Exception e) {
                    Logger.log(Logger.LogLevel.SEVERE, "Encountered exception whilst attempting to convert PetData to Ocelot.Type.", e, true);
                }
            }

            if (pd == PetData.ANGRY) {
                ((IWolfPet) pet).setAngry(b);
            }

            if (pd == PetData.TAMED) {
                ((IWolfPet) pet).setTamed(b);
            }

            if (pd.isType(PetData.Type.PROF)) {
                Profession p = Profession.valueOf(pd.toString());
                if (p != null) {
                    ((IVillagerPet) pet).setProfession(p);
                }
            }

            if (pd.isType(PetData.Type.COLOUR) && (petType == PetType.SHEEP || petType == PetType.WOLF)) {
                String s = pd == PetData.LIGHTBLUE ? "LIGHT_BLUE" : pd.toString();
                try {
                    DyeColor dc = DyeColor.valueOf(s);
                    if (dc != null) {
                        if (petType == PetType.SHEEP) {
                            ((ISheepPet) pet).setColor(dc);
                        } else if (petType == PetType.WOLF) {
                            ((IWolfPet) pet).setCollarColor(dc);
                        }
                    }
                } catch (Exception e) {
                    Logger.log(Logger.LogLevel.SEVERE, "Encountered exception whilst attempting to convert PetData to DyeColor.", e, true);
                }
            }

            if (pd.isType(PetData.Type.SKELETON_TYPE)) {
                SkeletonType skeletonType = SkeletonType.valueOf(pd.toString());
                ((ISkeletonPet) pet).setSkeletonType(skeletonType);
            }

            if (pd == PetData.FIRE) {
                ((IBlazePet) pet).setOnFire(b);
            }

            if (pd == PetData.SADDLE) {
                if (petType == PetType.PIG) {
                    ((IPigPet) pet).setSaddle(b);
                } else if (petType == PetType.HORSE) {
                    ((IHorsePet) pet).setSaddled(b);
                }
            }

            if (pd == PetData.SHEARED) {
                ((ISheepPet) pet).setSheared(b);
            }

            if (pd == PetData.SCREAMING) {
                ((IEndermanPet) pet).setScreaming(b);
            }

            if (pd == PetData.SHIELD) {
                ((IWitherPet) pet).setShielded(b);
            }
            
            if (pd == PetData.ELDER) {
                ((IGuardianPet) pet).setElder(b);
            }

            if (pd.isType(PetData.Type.RABBIT_TYPE) && petType == PetType.RABBIT) {
                ((IRabbitPet) pet).setRabbitType(Rabbit.Type.valueOf(pd.toString()));
            }

            if (petType == PetType.HORSE) {
                if (pd == PetData.CHESTED) {
                    ((IHorsePet) pet).setChested(b);
                }

                if (pd.isType(PetData.Type.HORSE_TYPE)) {
                    try {
                        HorseType h = HorseType.valueOf(pd.toString());
                        if (h != null) {
                            ((IHorsePet) pet).setHorseType(h);
                        }
                    } catch (Exception e) {
                        Logger.log(Logger.LogLevel.WARNING, "Encountered exception whilst attempting to convert PetData to Horse.Type.", e, true);
                    }
                }

                if (pd.isType(PetData.Type.HORSE_VARIANT)) {
                    try {
                        HorseVariant v = HorseVariant.valueOf(pd.toString());
                        if (v != null) {
                            HorseMarking m = ((IHorsePet) pet).getMarking();
                            if (m == null) {
                                m = HorseMarking.NONE;
                            }
                            ((IHorsePet) pet).setVariant(v, m);
                        }
                    } catch (Exception e) {
                        Logger.log(Logger.LogLevel.WARNING, "Encountered exception whilst attempting to convert PetData to Horse.Variant.", e, true);
                    }
                }

                if (pd.isType(PetData.Type.HORSE_MARKING)) {
                    try {
                        HorseMarking m;
                        if (pd == PetData.WHITEPATCH) {
                            m = HorseMarking.WHITE_PATCH;
                        } else if (pd == PetData.WHITESPOT) {
                            m = HorseMarking.WHITE_SPOTS;
                        } else if (pd == PetData.BLACKSPOT) {
                            m = HorseMarking.BLACK_SPOTS;
                        } else {
                            m = HorseMarking.valueOf(pd.toString());
                        }
                        if (m != null) {
                            HorseVariant v = ((IHorsePet) pet).getVariant();
                            if (v == null) {
                                v = HorseVariant.WHITE;
                            }
                            ((IHorsePet) pet).setVariant(v, m);
                        }
                    } catch (Exception e) {
                        Logger.log(Logger.LogLevel.WARNING, "Encountered exception whilst attempting to convert PetData to Horse.Marking.", e, true);
                    }
                }

                if (pd.isType(PetData.Type.HORSE_ARMOUR)) {
                    try {
                        HorseArmour a;
                        if (pd == PetData.NOARMOUR) {
                            a = HorseArmour.NONE;
                        } else {
                            a = HorseArmour.valueOf(pd.toString());
                        }
                        if (a != null) {
                            ((IHorsePet) pet).setArmour(a);
                        }
                    } catch (Exception e) {
                        Logger.log(Logger.LogLevel.WARNING, "Encountered exception whilst attempting to convert PetData to Horse.Armour.", e, true);
                    }
                }
            }
            
            if (petType == PetType.ZOMBIE) {
                if (pd.isType(PetData.Type.ZOMBIE_TYPE)) {
                    ZombieType zombieType = ZombieType.valueOf(pd.toString());
                    ((IZombiePet) pet).setZombieType(zombieType);
                }
            }
            
            pet.getPetData().removeIf(petData -> {
                if (petData != pd) {
                    for (PetData.Type dataType : pd.getTypes()) {
                        if (dataType != PetData.Type.BOOLEAN && petData.isType(dataType)) {
                            return true;
                        }
                    }
                }
                return false;
            });

            if (b) {
                if (!pet.getPetData().contains(pd)) {
                    pet.getPetData().add(pd);
                }
            } else {
                if (pet.getPetData().contains(pd)) {
                    pet.getPetData().remove(pd);
                }
            }
        }
    }
}
