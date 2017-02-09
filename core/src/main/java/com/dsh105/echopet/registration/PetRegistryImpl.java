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

package com.dsh105.echopet.registration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.event.PetPreSpawnEvent;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin;
import com.dsh105.echopet.compat.api.registration.EntityMapModifier;
import com.dsh105.echopet.compat.api.registration.PetRegistrationEntry;
import com.dsh105.echopet.compat.api.registration.PetRegistrationException;
import com.dsh105.echopet.compat.api.registration.PetRegistry;
import com.dsh105.echopet.compat.api.util.ReflectionUtil;
import com.google.common.base.Preconditions;

import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.nms.NMSInsentientEntity;
import net.techcable.sonarpet.nms.entity.EntityInsentientPet;
import net.techcable.sonarpet.nms.entity.generators.EntityPetGenerator;
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.objectweb.asm.Type;

/**
 * Reversible registration of entities to Minecraft internals. Allows for temporary modification of internal mappings
 * so
 * that custom pet entities can be spawned.
 * <p/>
 * NOTE: This class is a modified version of the registry used in EchoPet v3.
 */
public class PetRegistryImpl implements PetRegistry {

    private static final EntityMapModifier<Class<?>, String> CLASS_TO_NAME_MODIFIER;
    private static final EntityMapModifier<Class<?>, Integer> CLASS_TO_ID_MODIFIER;
    private static final EntityMapModifier<Integer, Class<?>> ID_TO_CLASS_MODIFIER;

    static {
        Class<?> entityTypes = ReflectionUtil.getNMSClass("EntityTypes");
        List<Field> typeMaps = new ArrayList<>();
        for (Field candidate : entityTypes.getDeclaredFields()) {
            if (Map.class.isAssignableFrom(candidate.getType())) {
                candidate.setAccessible(true);
                typeMaps.add(candidate);
            }
        }
        try {
            //noinspection unchecked
            CLASS_TO_NAME_MODIFIER = new EntityMapModifier<>((Map<Class<?>, String>) typeMaps.get(1).get(null));
            //noinspection unchecked
            ID_TO_CLASS_MODIFIER = new EntityMapModifier<>((Map<Integer, Class<?>>) typeMaps.get(2).get(null));
            //noinspection unchecked
            CLASS_TO_ID_MODIFIER = new EntityMapModifier<>((Map<Class<?>, Integer>) typeMaps.get(3).get(null));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to initialise Entity type maps correctly!", e);
        }
    }

    private final IEchoPetPlugin plugin;
    private final Map<PetType, PetRegistrationEntry> registrationEntries = new EnumMap<>(PetType.class);
    private final Map<PetType, Class<?>> generatedPetClasses = new EnumMap<>(PetType.class);
    private final Object petGenerationLock = new Object();

    public PetRegistryImpl(IEchoPetPlugin plugin) {
        this.plugin = plugin;
        for (PetType petType : PetType.values()) {
            try {
                PetRegistrationEntry registrationEntry = PetRegistrationEntry.create(this, petType);
                registrationEntries.put(petType, registrationEntry);

                // Since these are guaranteed to be unique (for vanilla Minecraft), we can safely assume that they can be applied permanently during the lifetime of this plugin
                CLASS_TO_NAME_MODIFIER.modify(getPetEntityClass(petType), registrationEntry.getName());
                CLASS_TO_ID_MODIFIER.modify(getPetEntityClass(petType), registrationEntry.getRegistrationId());
            } catch (PetRegistrationException e) {
                // not found = not compatible with this server version
            }
        }
        CLASS_TO_NAME_MODIFIER.applyModifications();
        CLASS_TO_ID_MODIFIER.applyModifications();
    }

    public PetRegistrationEntry getRegistrationEntry(PetType petType) {
        return registrationEntries.get(petType);
    }

    public void shutdown() {
        CLASS_TO_NAME_MODIFIER.removeModifications();
        CLASS_TO_ID_MODIFIER.removeModifications();
    }

