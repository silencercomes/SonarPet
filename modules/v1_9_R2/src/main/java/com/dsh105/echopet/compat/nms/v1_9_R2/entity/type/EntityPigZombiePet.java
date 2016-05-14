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

package com.dsh105.echopet.compat.nms.v1_9_R2.entity.type;

import com.dsh105.echopet.compat.api.entity.*;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityPigZombiePet;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityPet;

import net.minecraft.server.v1_9_R2.EnumItemSlot;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.Items;
import net.minecraft.server.v1_9_R2.World;

import org.bukkit.Sound;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import static com.dsh105.echopet.compat.nms.v1_9_R2.entity.type.EntityZombiePet.ZOMBIE_IS_BABY_METADATA;
import static com.dsh105.echopet.compat.nms.v1_9_R2.entity.type.EntityZombiePet.ZOMBIE_VILLAGER_METADATA;
import static com.dsh105.echopet.compat.nms.v1_9_R2.entity.type.EntityZombiePet.ZOMBIE_IS_CONVERTING_METADATA;
import static com.dsh105.echopet.compat.nms.v1_9_R2.entity.type.EntityZombiePet.ZOMBIE_HAS_HANDS_UP_METADATA;

@EntitySize(width = 0.6F, height = 1.8F)
@EntityPetType(petType = PetType.PIGZOMBIE)
public class EntityPigZombiePet extends EntityPet implements IEntityPigZombiePet { // NOTE: pig zombies extend zombies!

    public EntityPigZombiePet(World world) {
        super(world);
    }

    public EntityPigZombiePet(World world, IPet pet) {
        super(world, pet);
        new BukkitRunnable() {
            @Override
            public void run() {
                setEquipment(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            }
        }.runTaskLater(EchoPet.getPlugin(), 5L);
    }

    @Override
    public void setBaby(boolean flag) {
        getDatawatcher().set(ZOMBIE_IS_BABY_METADATA, flag);
    }

    @Override
    public void setVillager(boolean flag) {
        setVillagerType(flag ? Villager.Profession.FARMER : null);
    }

    public void setVillagerType(Villager.Profession type) {
        getDatawatcher().set(ZOMBIE_VILLAGER_METADATA, type == null ? 0 : type.getId() + 1);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(ZOMBIE_IS_BABY_METADATA, false);
        getDatawatcher().register(ZOMBIE_VILLAGER_METADATA, 0); // not a villager
        getDatawatcher().register(ZOMBIE_IS_CONVERTING_METADATA, false);
        getDatawatcher().register(ZOMBIE_HAS_HANDS_UP_METADATA, false);
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_ZOMBIE_PIG_AMBIENT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ZOMBIE_PIG_DEATH;

    }

    @Override
    public boolean isBaby() {
        return getDatawatcher().get(ZOMBIE_IS_BABY_METADATA);
    }

    @Override
    public SizeCategory getSizeCategory() {
        if (this.isBaby()) {
            return SizeCategory.TINY;
        } else {
            return SizeCategory.REGULAR;
        }
    }
}
