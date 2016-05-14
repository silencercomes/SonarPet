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
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySheepPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityAgeablePet;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataType;

import net.minecraft.server.v1_9_R2.World;

import org.bukkit.Sound;

@EntitySize(width = 0.9F, height = 1.3F)
@EntityPetType(petType = PetType.SHEEP)
public class EntitySheepPet extends EntityAgeablePet implements IEntitySheepPet {

    public static final MetadataKey<Byte> SHEEP_STATUS_METADATA = new MetadataKey<>(12, MetadataType.BYTE);

    public EntitySheepPet(World world) {
        super(world);
    }

    public EntitySheepPet(World world, IPet pet) {
        super(world, pet);
    }

    public int getColor() {
        return getDatawatcher().get(SHEEP_STATUS_METADATA) & 0xF;
    }

    @Override
    public void setColor(int i) {
        byte b = getDatawatcher().get(SHEEP_STATUS_METADATA);

        b = (byte) ((b & 0xF0) | (i & 0xF));

        getDatawatcher().set(SHEEP_STATUS_METADATA, b);
    }

    public boolean isSheared() {
        return (getDatawatcher().get(SHEEP_STATUS_METADATA) & 0x10) == 0x10;
    }

    @Override
    public void setSheared(boolean flag) {
        byte b = getDatawatcher().get(SHEEP_STATUS_METADATA);
        getDatawatcher().set(SHEEP_STATUS_METADATA, (byte) (flag ? (b | 0x10) : (b & ~0x10)));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(SHEEP_STATUS_METADATA, (byte) 0);
    }

    @Override
    protected void makeStepSound() {
        this.playSound(Sound.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_SHEEP_AMBIENT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SHEEP_DEATH;
    }
}
