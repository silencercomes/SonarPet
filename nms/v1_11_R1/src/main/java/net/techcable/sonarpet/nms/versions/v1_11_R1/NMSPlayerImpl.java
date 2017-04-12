package net.techcable.sonarpet.nms.versions.v1_11_R1;

import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.techcable.sonarpet.nms.NMSPlayer;

import org.bukkit.entity.Player;

public class NMSPlayerImpl extends NMSLivingEntityImpl implements NMSPlayer {
    public NMSPlayerImpl(EntityPlayer handle) {
        super(handle);
    }

    @Override
    public boolean isOnGround() {
        return getHandle().onGround;
    }

    @Override
    public EntityPlayer getHandle() {
        return (EntityPlayer) super.getHandle();
    }

    @Override
    public Player getBukkitEntity() {
        return (Player) super.getBukkitEntity();
    }
}
