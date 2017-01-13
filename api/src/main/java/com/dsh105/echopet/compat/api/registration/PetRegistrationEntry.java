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

package com.dsh105.echopet.compat.api.registration;

import lombok.*;

import com.dsh105.commodus.StringUtil;
import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.techcable.sonarpet.nms.NMSInsentientEntity;

public class PetRegistrationEntry {

    @Getter
    private final PetType petType;
    private final PetRegistry petRegistry;
    private final String name;
    private final int registrationId;
    private final Class<? extends IPet> petClass;
    private final Class<? extends IEntityPet> hookClass;
    private final Class<?> nmsClass;

    private Constructor<? extends IPet> petConstructor;
    private Constructor<? extends IEntityPet> hookConstructor;

    public PetRegistrationEntry(PetType petType, PetRegistry petRegistry, String name, int registrationId, Class<? extends IPet> petClass, Class<? extends IEntityPet> hookClass, Class<?> nmsClass) {
        if (hookClass == null) {
            throw new PetRegistrationException(name + " isn't supported!");
        }
        this.petType = petType;
        this.petRegistry = petRegistry;
        this.name = name;
        this.registrationId = registrationId;
        this.hookClass = hookClass;
        this.petClass = petClass;
        this.nmsClass = nmsClass;

        try {
            this.petConstructor = this.petClass.getConstructor(Player.class);
            this.hookConstructor = this.hookClass.getDeclaredConstructor(IPet.class, NMSInsentientEntity.class);
            this.hookConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new PetRegistrationException("Failed to create pet constructors!", e);
        }
    }

    public String getName() {
        return name;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public Class<? extends IPet> getPetClass() {
        return petClass;
    }

    public Class<? extends IEntityPet> getHookClass() {
        return hookClass;
    }

    public IPet createFor(Player owner) {
        try {
            return this.petConstructor.newInstance(owner);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create pet object for " + owner.getName(), e);
        }
    }

    public IEntityPet createHookClass(IPet pet, NMSInsentientEntity entity) {
        try {
            return this.hookConstructor.newInstance(pet, entity);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create EntityPet object for " + pet.getOwner().getName(), e);
        }
    }

    public static PetRegistrationEntry create(PetRegistry petRegistry, PetType petType) {
        return new PetRegistrationEntry(
                petType,
                petRegistry,
                StringUtil.capitalise(petType.toString().toLowerCase().replace("_", " ")).replace(" ", "") + "-Pet",
                petType.getRegistrationId(),
                petType.getPetClass(),
                petType.getHookClass(),
                petType.getNmsClass()
        );
    }
}