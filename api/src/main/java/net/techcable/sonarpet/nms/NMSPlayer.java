package net.techcable.sonarpet.nms;

import org.bukkit.entity.Player;

public interface NMSPlayer extends NMSLivingEntity {
    @Override
    Player getBukkitEntity();

    /**
     * Do a raw Entity.onGround check instead of craftbukkit's voodo.
     */
    boolean isOnGround();
}
