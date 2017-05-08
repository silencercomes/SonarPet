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

package com.dsh105.echopet.api.pet.type;

import com.dsh105.echopet.api.pet.Pet;
import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.ZombieType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityZombiePet;
import com.dsh105.echopet.compat.api.entity.type.pet.IZombiePet;

import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

@EntityPetType(petType = PetType.ZOMBIE)
public class ZombiePet extends Pet implements IZombiePet {

    boolean baby = false;
    private ZombieType zombieType = ZombieType.NORMAL;

    public ZombiePet(Player owner) {
        super(owner);
    }

    @Override
    public void setBaby(boolean flag) {
        ((IEntityZombiePet) getEntityPet()).setBaby(flag);
        this.baby = flag;
    }

    @Override
    public boolean isBaby() {
        return this.baby;
    }

    @Override
    public ZombieType getZombieType() {
        return zombieType;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setZombieType(ZombieType newType) {
        if (newType == this.zombieType) return;
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            final EntityHookType hookType;
            switch (newType) {
                case NORMAL:
                    hookType = EntityHookType.ZOMBIE;
                    break;
                case HUSK:
                    hookType = EntityHookType.HUSK_ZOMBIE;
                    break;
                case VILLAGER:
                    hookType = EntityHookType.VILLAGER_ZOMBIE;
                    break;
                case PIGMAN:
                    hookType = EntityHookType.PIG_ZOMBIE;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown zombie type: " + newType);
            }
            switchHookType(getOwner(), hookType);
        } else {
            if (this.getZombieType() == ZombieType.PIGMAN && newType != ZombieType.PIGMAN) {
                // Switch to a normal zombie
                switchHookType(getOwner(), EntityHookType.ZOMBIE);
            }
            switch (newType) {
                case PIGMAN:
                    // Pigmen are considered a separate entity type, even on old versions
                    switchHookType(getOwner(), EntityHookType.PIG_ZOMBIE);
                    break;
                case NORMAL:
                    ((Zombie) getEntityPet().getBukkitEntity()).setVillager(false);
                    break;
                case VILLAGER:
                    ((Zombie) getEntityPet().getBukkitEntity()).setVillager(true);
                default:
                    throw new IllegalArgumentException("Unknown zombie type: " + newType);
            }
        }
        this.zombieType = newType;
    }
}
