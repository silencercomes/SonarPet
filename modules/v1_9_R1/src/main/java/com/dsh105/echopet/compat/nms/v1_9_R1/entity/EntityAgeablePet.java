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

package com.dsh105.echopet.compat.nms.v1_9_R1.entity;

import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataType;

import net.minecraft.server.v1_9_R1.World;

public abstract class EntityAgeablePet extends EntityPet {

    public static final MetadataKey<Boolean> IS_BABY_METADATA = new MetadataKey<>(11, MetadataType.BOOLEAN);

    private int age = 0;
    private boolean ageLocked = true;

    public EntityAgeablePet(World world) {
        super(world);
    }

    public EntityAgeablePet(World world, IPet pet) {
        super(world, pet);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        getDatawatcher().set(IS_BABY_METADATA, age < 0);
    }

    public boolean isAgeLocked() {
        return ageLocked;
    }

    public void setAgeLocked(boolean ageLocked) {
        this.ageLocked = ageLocked;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(IS_BABY_METADATA, false);
    }

    @Override
    public void m() {
        super.m();
        if (!(this.world.isClientSide || this.ageLocked)) {
            int i = this.getAge();

            if (i < 0) {
                ++i;
                this.setAge(i);
            } else if (i > 0) {
                --i;
                this.setAge(i);
            }
        }
    }

    public void setBaby(boolean flag) {
        if (isBaby() == flag) return;
        if (flag) {
            setAge(-24000); // Taken from craftbukkit ;)
        } else {
            setAge(0);
        }
    }

    @Override
    public boolean isBaby() {
        return getDatawatcher().get(IS_BABY_METADATA);
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
