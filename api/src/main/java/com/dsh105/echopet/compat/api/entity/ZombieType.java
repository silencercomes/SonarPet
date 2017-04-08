package com.dsh105.echopet.compat.api.entity;

import lombok.*;

import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.PrettyEnum;
import net.techcable.sonarpet.utils.Versioning;

/**
 * The type of a zombie.
 *
 * NOTE: The names of these enums must match the corresponding PetData.
 */
@RequiredArgsConstructor
public enum ZombieType implements PrettyEnum {
    VILLAGER(NmsVersion.EARLIEST),
    NORMAL(NmsVersion.EARLIEST),
    HUSK(NmsVersion.v1_11_R1),
    PIGMAN(NmsVersion.EARLIEST);

    private final NmsVersion firstVersion;

    public boolean isSupported() {
        return Versioning.NMS_VERSION.compareTo(firstVersion) >= 0;
    }
}
