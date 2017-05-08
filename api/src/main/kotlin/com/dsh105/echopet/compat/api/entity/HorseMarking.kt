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

package com.dsh105.echopet.compat.api.entity

import net.techcable.sonarpet.utils.completeImmutableEnumMap
import org.bukkit.entity.Horse

enum class HorseMarking constructor(val bukkitStyle: Horse.Style) {
    NONE(Horse.Style.NONE),
    SOCKS(Horse.Style.WHITE),
    WHITE_PATCH(Horse.Style.WHITEFIELD),
    WHITE_SPOTS(Horse.Style.WHITE_DOTS),
    BLACK_SPOTS(Horse.Style.BLACK_DOTS);

    /**
     * Get the internal id of this marking and the given variant
     *
     * @param v the variant to include in the id
     * @return the internal id
     * *
     */
    @Deprecated("magic ids are version specific")
    fun getId(v: HorseVariant): Int {
        return v.bukkitColour.ordinal and 255 or (bukkitStyle.ordinal shl 8)
    }
}

private val BY_BUKKIT = completeImmutableEnumMap<Horse.Style, HorseMarking> { bukkitStyle ->
    enumValues<HorseMarking>().find { it.bukkitStyle == bukkitStyle }
        ?: throw AssertionError("No horse marking for bukkit style: $bukkitStyle")
}

val Horse.Style.sonarMarking: HorseMarking
    get() = BY_BUKKIT[this]
