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

import lombok.*;

import java.util.Optional;
import java.util.UUID;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.HorseArmour;
import com.dsh105.echopet.compat.api.entity.HorseMarking;
import com.dsh105.echopet.compat.api.entity.HorseType;
import com.dsh105.echopet.compat.api.entity.HorseVariant;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityHorsePet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityAgeablePet;

import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Blocks;
import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;
import org.bukkit.entity.Horse;

@EntitySize(width = 1.4F, height = 1.6F)
@EntityPetType(petType = PetType.HORSE)
public class EntityHorsePet extends EntityAgeablePet implements IEntityHorsePet {

    public static final DataWatcherObject<Byte> HORSE_FLAGS_METADATA = NMS.createMetadata(12, NMS.MetadataType.BYTE);
    public static final DataWatcherObject<Integer> HORSE_TYPE_METADATA = NMS.createMetadata(13, NMS.MetadataType.VAR_INT);
    public static final DataWatcherObject<Integer> HORSE_COLOR_AND_STYLE_METADATA = NMS.createMetadata(14, NMS.MetadataType.VAR_INT);
    public static final DataWatcherObject<UUID> HORSE_OWNER_METADATA = NMS.createMetadata(15, NMS.MetadataType.OPTIONAL_UUID);
    public static final DataWatcherObject<Integer> HORSE_ARMOR_METADATA = NMS.createMetadata(16, NMS.MetadataType.VAR_INT);

    private int rearingCounter = 0;
    int stepSoundCount = 0;

    public EntityHorsePet(World world) {
        super(world);
    }

    public EntityHorsePet(World world, IPet pet) {
        super(world, pet);
    }

    @Override
    public void setSaddled(boolean flag) {
        this.setHorseFlag(HorseFlag.SADDLED, flag);
    }

    @Override
    public void setType(HorseType t) {
        if (t != HorseType.NORMAL) {
            this.setArmour(HorseArmour.NONE);
        }
        this.datawatcher.set(HORSE_TYPE_METADATA, NMS.getId(t.getBukkitVariant()));
    }

    @Override
    public void setVariant(HorseVariant v, HorseMarking m) {
        this.datawatcher.set(HORSE_TYPE_METADATA, NMS.getId(v, m));
    }

    @Override
    public void setArmour(HorseArmour a) {
        this.datawatcher.set(HORSE_ARMOR_METADATA, NMS.getId(a));
    }

    @Override
    public void setChested(boolean flag) {
        this.setHorseFlag(HorseFlag.HAS_CHEST, flag);
    }

    @Override
    public boolean attack(Entity entity) {
        boolean flag = super.attack(entity);
        if (flag) {
            setHorseFlag(HorseFlag.REARING, true);
            switch (getType()) {
                case DONKEY:
                    playSound(Sound.ENTITY_DONKEY_ANGRY, 1, 1);
                    break;
                default:
                    playSound(Sound.ENTITY_HORSE_ANGRY, 1, 1);
                    break;
            }
        }
        return flag;
    }

    @RequiredArgsConstructor
    @Getter
    public enum HorseFlag {
        SADDLED(0x04),
        HAS_CHEST(0x08),
        EATING(0x20),
        REARING(0x40),
        MOUTH_OPEN(0x80);

        private final int id;
    }

    private void setHorseFlag(HorseFlag flag, boolean value) {
        int bitFlags = this.datawatcher.get(HORSE_FLAGS_METADATA);

        if (value) {
            bitFlags |= flag.getId();
        } else {
            bitFlags &= ~flag.getId();
        }
        datawatcher.set(HORSE_FLAGS_METADATA, (byte) bitFlags);
    }

    public Horse.Variant getType() {
        return NMS.variantById(this.datawatcher.get(HORSE_TYPE_METADATA));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        datawatcher.register(HORSE_FLAGS_METADATA, 0);
        datawatcher.register(HORSE_TYPE_METADATA, NMS.getId(Horse.Variant.HORSE));
        datawatcher.register(HORSE_COLOR_AND_STYLE_METADATA, NMS.getId(Horse.Color.WHITE, Horse.Style.NONE));
        datawatcher.register(HORSE_OWNER_METADATA, Optional.of(getOwner().getUniqueID()));
        datawatcher.register(HORSE_ARMOR_METADATA, NMS.getId(HorseArmour.NONE));
    }

