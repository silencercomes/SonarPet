package net.techcable.sonarpet.nms.versions.v1_10_R1;

import lombok.*;

import com.google.common.collect.ImmutableList;

import net.minecraft.server.v1_10_R1.Entity;
import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.nms.NMSEntity;
import net.techcable.sonarpet.nms.NMSLivingEntity;

@RequiredArgsConstructor
public class NMSEntityImpl implements NMSEntity {
    @Getter
    private final Entity handle;

    //
    // Deobfuscated methods
    //

    @Override
    public org.bukkit.entity.Entity getBukkitEntity() {
        return handle.getBukkitEntity();
    }

    @Override
    public ImmutableList<NMSEntity> getPassengers() {
        return ImmutableList.copyOf(handle.passengers.stream()
                .map(Entity::getBukkitEntity)
                .map(INMS.getInstance()::wrapEntity)
                .toArray(NMSEntity[]::new));
    }
}
