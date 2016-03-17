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

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.SizeCategory;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityEnderDragonPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityNoClipPet;

import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.DragonControllerPhase;
import net.minecraft.server.v1_9_R1.EntityComplexPart;
import net.minecraft.server.v1_9_R1.IComplex;
import net.minecraft.server.v1_9_R1.IMonster;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;

@EntitySize(width = 16.0F, height = 8.0F)
@EntityPetType(petType = PetType.ENDERDRAGON)
public class EntityEnderDragonPet extends EntityNoClipPet implements IComplex, IMonster, IEntityEnderDragonPet {

    public static final DataWatcherObject<Integer> DRAGON_PHASE_METADATA = NMS.createMetadata(11, NMS.MetadataType.VAR_INT);

    public EntityEnderDragonPet(World world) {
        super(world);
    }

    // TODO: logic


    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        datawatcher.register(DRAGON_PHASE_METADATA, DragonControllerPhase.k.b()); // set the dragon phase to the 'hover' phase
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ENDERDRAGON_GROWL;
    }

    @Override
    protected Sound getIdleSound() {
        return Sound.ENTITY_ENDERDRAGON_AMBIENT;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.GIANT;
    }

    @Override
    public World a() {
        return world;
    }

    @Override
    public boolean a(EntityComplexPart entityComplexPart, DamageSource damageSource, float v) {
        return true; // Tacos
    }
}
