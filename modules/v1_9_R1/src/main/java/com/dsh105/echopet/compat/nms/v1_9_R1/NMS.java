package com.dsh105.echopet.compat.nms.v1_9_R1;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.dsh105.echopet.compat.api.entity.HorseArmour;
import com.dsh105.echopet.compat.api.entity.HorseMarking;
import com.dsh105.echopet.compat.api.entity.HorseVariant;
import com.google.common.collect.ImmutableMap;

import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.DataWatcherRegistry;
import net.minecraft.server.v1_9_R1.DataWatcherSerializer;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EnumHorseArmor;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.SoundEffectType;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_9_R1.CraftSound;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class NMS {


    //
    // Version Specific Code
    //

    public static Horse.Variant variantById(int id) {
        return Horse.Variant.values()[id];
    }

    public static int getId(Horse.Variant bukkitVariant) {
        return bukkitVariant.ordinal();
    }

    public static int getId(HorseArmour dsh) {
        return EnumHorseArmor.valueOf(dsh.name()).ordinal();
    }

    public static int getId(HorseVariant dshVariant, HorseMarking dshMarking) {
        return getId(dshVariant.getBukkitColour(), dshMarking.getBukkitStyle());
    }

    public static int getId(Horse.Color color, Horse.Style style) {
        return (color.ordinal() & 255) | (style.ordinal() << 8);
    }

    // Obfuscation helpers

    public static void jump(EntityInsentient e) {
        e.getControllerJump().a();
    }

    public enum MetadataType {
        BYTE(0),
        VAR_INT(1),
        BOOLEAN(6),
        OPTIONAL_UUID(11),
        OPTIONAL_BLOCK_DATA(12);

        private final int id;

        MetadataType(int id) {
            this.id = id;
        }
    }


    @SuppressWarnings("unchecked") // I wish enums had generics.....
    public static <T> DataWatcherObject<T> createMetadata(int index, MetadataType type) {
        return new DataWatcherObject<>(index, (DataWatcherSerializer<T>) DataWatcherRegistry.a(type.id));
    }

    public static void playSound(Entity entity, SoundEffect nmsSound, float volume, float pitch) {
        entity.a(nmsSound, volume, pitch);
    }


    private static MethodHandle getSoundEffectType;
    static {
        try {
            getSoundEffectType = MethodHandles.lookup().findGetter(Block.class, "stepSound", SoundEffectType.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError("Unable to access sound effect type", e);
        }
    }

    public static final MethodHandle getVolumeMethod;

    static {
        try {
            getVolumeMethod = MethodHandles.lookup().findVirtual(EntityLiving.class, "cd", MethodType.methodType(float.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError("Unable to access/find get volume method", e);
        }
    }

    @SneakyThrows // Should not throw exception
    public static float getVolume(EntityLiving e) {
        return (float) getVolumeMethod.invokeExact(e);
    }

    @SneakyThrows // No exception is ever thrown
    public static SoundEffectType getSoundEffectType(Block block) {
        return (SoundEffectType) getSoundEffectType.invokeExact(block);
    }

    /**
     * The sound data for a block
     */
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class SoundData {
        public static final SoundData WOOD = new SoundData(SoundEffectType.a);

        @Getter
        @NonNull
        private final SoundEffectType handle;

        public float getVolume() {
            return getHandle().a();
        }

        public float getPitch() {
            return getHandle().b();
        }

        public SoundEffect getStepSound() {
            return getHandle().d();
        }

        public static SoundData getFromBlock(Block block) {
            return new SoundData(getSoundEffectType(block));
        }
    }

    /**
     * Get the entities sideways movement.
     * <p>Positive numbers move to the left and negative to the right.</p>
     *
     * @param entity the entity to get the movement of
     * @return the sideways movement
     */
    public static float getSidewaysMotion(EntityLiving entity) {
        return entity.bd;
    }

    /**
     * Get the entities forward movement.
     * <p>Negative numbers will move backwards.</p>
     *
     * @param entity the entity to get the movement of
     * @return the forward movement
     */
    public static float getForwardsMotion(EntityLiving entity) {
        return entity.be;
    }

    //
    // Utilities for bukkit objects
    //

    // convertors

    public static final ImmutableMap<SoundEffect, Sound> BUKKIT_SOUNDS;
    static {
        ImmutableMap.Builder<SoundEffect, Sound> soundsBuilder = ImmutableMap.builder();
        for (Sound bukkitSound : Sound.values()) {
            SoundEffect mojangSound = fromBukkit(bukkitSound);
            soundsBuilder.put(mojangSound, bukkitSound);
        }
        BUKKIT_SOUNDS = soundsBuilder.build();
    }
    public static Sound toBukkit(SoundEffect mojangEffect) {
        Sound bukkitEffect = BUKKIT_SOUNDS.get(mojangEffect);
        if (bukkitEffect == null) throw new AssertionError("No bukkit sound effect for mojang effect: " + mojangEffect);
        return bukkitEffect;
    }

    public static SoundEffect fromBukkit(Sound bukkit) {
        return bukkit == null ? null : CraftSound.getSoundEffect(CraftSound.getSound(bukkit));
    }

    // unwrappers

    public static EntityPlayer getHandle(Player p) {
        if (p instanceof CraftPlayer) {
            return ((CraftPlayer) p).getHandle();
        } else {
            throw new RuntimeException("Players must be CraftPlayers to get handle");
        }
    }

}
