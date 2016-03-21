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
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWitherPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataType;

import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;

@EntitySize(width = 0.9F, height = 4.0F)
@EntityPetType(petType = PetType.WITHER)
public class EntityWitherPet extends EntityPet implements IEntityWitherPet {

    public EntityWitherPet(World world) {
        super(world);
    }

    public EntityWitherPet(World world, IPet pet) {
        super(world, pet);
    }

    public static final MetadataKey<Integer> WITHER_FIRST_HEAD_TARGET_METADATA = new MetadataKey<>(11, MetadataType.VAR_INT);
    public static final MetadataKey<Integer> WITHER_SECOND_HEAD_TARGET_METADATA = new MetadataKey<>(12, MetadataType.VAR_INT);
    public static final MetadataKey<Integer> WITHER_THIRD_HEAD_TARGET_METADATA = new MetadataKey<>(13, MetadataType.VAR_INT);
    public static final MetadataKey<Integer> WITHER_INVULNERABLE_METADATA = new MetadataKey<>(13, MetadataType.VAR_INT);


    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(WITHER_FIRST_HEAD_TARGET_METADATA, 0);
        getDatawatcher().register(WITHER_SECOND_HEAD_TARGET_METADATA, 0);
        getDatawatcher().register(WITHER_THIRD_HEAD_TARGET_METADATA, 0);
        getDatawatcher().register(WITHER_INVULNERABLE_METADATA, 0);;
    }

    public void setShielded(boolean flag) {
        getDatawatcher().register(WITHER_INVULNERABLE_METADATA, (flag ? 1 : 0));
        this.setHealth((float) (flag ? 150 : 300));
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_WITHER_AMBIENT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_WITHER_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.LARGE;
    }
}
