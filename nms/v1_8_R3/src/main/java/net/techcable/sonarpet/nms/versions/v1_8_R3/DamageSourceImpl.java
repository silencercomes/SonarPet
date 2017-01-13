package net.techcable.sonarpet.nms.versions.v1_8_R3;

import lombok.*;

import net.minecraft.server.v1_8_R3.DamageSource;

@RequiredArgsConstructor
public class DamageSourceImpl implements net.techcable.sonarpet.nms.DamageSource {
    @Getter
    private final DamageSource handle;
}
