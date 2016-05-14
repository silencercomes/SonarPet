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
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySpiderPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataType;

import net.minecraft.server.v1_9_R2.DataWatcherObject;
import net.minecraft.server.v1_9_R2.World;

import org.bukkit.Sound;

@EntitySize(width = 1.4F, height = 0.9F)
@EntityPetType(petType = PetType.SPIDER)
public class EntitySpiderPet extends EntityPet implements IEntitySpiderPet {

    public static final MetadataKey<Byte> SPIDER_IS_CLIMBING_METADATA = new MetadataKey<>(11, MetadataType.BYTE);

    public EntitySpiderPet(World world) {
        super(world);
    }

    public EntitySpiderPet(World world, IPet pet) {
        super(world, pet);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(SPIDER_IS_CLIMBING_METADATA, (byte) 0);
    }

    @Override
    protected void makeStepSound() {
        playSound(Sound.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_SPIDER_AMBIENT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SPIDER_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.REGULAR;
    }
}