    @Override
    protected Sound getIdleSound() {
        switch (this.getType()) {
            case DONKEY:
                return Sound.ENTITY_DONKEY_AMBIENT;
            case HORSE:
                return Sound.ENTITY_HORSE_AMBIENT;
            case MULE:
                return Sound.ENTITY_MULE_AMBIENT;
            case SKELETON_HORSE:
                return Sound.ENTITY_SKELETON_AMBIENT;
            case UNDEAD_HORSE:
                return Sound.ENTITY_SKELETON_AMBIENT;
            default:
                throw new AssertionError("No idle sound for type: " + getType());
        }
    }

    @Override
    protected void makeStepSound(int i, int j, int k, Block block) {
        NMS.SoundData soundData = NMS.SoundData.getFromBlock(block);

        if (this.world.getType(new BlockPosition(i, j + 1, k)) == Blocks.SNOW) {
            soundData = NMS.SoundData.getFromBlock(Blocks.SNOW);
        }

        if (!block.getBlockData().getMaterial().isLiquid()) {

            if (!this.passengers.isEmpty() && getType() != Horse.Variant.DONKEY && getType() != Horse.Variant.MULE) {
                ++this.stepSoundCount;
                if (this.stepSoundCount > 5 && this.stepSoundCount % 3 == 0) {
                    this.playSound(Sound.ENTITY_HORSE_GALLOP, soundData.getVolume() * 0.15F, soundData.getPitch());
                    if (getType() == Horse.Variant.HORSE && this.random.nextInt(10) == 0) {
                        this.playSound(Sound.ENTITY_HORSE_BREATHE, soundData.getVolume() * 0.6F, soundData.getPitch());
                    }
                } else if (this.stepSoundCount <= 5) {
                    this.playSound(Sound.ENTITY_HORSE_STEP_WOOD, soundData.getVolume() * 0.15F, soundData.getPitch());
                }
            } else if (soundData.equals(NMS.SoundData.WOOD)) {
                this.playSound(Sound.ENTITY_HORSE_STEP_WOOD, soundData.getVolume() * 0.15F, soundData.getPitch());
            } else {
                this.playSound(Sound.ENTITY_HORSE_STEP, soundData.getVolume() * 0.15F, soundData.getPitch());
            }
        }
    }

    @Override
    public void g(float sideMot, float forwMot) {
        super.g(sideMot, forwMot);
        if (forwMot <= 0.0F) {
            this.stepSoundCount = 0;
        }
    }

    @Override
    protected Sound getDeathSound() {
        switch (getType()) {
            case DONKEY:
                return Sound.ENTITY_DONKEY_DEATH;
            case HORSE:
                return Sound.ENTITY_HORSE_DEATH;
            case MULE:
                return Sound.ENTITY_MULE_DEATH;
            case SKELETON_HORSE:
                return Sound.ENTITY_SKELETON_HORSE_DEATH;
            case UNDEAD_HORSE:
                return Sound.ENTITY_ZOMBIE_HORSE_DEATH;
            default:
                throw new AssertionError("No death sound for type: " + getType());
        }
    }

    @Override
    public SizeCategory getSizeCategory() {
        if (this.isBaby()) {
            return SizeCategory.TINY;
        } else {
            return SizeCategory.LARGE;
        }
    }

    @Override
    public void onLive() {
        super.onLive();
        if (rearingCounter > 0 && ++rearingCounter > 20) {
            setHorseFlag(HorseFlag.REARING, false);
        }
    }

    @Override
    protected void doJumpAnimation() {
        this.playSound(Sound.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
        this.rearingCounter = 1;
        setHorseFlag(HorseFlag.REARING, true);
    }
}
