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

package com.dsh105.echopet.compat.api.entity;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.api.util.wrapper.WrappedEntityType;
import com.google.common.collect.ImmutableList;

import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.utils.MoreCollectors;
import net.techcable.sonarpet.utils.Versioning;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static com.google.common.base.Preconditions.*;

public enum PetType {

    // Aggressive mobs
    BLAZE("Blaze", 61, "Blaze Pet", 20D, 6D, "BLAZE", PetData.FIRE),
    CAVESPIDER("CaveSpider", 59, "Cave Spider Pet", 12D, 5D, "CAVE_SPIDER"),
    CREEPER("Creeper", 50, "Creeper Pet", 20D, 6D, "CREEPER", PetData.POWER),
    ENDERDRAGON("EnderDragon", 63, "EnderDragon Pet", 200D, 0D, "ENDER_DRAGON"),
    ENDERMAN("Enderman", 58, "Enderman Pet", 40D, 6D, "ENDERMAN", PetData.SCREAMING),
    ENDERMITE("Endermite", 67, "Endermite Pet", 2D, 2D, "ENDERMITE"),
    GHAST("Ghast", 56, "Ghast Pet", 10D, 7D, "GHAST"),
    GIANT("Giant", 53, "Giant Pet", 100D, 0D, "GIANT") {
        @Override
        public Class<?> getNmsClass() {
            return MinecraftReflection.getNmsClass("EntityGiantZombie");
        }
    },
    GUARDIAN("Guardian", 68, "Guardian Pet", 20D, 10D, "GUARDIAN", PetData.ELDER),
    MAGMACUBE("MagmaCube", 62, "Magma Cube Pet", 20D, 5D, "MAGMA_CUBE", PetData.SMALL, PetData.MEDIUM, PetData.LARGE),
    PIGZOMBIE("PigZombie", 57, "Pig Zombie Pet", 20D, 6D, "PIG_ZOMBIE", PetData.BABY),
    SILVERFISH("Silverfish", 60, "Silverfish Pet", 8D, 4D, "SILVERFISH"),
    SKELETON("Skeleton", 51, "Skeleton Pet", 20D, 5D, "SKELETON", PetData.WITHER),
    SLIME("Slime", 55, "Slime Pet", 20D, 4D, "SLIME", PetData.SMALL, PetData.MEDIUM, PetData.LARGE),
    SPIDER("Spider", 52, "Spider Pet", 16D, 5D, "SPIDER"),
    WITCH("Witch", 66, "Witch Pet", 26D, 5D, "WITCH"),
    WITHER("Wither", 64, "Wither Pet", 300D, 8D, "WITHER", PetData.SHIELD),
    ZOMBIE("Zombie", 54, "Zombie Pet", 20D, 5D, "ZOMBIE", PetData.BABY, PetData.VILLAGER),

    // Passive mobs
    BAT("Bat", 65, "Bat Pet", 6D, 3D, "BAT"),
    CHICKEN("Chicken", 93, "Chicken Pet", 4D, 3D, "CHICKEN", PetData.BABY),
    COW("Cow", 92, "Cow Pet", 10D, 4D, "COW", PetData.BABY),
    HORSE("Horse", 100, "Horse Pet", 30D, 4D, "HORSE", PetData.BABY, PetData.CHESTED, PetData.SADDLE,
          PetData.NORMAL, PetData.DONKEY,
          PetData.MULE, PetData.SKELETON, PetData.ZOMBIE, PetData.WHITE,
          PetData.CREAMY, PetData.CHESTNUT, PetData.BROWN, PetData.BLACK,
          PetData.GRAY, PetData.DARKBROWN, PetData.NONE, PetData.SOCKS,
          PetData.WHITEPATCH, PetData.WHITESPOT, PetData.BLACKSPOT,
          PetData.NOARMOUR, PetData.IRON, PetData.GOLD, PetData.DIAMOND),
    IRONGOLEM("IronGolem", 99, "Iron Golem Pet", 100D, 7D, "IRON_GOLEM"),
    MUSHROOMCOW("MushroomCow", 96, "Mushroom Cow Pet", 10D, 3D, "MUSHROOM_COW", PetData.BABY),
    OCELOT("Ocelot", 98, "Ocelot Pet", 10D, 4D, "OCELOT", PetData.BABY, PetData.BLACK, PetData.RED, PetData.SIAMESE, PetData.WILD),
    PIG("Pig", 90, "Pig Pet", 10D, 3D, "PIG", PetData.BABY, PetData.SADDLE),
    RABBIT("Rabbit", 101, "Rabbit Pet", 8D, 3D, "RABBIT", PetData.BABY, PetData.BROWN, PetData.WHITE, PetData.BLACK, PetData.BLACK_AND_WHITE, PetData.SALT_AND_PEPPER, PetData.THE_KILLER_BUNNY),
    SHEEP("Sheep", 91, "Sheep Pet", 8D, 3D, "SHEEP", PetData.BABY, PetData.SHEARED,
          PetData.BLACK, PetData.BLUE, PetData.BROWN,
          PetData.CYAN, PetData.GRAY, PetData.GREEN,
          PetData.LIGHTBLUE, PetData.LIME, PetData.MAGENTA,
          PetData.ORANGE, PetData.PINK, PetData.PURPLE, PetData.RED,
          PetData.SILVER, PetData.WHITE, PetData.YELLOW),
    SNOWMAN("Snowman", 97, "Snowman Pet", 4D, 4D, "SNOWMAN"),
    SQUID("Squid", 94, "Squid Pet", 10D, 4D, "SQUID"),
    VILLAGER("Villager", 120, "Villager Pet", 20D, 4D, "VILLAGER", PetData.BABY, PetData.BLACKSMITH, PetData.BUTCHER, PetData.FARMER, PetData.LIBRARIAN, PetData.PRIEST),
    WOLF("Wolf", 95, "Wolf Pet", 20D, 6D, "WOLF", PetData.BABY, PetData.TAMED, PetData.ANGRY,
         PetData.BLACK, PetData.BLUE, PetData.BROWN,
         PetData.CYAN, PetData.GRAY, PetData.GREEN,
         PetData.LIGHTBLUE, PetData.LIME, PetData.MAGENTA,
         PetData.ORANGE, PetData.PINK, PetData.PURPLE, PetData.RED,
         PetData.SILVER, PetData.WHITE, PetData.YELLOW),

