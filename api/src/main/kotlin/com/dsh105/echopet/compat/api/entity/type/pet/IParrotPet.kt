package com.dsh105.echopet.compat.api.entity.type.pet

import com.dsh105.echopet.compat.api.entity.IAgeablePet
import com.dsh105.echopet.compat.api.entity.IPet
import org.bukkit.entity.Parrot

interface IParrotPet: IAgeablePet {
    var parrotColor: Parrot.Variant
}