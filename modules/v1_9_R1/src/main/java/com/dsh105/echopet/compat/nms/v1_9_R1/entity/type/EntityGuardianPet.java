package com.dsh105.echopet.compat.nms.v1_9_R1.entity.type;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityGuardianPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataType;

import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;

@EntitySize(width = 0.85F, height = 0.85F)
@EntityPetType(petType = PetType.GUARDIAN)
public class EntityGuardianPet extends EntityPet implements IEntityGuardianPet {

    public static final MetadataKey<Byte> GUARDIAN_FLAGS_METADATA = new MetadataKey<>(11, MetadataType.BYTE);
    public static final MetadataKey<Integer> TARGET_ENTITY_ID_METADATA = new MetadataKey<>(12, MetadataType.VAR_INT);

    public EntityGuardianPet(World world) {
        super(world);
    }

    public EntityGuardianPet(World world, IPet pet) {
        super(world, pet);
    }

    @Override
    protected Sound getIdleSound() {
        // Different ambient sounds for if we are are land or are an elder guardian
        if (isElder()) {
            return isInWater() ? Sound.ENTITY_ELDER_GUARDIAN_AMBIENT : Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND;
        } else {
            return isInWater() ? Sound.ENTITY_GUARDIAN_AMBIENT : Sound.ENTITY_GUARDIAN_AMBIENT_LAND;
        }
    }

    @Override
    protected Sound getDeathSound() {
        // Different death sounds for if we are are land or are an elder guardian
        if (isElder()) {
            return isInWater() ? Sound.ENTITY_ELDER_GUARDIAN_DEATH : Sound.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
        } else {
            return isInWater() ? Sound.ENTITY_GUARDIAN_DEATH : Sound.ENTITY_GUARDIAN_DEATH_LAND;
        }
    }

    @Override
    public SizeCategory getSizeCategory() {
        return isElder() ? SizeCategory.GIANT : SizeCategory.LARGE;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(GUARDIAN_FLAGS_METADATA, (byte) 0);
        getDatawatcher().register(TARGET_ENTITY_ID_METADATA, 0);
    }

    @Override
    public void onLive() {
        super.onLive();
    }

    @Override
    public boolean isElder() {
        return (getDatawatcher().get(GUARDIAN_FLAGS_METADATA) & 0x04) != 0;
    }

    @Override
    public void setElder(boolean flag) {
        int i = 4;
        byte existing = getDatawatcher().get(GUARDIAN_FLAGS_METADATA);

        getDatawatcher().set(GUARDIAN_FLAGS_METADATA, (byte) (flag ? (existing | i) : (existing & (~i))));
    }
}
