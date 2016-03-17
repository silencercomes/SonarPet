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

import java.util.Optional;
import java.util.UUID;

import com.dsh105.echopet.compat.api.entity.*;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWolfPet;
import com.dsh105.echopet.compat.api.entity.type.pet.IWolfPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityAgeablePet;

import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.EnumColor;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.MathHelper;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.DyeColor;
import org.bukkit.Sound;

@EntitySize(width = 0.6F, height = 0.8F)
@EntityPetType(petType = PetType.WOLF)
public class EntityWolfPet extends EntityAgeablePet implements IEntityWolfPet {


    public static final DataWatcherObject<Byte> WOLF_FLAGS_METADATA = NMS.createMetadata(12, NMS.MetadataType.BYTE);
    public static final DataWatcherObject<Optional<UUID>> WOLF_OWNER_METADATA = NMS.createMetadata(13, NMS.MetadataType.OPTIONAL_UUID);
    public static final DataWatcherObject<Float> WOLF_DAMAGE_TAKEN_METADATA = NMS.createMetadata(14, NMS.MetadataType.VAR_INT);
    public static final DataWatcherObject<Boolean> WOLF_IS_BEGGING_METADATA = NMS.createMetadata(15, NMS.MetadataType.BOOLEAN);
    public static final DataWatcherObject<Integer> WOLF_COLLAR_COLOR_METADATA = NMS.createMetadata(16, NMS.MetadataType.VAR_INT);


    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(WOLF_FLAGS_METADATA, 0x04); // Default to tamed
        this.datawatcher.register(WOLF_OWNER_METADATA, Optional.of(getOwner().getUniqueID()));
        this.datawatcher.register(WOLF_DAMAGE_TAKEN_METADATA, 0);
        this.datawatcher.register(WOLF_IS_BEGGING_METADATA, fallDistance);
        this.datawatcher.register(WOLF_COLLAR_COLOR_METADATA, 0); // White colar
    }

    private boolean wet;
    private boolean shaking;
    private float shakeCount;

    public EntityWolfPet(World world) {
        super(world);
    }

    public EntityWolfPet(World world, IPet pet) {
        super(world, pet);
    }

    public boolean isTamed() {
        return (this.datawatcher.get(WOLF_FLAGS_METADATA) & 0x04) == 0x04;
    }

    @Override
    public void setTamed(boolean flag) {
        byte b = this.datawatcher.get(WOLF_FLAGS_METADATA);

        datawatcher.set(WOLF_FLAGS_METADATA, (byte) (flag ? (b | 0x04) : (b & ~0x04)));

        if (!flag) {
            getPet().getPetData().remove(PetData.TAMED);
        } else if (!getPet().getPetData().contains(PetData.TAMED)) {
            this.getPet().getPetData().add(PetData.TAMED);
        }

        if (isAngry() && flag) {
            setAngry(false);
        }
    }

    @Override
    public void setAngry(boolean flag) {
        if (flag) {
            if (!getPet().getPetData().contains(PetData.ANGRY)) {
                this.getPet().getPetData().add(PetData.ANGRY);
            }
            if (isTamed()) {
                setTamed(false);
            }
        } else {
            getPet().getPetData().remove(PetData.ANGRY);
        }

        byte b = this.datawatcher.get(WOLF_FLAGS_METADATA);

        datawatcher.register(WOLF_FLAGS_METADATA, flag ? (b | 0x02) : (b & ~0x02));
    }

    public boolean isAngry() {
        return (this.datawatcher.get(WOLF_FLAGS_METADATA) & 0x02) != 0;
    }

    @Override
    public void setCollarColor(DyeColor dc) {
        datawatcher.set(WOLF_COLLAR_COLOR_METADATA, (int) dc.getDyeData());
    }

    @Override
    public void onLive() {
        super.onLive();
        if (this.inWater) {
            this.wet = true;
            this.shaking = false;
            this.shakeCount = 0.0F;
        } else if ((this.wet || this.shaking) && this.shaking) {
            if (this.shakeCount == 0.0F) {
                this.playSound(Sound.ENTITY_WOLF_SHAKE, NMS.getVolume(this), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.shakeCount += 0.05F;
            if (this.shakeCount - 0.05F >= 2.0F) {
                this.wet = false;
                this.shaking = false;
                this.shakeCount = 0.0F;
            }

            if (this.shakeCount > 0.4F) {
                float f = (float) this.getBoundingBox().b;
                int i = (int) (MathHelper.sin((this.shakeCount - 0.4F) * 3.1415927F) * 7.0F);

                for (int j = 0; j < i; ++j) {
                    float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;

                    this.world.addParticle(EnumParticle.WATER_SPLASH, this.locX + (double) f1, (double) (f + 0.8F), this.locZ + (double) f2, this.motX, this.motY, this.motZ);
                }
            }
        }
    }

    @Override
    protected Sound getIdleSound() {
        if (this.isAngry()) {
            return Sound.ENTITY_WOLF_GROWL;
        } else if (this.random.nextInt(3) == 0)
            if (this.isTamed() && this.datawatcher.get(WOLF_DAMAGE_TAKEN_METADATA) < 10) {
                return Sound.ENTITY_WOLF_WHINE;
            } else {
                return Sound.ENTITY_WOLF_PANT;
            }
        else {
            return Sound.ENTITY_WOLF_AMBIENT;
        }
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_WOLF_DEATH;
    }

}
