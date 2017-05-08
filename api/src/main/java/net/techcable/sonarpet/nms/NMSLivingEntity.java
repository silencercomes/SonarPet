package net.techcable.sonarpet.nms;

import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.sonarpet.SafeSound;
import net.techcable.sonarpet.utils.Versioning;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface NMSLivingEntity extends NMSEntity {
    LivingEntity getBukkitEntity();

    boolean isInWater();

    boolean isInLava();

    double distanceTo(Entity other);

    default void playSound(SafeSound sound, float volume, float pitch) {
        playSound(sound.getNmsSound(), volume, pitch);
    }

    void playSound(NMSSound nmsSound, float volume, float pitch);

    boolean isInvisible();

    boolean isSneaking();

    void setSneaking(boolean b);

    void setInvisible(boolean b);

    boolean isSprinting();

    void setSprinting(boolean b);

    /**
     * Sets Entity.yaw and Entity.lastYaw to the specified value
     */
    void setYaw(float yaw);

    void setStepHeight(float stepHeight);

    /**
     * Correct the magic yaw fields.
     * Highly version-dependent!
     */
    void correctYaw();

    void setPitch(float pitch);

    PineappleField<Object, Float> SIDEWAYS_MOTION_FIELD = PineappleField.create(
            MinecraftReflection.getNmsClass("EntityLiving"),
            Versioning.NMS_VERSION.getObfuscatedField("ENTITY_SIDEWAYS_MOTION_FIELD"),
            float.class
    );
    default float getSidewaysMotion() {
        Object rawEntity = MinecraftReflection.getHandle(getBukkitEntity());
        return SIDEWAYS_MOTION_FIELD.getBoxed(rawEntity);
    }

    PineappleField<Object, Float> FORWARD_MOTION_FIELD = PineappleField.create(
            MinecraftReflection.getNmsClass("EntityLiving"),
            Versioning.NMS_VERSION.getObfuscatedField("ENTITY_FORWARD_MOTION_FIELD"),
            float.class
    );
    default float getForwardsMotion() {
        Object rawEntity = MinecraftReflection.getHandle(getBukkitEntity());
        return FORWARD_MOTION_FIELD.getBoxed(rawEntity);
    }

    void setMoveSpeed(double rideSpeed);

    boolean isJumping();

    void setUpwardsMotion(double motY);

    Player findNearbyPlayer(double range);

    void setNoClip(boolean b);

    DataWatcher getDataWatcher();

    double getWidth();

    double getLength();
}
