package com.dsh105.echopet.compat.api.entity;

import lombok.*;

import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.PrettyEnum;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.entity.Skeleton;

/**
 * The type of a skeleton.
 *
 * NOTE: The names of these enums must match the corresponding PetData.
 */
@RequiredArgsConstructor
public enum SkeletonType implements PrettyEnum {
    NORMAL(NmsVersion.EARLIEST),
    WITHER(NmsVersion.EARLIEST),
    STRAY(NmsVersion.v1_11_R1);

    private final NmsVersion firstVersion;

    public boolean isSupported() {
        return Versioning.NMS_VERSION.compareTo(firstVersion) >= 0;
    }

    public Skeleton.SkeletonType getBukkitType() {
        switch (this) {
            case NORMAL:
                return Skeleton.SkeletonType.NORMAL;
            case STRAY:
                return Skeleton.SkeletonType.STRAY;
            case WITHER:
                return Skeleton.SkeletonType.WITHER;
            default:
                throw new AssertionError("Unknown skeletonType: " + this);
        }
    }
}
