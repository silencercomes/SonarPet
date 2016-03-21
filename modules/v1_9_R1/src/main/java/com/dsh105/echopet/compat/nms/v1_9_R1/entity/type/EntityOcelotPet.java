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

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.EntitySize;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityOcelotPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityAgeablePet;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataKey;
import com.dsh105.echopet.compat.nms.v1_9_R1.metadata.MetadataType;

import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;
import org.bukkit.entity.Ocelot;

@EntitySize(width = 0.6F, height = 0.8F)
@EntityPetType(petType = PetType.OCELOT)
public class EntityOcelotPet extends EntityAgeablePet implements IEntityOcelotPet {

    public static final MetadataKey<Byte> OCELOT_FLAGS_METADATA = new MetadataKey<>(12, MetadataType.BYTE);
    public static final MetadataKey<Optional<UUID>> OCELOT_OWNER_METADATA = new MetadataKey<>(13, MetadataType.OPTIONAL_UUID);
    public static final MetadataKey<Integer> OCELOT_TYPE_METADATA = new MetadataKey<>(14, MetadataType.VAR_INT);

    public EntityOcelotPet(World world) {
        super(world);
    }

    public EntityOcelotPet(World world, IPet pet) {
        super(world, pet);
        getDatawatcher().set(OCELOT_FLAGS_METADATA, (byte) 0x04); // set tame
        getDatawatcher().set(OCELOT_OWNER_METADATA, Optional.of(pet.getOwnerUUID()));
    }

    public int getCatType() {
        return getDatawatcher().get(OCELOT_TYPE_METADATA);
    }

    @Override
    public void setCatType(int i) {
        getDatawatcher().set(OCELOT_TYPE_METADATA, i);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        getDatawatcher().register(OCELOT_FLAGS_METADATA, (byte) 0);
        getDatawatcher().register(OCELOT_OWNER_METADATA, Optional.empty());
        getDatawatcher().register(OCELOT_TYPE_METADATA, Ocelot.Type.BLACK_CAT.getId());
    }

    @Override
    protected void makeStepSound() {
        //this.makeSound("mob.ozelot.step", 0.15F, 1.0F); // TODO
    }

    @Override
    protected Sound getIdleSound() {
        return (this.random.nextInt(4) == 0 ? Sound.ENTITY_CAT_PURREOW : Sound.ENTITY_CAT_AMBIENT); // Play puring sounds instead of default mojang sounds
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_CAT_DEATH;
    }
}
