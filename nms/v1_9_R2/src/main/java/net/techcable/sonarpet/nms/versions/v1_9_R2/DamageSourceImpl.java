package net.techcable.sonarpet.nms.versions.v1_9_R2;

import lombok.*;

import net.minecraft.server.v1_9_R2.DamageSource;

@RequiredArgsConstructor
public class DamageSourceImpl implements net.techcable.sonarpet.nms.DamageSource {
    @Getter
    private final DamageSource handle;
}
