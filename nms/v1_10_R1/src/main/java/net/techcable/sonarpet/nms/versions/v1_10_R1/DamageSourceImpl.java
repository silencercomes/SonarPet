package net.techcable.sonarpet.nms.versions.v1_10_R1;

import lombok.*;

import net.minecraft.server.v1_10_R1.DamageSource;

@RequiredArgsConstructor
public class DamageSourceImpl implements net.techcable.sonarpet.nms.DamageSource {
    @Getter
    private final DamageSource handle;
}
