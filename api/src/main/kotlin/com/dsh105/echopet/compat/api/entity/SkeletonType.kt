package com.dsh105.echopet.compat.api.entity

import lombok.*

import net.techcable.sonarpet.utils.NmsVersion
import net.techcable.sonarpet.utils.PrettyEnum
import net.techcable.sonarpet.utils.Versioning

import org.bukkit.entity.Skeleton

/**
 * The type of a skeleton.

 * NOTE: The names of these enums must match the corresponding PetData.
 */
@RequiredArgsConstructor
enum class SkeletonType(val minimumVersion: NmsVersion = NmsVersion.EARLIEST) : PrettyEnum {
    NORMAL,
    WITHER,
    STRAY(minimumVersion = NmsVersion.v1_11_R1);

    val isSupported: Boolean
        get() = Versioning.NMS_VERSION >= minimumVersion

    /**
     * The bukkit type of this skeleton, or null if unsupported.
     */
    @Suppress("DEPRECATION")
    val bukkitType: Skeleton.SkeletonType?
        get() {
            if (isSupported) {
                return when (this) {
                    NORMAL -> Skeleton.SkeletonType.NORMAL
                    STRAY -> Skeleton.SkeletonType.STRAY
                    WITHER -> Skeleton.SkeletonType.WITHER
                }
            } else {
                return null
            }
        }
}
