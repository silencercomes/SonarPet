package com.dsh105.echopet.api.pet.type

import com.dsh105.echopet.api.pet.Pet
import com.dsh105.echopet.compat.api.entity.EntityPetType
import com.dsh105.echopet.compat.api.entity.PetType
import com.dsh105.echopet.compat.api.entity.type.pet.IParrotPet
import org.bukkit.entity.Parrot
import org.bukkit.entity.Player

@EntityPetType(petType = PetType.PARROT)
class ParrotPet(owner: Player): Pet(owner), IParrotPet {
    override fun setBaby(flag: Boolean) {
        if (flag) {
            craftPet.setBaby()
        } else {
            craftPet.setAdult()
        }
    }

    override fun isBaby() = !craftPet.isAdult

    override var parrotColor: Parrot.Variant
        get() = craftPet.variant
        set(value) {
            craftPet.variant = value
        }

    override fun getCraftPet() = super.getCraftPet() as Parrot
}