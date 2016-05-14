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

import com.dsh105.echopet.compat.api.entity.*;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityEndermanPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.entity.EntityPet;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R2.metadata.MetadataType;
import com.google.common.base.Optional;

import net.minecraft.server.v1_9_R2.DataWatcherObject;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.World;

import org.bukkit.Sound;

@EntitySize(width = 0.6F, height = 2.9F)
@EntityPetType(petType = PetType.ENDERMAN)
public class EntityEndermanPet extends EntityPet implements IEntityEndermanPet {

    public EntityEndermanPet(World world) {
        super(world);
    }

    public EntityEndermanPet(World world, IPet pet) {
        super(world, pet);
    }

    public static final MetadataKey<Optional<IBlockData>> ENDERMAN_CARRIED_BLOCK_METADATA = new MetadataKey<>(11, MetadataType.OPTIONAL_BLOCK_DATA);
    public static final MetadataKey<Boolean> ENDERMAN_IS_SCREAMING_METADATA = new MetadataKey<>(12, MetadataType.BOOLEAN);

    @Override
    public void setScreaming(boolean flag) {
        getDatawatcher().set(ENDERMAN_IS_SCREAMING_METADATA, flag);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(ENDERMAN_CARRIED_BLOCK_METADATA, Optional.absent());
        getDatawatcher().register(ENDERMAN_IS_SCREAMING_METADATA, false);
    }

    @Override
    protected Sound getIdleSound() {
        return this.isScreaming() ? Sound.ENTITY_ENDERMEN_SCREAM : Sound.ENTITY_ENDERMEN_AMBIENT;
    }

    public boolean isScreaming() {
        return getDatawatcher().get(ENDERMAN_IS_SCREAMING_METADATA);
    }

    public void clearCarried() {
        getDatawatcher().set(ENDERMAN_CARRIED_BLOCK_METADATA, Optional.absent());
    }

    public void setCarried(IBlockData blockData) {
        getDatawatcher().set(ENDERMAN_CARRIED_BLOCK_METADATA, Optional.of(blockData));
    }

    public Optional<IBlockData> getCarried() {
        return getDatawatcher().get(ENDERMAN_CARRIED_BLOCK_METADATA);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ENDERMEN_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.REGULAR;
    }
}
