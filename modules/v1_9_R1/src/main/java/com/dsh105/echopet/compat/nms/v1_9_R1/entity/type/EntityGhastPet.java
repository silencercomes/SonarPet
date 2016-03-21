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
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityGhastPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataType;

import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;

@EntitySize(width = 4.0F, height = 4.0F)
@EntityPetType(petType = PetType.GHAST)
public class EntityGhastPet extends EntityPet implements IEntityGhastPet {

    public static MetadataKey<Boolean> IS_ATTACKING_METADATA = new MetadataKey<>(11, MetadataType.BOOLEAN);

    public EntityGhastPet(World world) {
        super(world);
    }

    public EntityGhastPet(World world, IPet pet) {
        super(world, pet);
    }

    // TODO: fireballs

    @Override
    protected void initAttributes() {
        super.initAttributes();
        getDatawatcher().register(IS_ATTACKING_METADATA, false);
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_GHAST_AMBIENT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_GHAST_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.OVERSIZE;
    }
}
