package net.techcable.sonarpet.nms;

import net.techcable.sonarpet.SafeSound;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface NMSLivingEntity extends NMSEntity {
    LivingEntity getBukkitEntity();

    boolean damageEntity(DamageSource damageSource, float amount);

    boolean isInWater();

    boolean isInLava();

    double distanceTo(Entity other);

    default void playSound(SafeSound sound, float volume, float pitch) {
        playSound(sound.getBukkitSound(), volume, pitch);
    }

    void playSound(Sound bukkitSound, float volume, float pitch);

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

    float getSidewaysMotion();

    float getForwardsMotion();

    void setMoveSpeed(double rideSpeed);

    boolean isJumping();

    void setUpwardsMotion(double motY);

    Player findNearbyPlayer(double range);

    void setNoClip(boolean b);

    DataWatcher getDataWatcher();

    double getWidth();

    double getLength();
}
