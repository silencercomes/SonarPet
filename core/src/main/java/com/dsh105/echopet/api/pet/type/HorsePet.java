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

package com.dsh105.echopet.api.pet.type;

import lombok.*;

import java.lang.invoke.MethodHandle;

import com.dsh105.echopet.api.pet.Pet;
import com.dsh105.echopet.compat.api.entity.*;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityHorsePet;
import com.dsh105.echopet.compat.api.entity.type.pet.IHorsePet;

import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

@EntityPetType(petType = PetType.HORSE)
public class HorsePet extends Pet implements IHorsePet {

    HorseType horseType = HorseType.NORMAL;
    HorseVariant variant = HorseVariant.WHITE;
    HorseMarking marking = HorseMarking.NONE;
    HorseArmour armour = HorseArmour.NONE;
    boolean baby = false;
    boolean chested = false;
    boolean saddle = false;

    public HorsePet(Player owner) {
        super(owner);
    }

    // We have to do this with reflection since the API broke in 1.11 -_-
    @SuppressWarnings("deprecation")
    private static final MethodHandle SET_VARIANT_METHOD = Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) < 0 ?
            Reflection.getMethod(Horse.class, "setVariant", Horse.Variant.class) : null;
    @Override
    @SneakyThrows
    public void setHorseType(HorseType newType) {
        if (newType != HorseType.NORMAL) {
            this.setArmour(HorseArmour.NONE);
        }
        if (Versioning.NMS_VERSION.compareTo(NmsVersion.v1_11_R1) >= 0) {
            final EntityHookType hookType;
            switch (newType) {
                case NORMAL:
                    hookType = EntityHookType.HORSE;
                    break;
                case DONKEY:
                    hookType = EntityHookType.DONKEY;
                    break;
                case MULE:
                    hookType = EntityHookType.MULE;
                    break;
                case ZOMBIE:
                    hookType = EntityHookType.ZOMBIE_HORSE;
                    break;
                case SKELETON:
                    hookType = EntityHookType.SKELETON_HORSE;
                    break;
                case LLAMA:
                    hookType = EntityHookType.LLAMA;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown horse type: " + newType);
            }
            switchHookType(getOwner(), hookType);
        } else {
            SET_VARIANT_METHOD.invoke(getEntityPet().getBukkitEntity(), newType);
        }
        this.horseType = newType;
    }

    @Override
    public void setVariant(HorseVariant variant, HorseMarking marking) {
        ((IEntityHorsePet) getEntityPet()).setVariant(variant, marking);
        this.variant = variant;
        this.marking = marking;
    }

    @Override
    public void setArmour(HorseArmour armour) {
        ((IEntityHorsePet) getEntityPet()).setArmour(armour);
        this.armour = armour;
    }

    @Override
    public boolean isBaby() {
        return this.baby;
    }

    @Override
    public void setBaby(boolean flag) {
        ((IEntityHorsePet) getEntityPet()).setBaby(flag);
        this.baby = flag;
    }

    @Override
    public void setSaddled(boolean flag) {
        ((IEntityHorsePet) getEntityPet()).setSaddled(flag);
        this.saddle = flag;
    }

    @Override
    public void setChested(boolean flag) {
        ((IEntityHorsePet) getEntityPet()).setChested(flag);
        this.chested = flag;
    }

    @Override
    public HorseType getHorseType() {
        return this.horseType;
    }

    @Override
    public HorseVariant getVariant() {
        return this.variant;
    }

    @Override
    public HorseMarking getMarking() {
        return this.marking;
    }

    @Override
    public HorseArmour getArmour() {
        return this.armour;
    }

    @Override
    public boolean isSaddled() {
        return this.saddle;
    }

    @Override
    public boolean isChested() {
        return this.chested;
    }
}