    HUMAN("Human", 54, "Human Pet", 20D, 6D, "UNKNOWN");

    private final Class<? extends IPet> petClass;
    private final MethodHandle petConstructor;
    private final String defaultName;
    private final double maxHealth;
    private final double attackDamage;
    private final WrappedEntityType entityTypeWrapper;
    private final List<PetData> allowedData;
    private final int id;
    private final String classIdentifier;

    @SneakyThrows(ClassNotFoundException.class)
    PetType(String classIdentifier, int registrationId, String defaultName, double maxHealth, double attackDamage, String entityTypeName, PetData... allowedData) {
        this.classIdentifier = classIdentifier;
        this.petClass = Class.forName("com.dsh105.echopet.api.pet.type." + classIdentifier + "Pet").asSubclass(IPet.class);
        this.petConstructor = Reflection.getConstructor(petClass, Player.class);
        this.id = registrationId;
        this.allowedData = ImmutableList.copyOf(allowedData);
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.entityTypeWrapper = new WrappedEntityType(entityTypeName);
        this.defaultName = defaultName;
    }

    public int getRegistrationId() {
        return this.id;
    }

    public double getMaxHealth() {
        return this.maxHealth;
    }

    public String getDefaultName(String name) {
        return EchoPet.getConfig().getString("pets." + this.toString().toLowerCase().replace("_", " ") + ".defaultName", this.defaultName).replace("(user)", name).replace("(userApos)", name + "'s");
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    public double getAttackDamage() {
        return EchoPet.getConfig().getDouble("pets." + this.toString().toLowerCase().replace("_", " ") + ".attackDamage", this.attackDamage);
    }

    public EntityType getEntityType() {
        return this.entityTypeWrapper.get();
    }

    public List<PetData> getAllowedDataTypes() {
        return this.allowedData;
    }

    public boolean isDataAllowed(PetData data) {
        return getAllowedDataTypes().contains(data);
    }

    @SneakyThrows
    public IPet getNewPetInstance(Player owner) {
        checkState(isSupported(), "%s is not supported on %s", this, Versioning.NMS_VERSION);
        checkNotNull(owner, "Null owner");
        return (IPet) petConstructor.invoke(owner);
    }

    public Class<? extends IPet> getPetClass() {
        return this.petClass;
    }

    public Class<?> getNmsClass() {
        return MinecraftReflection.getNmsClass("Entity" + classIdentifier);
    }
    // NOTE: Lazy-load hookType information to avoid circular dependency issues
    private ImmutableList<EntityHookType> hookTypes;
    private EntityHookType primaryHookType;
    private void computeHookTypes() {
        this.hookTypes = Arrays.stream(EntityHookType.values())
                .filter((hookType) -> hookType.getPetType() == PetType.this)
                .filter(EntityHookType::isActive)
                .collect(MoreCollectors.toImmutableList());
        if (hookTypes.isEmpty()) {
            this.primaryHookType = null;
        } else {
            this.primaryHookType = hookTypes.stream()
                    .filter((hookType) -> hookType.getNmsName().equals(classIdentifier))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Couldn't find primary hook type for "
                                    + this
                                    + " in "
                                    + hookTypes.stream()
                                    .map(EntityHookType::toString)
                                    .collect(Collectors.joining(",", "[", "]"))
                    ));
        }
    }
    public boolean isSupported() {
        return !getHookTypes().isEmpty();
    }

    public EntityHookType getPrimaryHookType() {
        if (isSupported() && primaryHookType == null) computeHookTypes();
        return primaryHookType;
    }

    public ImmutableList<EntityHookType> getHookTypes() {
        if (hookTypes == null) computeHookTypes();
        return this.hookTypes;
    }

}