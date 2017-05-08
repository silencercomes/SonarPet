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

enum class HorseVariant constructor(val bukkitColour: Horse.Color) {

    WHITE(Horse.Color.WHITE),
    CREAMY(Horse.Color.CREAMY),
    CHESTNUT(Horse.Color.CHESTNUT),
    BROWN(Horse.Color.BROWN),
    BLACK(Horse.Color.BLACK),
    GRAY(Horse.Color.GRAY),
    DARKBROWN(Horse.Color.DARK_BROWN);
}

private val BY_BUKKIT = completeImmutableEnumMap<Horse.Color, HorseVariant> { bukkitColor ->
    enumValues<HorseVariant>().find { variant -> variant.bukkitColour == bukkitColor }
            ?: throw AssertionError("No horse marking for bukkit color: $bukkitColor")
}

val Horse.Color.sonarVariant: HorseVariant
    get() = BY_BUKKIT[this]
