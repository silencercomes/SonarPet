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

import java.util.UUID;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityWolfPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityAgeablePet;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataType;
import com.google.common.base.Optional;

import net.minecraft.server.v1_9_R2.EnumParticle;
import net.minecraft.server.v1_9_R2.MathHelper;
import net.minecraft.server.v1_9_R2.World;

import org.bukkit.DyeColor;
import org.bukkit.Sound;

@EntitySize(width = 0.6F, height = 0.8F)
@EntityPetType(petType = PetType.WOLF)
public class EntityWolfPet extends EntityAgeablePet implements IEntityWolfPet {


    public static final MetadataKey<Byte> WOLF_FLAGS_METADATA = new MetadataKey<>(12, MetadataType.BYTE);
    public static final MetadataKey<Optional<UUID>> WOLF_OWNER_METADATA = new MetadataKey<>(13, MetadataType.OPTIONAL_UUID);
    public static final MetadataKey<Float> WOLF_DAMAGE_TAKEN_METADATA = new MetadataKey<>(14, MetadataType.FLOAT);
    public static final MetadataKey<Boolean> WOLF_IS_BEGGING_METADATA = new MetadataKey<>(15, MetadataType.BOOLEAN);
    public static final MetadataKey<Integer> WOLF_COLLAR_COLOR_METADATA = new MetadataKey<>(16, MetadataType.VAR_INT);


    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(WOLF_FLAGS_METADATA, (byte) 0); // Default to not tamed
        getDatawatcher().register(WOLF_OWNER_METADATA, Optional.absent());
        getDatawatcher().register(WOLF_DAMAGE_TAKEN_METADATA, 0F);
        getDatawatcher().register(WOLF_IS_BEGGING_METADATA, false);
        getDatawatcher().register(WOLF_COLLAR_COLOR_METADATA, 0); // White colar
    }

    private boolean wet;
    private boolean shaking;
    private float shakeCount;

    public EntityWolfPet(World world) {
        super(world);
    }

    public EntityWolfPet(World world, IPet pet) {
        super(world, pet);
        setTamed(true); // Tame
    }

    public boolean isTamed() {
        return (getDatawatcher().get(WOLF_FLAGS_METADATA) & 0x04) == 0x04;
    }

    @Override
    public void setTamed(boolean flag) {
        byte b = getDatawatcher().get(WOLF_FLAGS_METADATA);

        getDatawatcher().set(WOLF_FLAGS_METADATA, (byte) (flag ? (b | 0x04) : (b & ~0x04)));
        getDatawatcher().set(WOLF_OWNER_METADATA, flag ? Optional.of(getPlayerOwner().getUniqueId()) : Optional.absent());

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

        byte b = getDatawatcher().get(WOLF_FLAGS_METADATA);

        getDatawatcher().set(WOLF_FLAGS_METADATA, (byte) (flag ? (b | 0x02) : (b & ~0x02)));
    }

    public boolean isAngry() {
        return (getDatawatcher().get(WOLF_FLAGS_METADATA) & 0x02) != 0;
    }

    @Override
    public void setCollarColor(DyeColor dc) {
        getDatawatcher().set(WOLF_COLLAR_COLOR_METADATA, (int) dc.getDyeData());
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
            if (this.isTamed() && getDatawatcher().get(WOLF_DAMAGE_TAKEN_METADATA) < 10) {
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
