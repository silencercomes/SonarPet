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
import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntitySlimePet;
import com.dsh105.echopet.compat.api.util.Perm;
import com.dsh105.echopet.compat.nms.v1_9_R2.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataType;

import net.minecraft.server.v1_9_R2.World;

import org.bukkit.Sound;

@EntitySize(width = 0.6F, height = 0.6F)
@EntityPetType(petType = PetType.SLIME)
public class EntitySlimePet extends EntityPet implements IEntitySlimePet {

    public static final MetadataKey<Integer> SIME_SIZE_METADATA = new MetadataKey<>(11, MetadataType.VAR_INT);

    int jumpDelay;

    public EntitySlimePet(World world) {
        super(world);
    }

    public EntitySlimePet(World world, IPet pet) {
        super(world, pet);
        if (!Perm.hasDataPerm(pet.getOwner(), false, pet.getPetType(), PetData.MEDIUM, false)) {
            if (!Perm.hasDataPerm(pet.getOwner(), false, pet.getPetType(), PetData.SMALL, false)) {
                this.setSize(4);
            } else {
                this.setSize(1);
            }
        } else {
            this.setSize(2);
        }
        this.jumpDelay = this.random.nextInt(15) + 10;
    }

    @Override
    public void setSize(int i) {
        getDatawatcher().set(SIME_SIZE_METADATA, i);
        EntitySize es = this.getClass().getAnnotation(EntitySize.class);
        this.setSize(es.width() * (float) i, es.height() * (float) i);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.setHealth(this.getMaxHealth());
    }

    public int getSize() {
        return getDatawatcher().get(SIME_SIZE_METADATA);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(SIME_SIZE_METADATA, 1);
    }

    @Override
    protected Sound getIdleSound() {
        return null;
    }

    @Override
    protected Sound getDeathSound() {
        return (this.getSize() > 1 ? Sound.ENTITY_SLIME_DEATH : Sound.ENTITY_SMALL_SLIME_DEATH);
    }

    @Override
    public void onLive() {
        super.onLive();

        if (this.onGround && this.jumpDelay-- <= 0) {
            this.jumpDelay = this.random.nextInt(15) + 10;
            this.playSound(this.getDeathSound(), NMS.getVolume(this), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            NMS.jump(this);
        }
    }

    @Override
    public SizeCategory getSizeCategory() {
        switch (getSize()) {
            case 1:
                return SizeCategory.TINY;
            case 4:
                return SizeCategory.LARGE;
            default:
                return SizeCategory.REGULAR;
        }
    }
}
