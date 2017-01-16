package net.techcable.sonarpet.nms;

import com.google.common.collect.ImmutableList;

import org.bukkit.entity.Entity;

public interface NMSEntity {
    Entity getBukkitEntity();

    ImmutableList<NMSEntity> getPassengers();

    boolean damageEntity(DamageSource damageSource, float amount);
}
