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

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityZombiePet;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataType;

import net.minecraft.server.v1_9_R2.EnumItemSlot;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.Items;
import net.minecraft.server.v1_9_R2.World;

import org.bukkit.Sound;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

@EntitySize(width = 0.6F, height = 1.8F)
@EntityPetType(petType = PetType.ZOMBIE)
public class EntityZombiePet extends EntityPet implements IEntityZombiePet {

    public static final MetadataKey<Boolean> ZOMBIE_IS_BABY_METADATA = new MetadataKey<>(11, MetadataType.BOOLEAN);
    public static final MetadataKey<Integer> ZOMBIE_VILLAGER_METADATA = new MetadataKey<>(12, MetadataType.VAR_INT);
    public static final MetadataKey<Boolean> ZOMBIE_IS_CONVERTING_METADATA = new MetadataKey<>(13, MetadataType.BOOLEAN);
    public static final MetadataKey<Boolean> ZOMBIE_HAS_HANDS_UP_METADATA = new MetadataKey<>(15, MetadataType.BOOLEAN);



    public EntityZombiePet(World world) {
        super(world);
    }

    public EntityZombiePet(World world, IPet pet) {
        super(world, pet);
        new BukkitRunnable() {
            @Override
            public void run() {
                setEquipment(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
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
        return Sound.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected void makeStepSound() {
        this.playSound(Sound.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ZOMBIE_DEATH;
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
