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

package com.dsh105.echopet.api.pet.type

import com.dsh105.echopet.api.pet.Pet
import com.dsh105.echopet.compat.api.entity.EntityPetType
import com.dsh105.echopet.compat.api.entity.PetType
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityGuardianPet
import com.dsh105.echopet.compat.api.entity.type.pet.IGuardianPet
import net.techcable.sonarpet.EntityHookType
import net.techcable.sonarpet.utils.NmsVersion
import net.techcable.sonarpet.utils.Versioning
import org.bukkit.entity.Guardian
import org.bukkit.entity.Player

@EntityPetType(petType = PetType.GUARDIAN)
class GuardianPet(owner: Player) : Pet(owner), IGuardianPet {

    override var elder: Boolean
        get() = (entityPet as IEntityGuardianPet).isElder
        set(flag) {
            if (Versioning.NMS_VERSION >= NmsVersion.v1_11_R1) {
                switchHookType(owner, if (flag) EntityHookType.ELDER_GUARDIAN else EntityHookType.GUARDIAN)
            } else {
                @Suppress("DEPRECATION")
                (entityPet.bukkitEntity as Guardian).isElder = true
            }
        }
}