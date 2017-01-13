package com.dsh105.echopet.compat.api.registration;

import java.util.concurrent.Callable;

import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;

import org.bukkit.entity.Player;

public interface PetRegistry {

    PetRegistrationEntry getRegistrationEntry(PetType petType);

    IPet spawn(PetType petType, final Player owner);

    <T> T performRegistration(PetRegistrationEntry registrationEntry, Callable<T> callable);

    Class<?> getPetEntityClass(PetType petType);

    IEntityPet spawnEntity(IPet pet, Player owner);
}
