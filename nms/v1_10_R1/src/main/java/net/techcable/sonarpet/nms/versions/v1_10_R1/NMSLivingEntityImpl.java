package net.techcable.sonarpet.nms.versions.v1_10_R1;

import net.minecraft.server.v1_10_R1.DamageSource;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.SoundEffect;
import net.techcable.sonarpet.nms.DataWatcher;
import net.techcable.sonarpet.nms.NMSLivingEntity;
import net.techcable.sonarpet.utils.reflection.SonarField;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_10_R1.CraftSound;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NMSLivingEntityImpl extends NMSEntityImpl implements NMSLivingEntity {

    //
    // !!!!! Highly version-dependent !!!!!
    // Check these every minor update!
    //
    public static final String IS_JUMPING_FIELD_NAME = "be";

    public NMSLivingEntityImpl(EntityLiving handle) {
        super(handle);
    }

    /**
     * Set the 'offsets' for pitch and yaw to the same value as the yaw itself.
     * Apparently this is needed to set rotation.
     * See EntityLiving.h(FF) for details (method profiler 'headTurn').
     * Note that EntityInsentient overrides h(FF) and delegates to 'EntityAIBodyControl'.
     * 'EntityAIBodyControl' is what actually accesses/uses these fields and where the mappings should be fetched.
     * <p>
     * Also, these fields have the MCP names 'renderYawOffset' and 'rotationYawHead'
     */
    @Override
    public void correctYaw() {
        getHandle().aO = getHandle().aQ = getHandle().yaw;
    }

    @Override
    public float getSidewaysMotion() {
        return getHandle().bf;
    }

    @Override
    public float getForwardsMotion() {
        return getHandle().bg;
    }

    @Override
    public boolean isInLava() {
        return getHandle().aO();
    }

    //
    // Breakage likely, check for bugs here
    //

    @Override
    public void setMoveSpeed(double rideSpeed) {
        getHandle().l((float) rideSpeed);
    }

    @Override
    public void setStepHeight(float stepHeight) {
        getHandle().P = stepHeight;
    }


    //
    // Unlikely to break, even across major versions
    // IE: never broken yet ^_^
    //

    @Override
    public void playSound(Sound bukkitSound, float volume, float pitch) {
        SoundEffect sound = CraftSound.getSoundEffect(CraftSound.getSound(bukkitSound));
        getHandle().a(sound, volume, pitch);
    }

    @Override
    public double distanceTo(Entity other) {
        return getHandle().h(((CraftEntity) other).getHandle());
    }


    //
    // Deobfuscated methods :)
    //

    @Override
    public Player findNearbyPlayer(double range) {
        EntityHuman player = getHandle().world.findNearbyPlayer(getHandle(), range);
        return player == null ? null : (Player) player.getBukkitEntity();
    }

    @Override
    public boolean isInvisible() {
        return getHandle().isInvisible();
    }

    @Override
    public boolean isSneaking() {
        return getHandle().isSneaking();
    }

    @Override
    public void setSneaking(boolean b) {
        getHandle().setSneaking(b);
    }

    @Override
    public void setInvisible(boolean b) {
        getHandle().setInvisible(b);
    }

    @Override
    public boolean isSprinting() {
        return getHandle().isSprinting();
    }

    @Override
    public void setSprinting(boolean b) {
        getHandle().setSprinting(b);
    }

    @Override
    public void setYaw(float yaw) {
        getHandle().yaw = yaw;
    }

    @Override
    public void setPitch(float pitch) {
        getHandle().pitch = pitch;
    }

    @Override
    public void setNoClip(boolean b) {
        getHandle().noclip = b;
    }

    @Override
    public boolean isInWater() {
        return getHandle().isInWater();
    }


    @Override
    public void setUpwardsMotion(double motY) {
        getHandle().motY = motY;
    }

    @Override
    public DataWatcher getDataWatcher() {
        return new DataWatcherImpl(getHandle().getDataWatcher());
    }

    @Override
    public double getWidth() {
        return getHandle().width;
    }

    @Override
    public double getLength() {
        return getHandle().length;
    }

    //
    // Utility methods and wrappers
    //

    private static final SonarField<Boolean> IS_JUMPING_FIELD = SonarField.getField(EntityLiving.class, IS_JUMPING_FIELD_NAME, boolean.class);

    @Override
    public boolean isJumping() {
        return IS_JUMPING_FIELD.getValue(getHandle());
    }

    @Override
    public EntityLiving getHandle() {
        return (EntityLiving) super.getHandle();
    }

    @Override
    public LivingEntity getBukkitEntity() {
        return (LivingEntity) getHandle().getBukkitEntity();
    }
}
