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

import com.dsh105.echopet.compat.api.entity.*;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityBlazePet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataType;

import net.minecraft.server.v1_9_R1.World;
import net.minecraft.server.v1_9_R1.DataWatcherObject;

import org.bukkit.Sound;

@EntitySize(width = 0.6F, height = 1.7F)
@EntityPetType(petType = PetType.BLAZE)
public class EntityBlazePet extends EntityPet implements IEntityBlazePet {

    public static MetadataKey<Byte> BLAZE_ON_FIRE_METADATA = new MetadataKey<>(11, MetadataType.BYTE);

    public EntityBlazePet(World world) {
        super(world);
    }

    public EntityBlazePet(World world, IPet pet) {
        super(world, pet);
    }

    @Override
    public void setOnFire(boolean flag) {
        getDatawatcher().set(BLAZE_ON_FIRE_METADATA, (byte) (flag ? 1 : 0));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(BLAZE_ON_FIRE_METADATA, (byte) 0);
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_BLAZE_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.REGULAR;
    }
}
