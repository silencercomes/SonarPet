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
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityBatPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityPet;

import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.MathHelper;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;

@EntitySize(width = 0.5F, height = 0.9F)
@EntityPetType(petType = PetType.BAT)
public class EntityBatPet extends EntityPet implements IEntityBatPet {

    public static final DataWatcherObject<Byte> BAT_HANGING_METADATA = NMS.createMetadata(11, NMS.MetadataType.BYTE);

    public EntityBatPet(World world) {
        super(world);
    }

    public EntityBatPet(World world, IPet pet) {
        super(world, pet);
    }

    @Override
    public void setHanging(boolean flag) {
        byte var2 = this.datawatcher.get(BAT_HANGING_METADATA);
        if(flag) {
            this.datawatcher.set(BAT_HANGING_METADATA, (byte) (var2 | 1));
        } else {
            this.datawatcher.set(BAT_HANGING_METADATA, (byte) (var2 & -2));
        }
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(BAT_HANGING_METADATA, (byte) 0);
    }

    @Override
    protected Sound getIdleSound() {
        return this.isAsleep() && this.random.nextInt(4) != 0 ? null : Sound.ENTITY_BAT_DEATH;
    }

    @Override
    public void onLive() {
        super.onLive();
        if (this.isAsleep()) {
            this.motX = this.motY = this.motZ = 0.0D;
            this.locY = (double) MathHelper.floor(this.locY) + 1.0D - (double) this.length;
        } else {
            this.motY *= 0.6000000238418579D;
        }
    }

    public boolean isAsleep() {
        return (this.datawatcher.get(BAT_HANGING_METADATA) & 1) != 0;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_BAT_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.TINY;
    }
}
