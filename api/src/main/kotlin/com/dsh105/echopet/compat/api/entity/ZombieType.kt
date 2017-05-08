package com.dsh105.echopet.compat.api.entity

import lombok.*

import net.techcable.sonarpet.utils.NmsVersion
import net.techcable.sonarpet.utils.PrettyEnum
import net.techcable.sonarpet.utils.Versioning

/**
 * The type of a zombie.

 * NOTE: The names of these enums must match the corresponding PetData.
 */
@RequiredArgsConstructor
enum class ZombieType(val minimumVersion: NmsVersion = NmsVersion.EARLIEST) : PrettyEnum {
    VILLAGER,
    NORMAL,
    HUSK(minimumVersion = NmsVersion.v1_11_R1),
    PIGMAN;

    val isSupported: Boolean
        get() = Versioning.NMS_VERSION >= minimumVersion
}
