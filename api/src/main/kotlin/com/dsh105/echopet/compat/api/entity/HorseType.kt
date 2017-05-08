/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package com.dsh105.echopet.compat.api.entity


import net.techcable.sonarpet.utils.NmsVersion
import net.techcable.sonarpet.utils.PrettyEnum
import net.techcable.sonarpet.utils.Versioning
import net.techcable.sonarpet.utils.completeImmutableEnumMap


import org.bukkit.entity.Horse

enum class HorseType constructor(val minimumVersion: NmsVersion = NmsVersion.EARLIEST) : PrettyEnum {
    NORMAL,
    DONKEY,
    MULE,
    LLAMA(minimumVersion = NmsVersion.v1_11_R1),
    ZOMBIE,
    SKELETON;

    /**
     * Get the internal id of the horse type,
     * assuming that it is supported.
     *
     * @return the internal id
     */
    val id: Int
        @Deprecated("Magic ids are version specific")
        get() = assumeBukkitVariant().ordinal

    /**
     * Get the bukkit variant of this horse, assuming that it [isSupported].
     *
     * @throws IllegalStateException
     */
    fun assumeBukkitVariant(): Horse.Variant {
        return bukkitVariant ?: throw IllegalStateException("$this isn't supported on ${Versioning.NMS_VERSION}")
    }

    /**
     * The bukkit variant of this horse.
     */
    val bukkitVariant: Horse.Variant?
        get() {
            if (isSupported) {
                return when (this) {
                    NORMAL -> Horse.Variant.HORSE
                    DONKEY -> Horse.Variant.DONKEY
                    MULE -> Horse.Variant.MULE
                    LLAMA -> Horse.Variant.LLAMA
                    ZOMBIE -> Horse.Variant.UNDEAD_HORSE
                    SKELETON -> Horse.Variant.SKELETON_HORSE
                }
            } else {
                return null
            }
        }

    /**
     * If this horse type is supported on this version.
     */
    val isSupported: Boolean
        get() = Versioning.NMS_VERSION >= minimumVersion
}

private val BY_BUKKIT_VARIANT = completeImmutableEnumMap<Horse.Variant, HorseType> {
    enumValues<HorseType>().find { type -> type.bukkitVariant == it }
            ?: throw AssertionError("No horse marking for bukkit style: $it")
}

val Horse.Variant.sonarType: HorseType
    get() = BY_BUKKIT_VARIANT[this]