    public IPet spawn(PetType petType, final Player owner) {
        Preconditions.checkNotNull(petType, "Pet type must not be null.");
        Preconditions.checkNotNull(owner, "Owner type must not be null.");

        final PetRegistrationEntry registrationEntry = getRegistrationEntry(petType);
        if (registrationEntry == null) {
            // Pet type not registered
            return null;
        }

        return performRegistration(registrationEntry, () -> registrationEntry.createFor(owner));
    }

    public <T> T performRegistration(PetRegistrationEntry registrationEntry, Callable<T> callable) {
        Class<?> existingEntityClass = ID_TO_CLASS_MODIFIER.getMap().get(registrationEntry.getRegistrationId());
        // Just to be sure, remove any existing mappings and replace them afterwards
        // Make this entity the 'default' while the pet is being spawned
        ID_TO_CLASS_MODIFIER.clear(existingEntityClass);
        ID_TO_CLASS_MODIFIER.modify(registrationEntry.getRegistrationId(), getPetEntityClass(registrationEntry.getPetType()));

        try {
            ID_TO_CLASS_MODIFIER.applyModifications();
            return callable.call();
        } catch (Exception e) {
            throw new PetRegistrationException(e);
        } finally {
            // Ensure everything is back to normal
            // Client will now receive the correct entity ID and we're all set!
            ID_TO_CLASS_MODIFIER.removeModifications();
            ID_TO_CLASS_MODIFIER.add(existingEntityClass);
        }
    }

    public Class<?> getPetEntityClass(PetType petType) {
        synchronized (petGenerationLock) {
            return generatedPetClasses.computeIfAbsent(petType, (type) -> {
                Type generatedType = Type.getObjectType("net/techcable/sonarpet/nms/entities/type/Generated"
                        + type.getHookClass().getSimpleName());
                plugin.getLogger().fine("Generating " + type.getHookClass().getSimpleName());
                Class<? extends EntityPetGenerator> generatorClass;
                if (petType.getHookClass().isAnnotationPresent(GeneratorClass.class)) {
                    generatorClass = petType.getHookClass().getAnnotation(GeneratorClass.class).value();
                } else {
                    generatorClass = EntityPetGenerator.class;
                }
                try {
                    return generatorClass.getConstructor(Type.class, Class.class, Class.class)
                            .newInstance(generatedType, type.getHookClass(), type.getNmsClass())
                            .generateClass();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Unable to generate class for " + petType, e);
                }
            });
        }
    }

    @Override
    public IEntityPet spawnEntity(IPet pet, Player owner) {
        PetPreSpawnEvent spawnEvent = new PetPreSpawnEvent(pet, owner.getLocation());
        EchoPet.getPlugin().getServer().getPluginManager().callEvent(spawnEvent);
        if (spawnEvent.isCancelled()) {
            owner.sendMessage(EchoPet.getPrefix() + ChatColor.YELLOW + "Pet spawn was cancelled externally.");
            EchoPet.getManager().removePet(pet, true);
            return null;
        }
        PetRegistrationEntry entry = getRegistrationEntry(pet.getPetType());
        Class<?> entityType = getPetEntityClass(pet.getPetType());
        try {
            Field hookField = entityType.getField("hook");
            Object worldHandle = MinecraftReflection.getObcClass("CraftWorld").getMethod("getHandle")
                    .invoke(owner.getWorld());
            Object rawEntity = entityType.getConstructor(MinecraftReflection.getNmsClass("World"))
                    .newInstance(worldHandle);
            Entity bukkitEntity = (Entity) MinecraftReflection.getNmsClass("Entity").getDeclaredMethod("getBukkitEntity")
                    .invoke(rawEntity);
            NMSInsentientEntity entity = (NMSInsentientEntity) INMS.getInstance().wrapEntity(bukkitEntity);
            IEntityPet hook = entry.createHookClass(pet, entity);
            hookField.set(rawEntity, hook);
            INMS.getInstance().spawnEntity(entity, spawnEvent.getSpawnLocation());
            ((EntityInsentientPet) hook).initiateEntityPet();
            return hook;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to spawn pet: " + pet.getPetType(), e);
        }
    }
}