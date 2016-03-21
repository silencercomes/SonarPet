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

package com.dsh105.echopet.compat.nms.v1_9_R1.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySkeletonPet;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataType;

import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.Items;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;
import org.bukkit.entity.Skeleton;
import org.bukkit.scheduler.BukkitRunnable;

@EntitySize(width = 0.6F, height = 1.9F)
@EntityPetType(petType = PetType.SKELETON)
public class EntitySkeletonPet extends EntityPet implements IEntitySkeletonPet {

    public static final MetadataKey<Integer> SKELETON_TYPE_METADATA = new MetadataKey<>(11, MetadataType.VAR_INT);
    public static final MetadataKey<Boolean> SKELETON_IS_TARGETING = new MetadataKey<>(12, MetadataType.BOOLEAN);

    public EntitySkeletonPet(World world) {
        super(world);
    }

    public EntitySkeletonPet(World world, final IPet pet) {
        super(world, pet);
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (getSkeletonType()) {
                    case NORMAL:
                        setEquipment(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
                        break;
                    case WITHER:
                        setEquipment(EnumItemSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                        break;
                    default:
                        throw new AssertionError("Unknown skeleton type: " + getSkeletonType());
                }
            }
        }.runTaskLater(EchoPet.getPlugin(), 5L);
    }

    @Override
    public void setWither(boolean flag) {
        setSkeletonType(flag ? Skeleton.SkeletonType.WITHER : Skeleton.SkeletonType.NORMAL);
    }


    public Skeleton.SkeletonType getSkeletonType() {
        return Skeleton.SkeletonType.getType(getDatawatcher().get(SKELETON_TYPE_METADATA));
    }

    public void setSkeletonType(Skeleton.SkeletonType type) {
        getDatawatcher().set(SKELETON_TYPE_METADATA, type.getId());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(SKELETON_TYPE_METADATA, Skeleton.SkeletonType.NORMAL.getId());
        getDatawatcher().register(SKELETON_IS_TARGETING, false);
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected void makeStepSound() {
        this.playSound(Sound.ENTITY_SKELETON_STEP, 0.15F, 1.0F);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SKELETON_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        switch (getSkeletonType()) {
            case NORMAL:
                return SizeCategory.REGULAR;
            case WITHER:
                return SizeCategory.LARGE;
            default:
                throw new AssertionError("Unknown skeleton type: " + getSkeletonType());
        }
    }
}
