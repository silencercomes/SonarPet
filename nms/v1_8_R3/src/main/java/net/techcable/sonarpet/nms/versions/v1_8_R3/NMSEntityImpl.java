package net.techcable.sonarpet.nms.versions.v1_8_R3;

import lombok.*;

import com.google.common.collect.ImmutableList;

import net.minecraft.server.v1_8_R3.Entity;
import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.nms.NMSEntity;

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
        if (handle.passenger != null) {
            return ImmutableList.of(INMS.getInstance().wrapEntity(handle.passenger.getBukkitEntity()));
        } else {
            return ImmutableList.of();
        }
    }
}
