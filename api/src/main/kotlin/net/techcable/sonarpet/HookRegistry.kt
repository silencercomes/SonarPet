package net.techcable.sonarpet

import com.dsh105.echopet.compat.api.entity.IEntityPet
import com.dsh105.echopet.compat.api.entity.IPet
import org.bukkit.Location

interface HookRegistry {
    fun registerHookClass(type: EntityHookType, hookClass: Class<out IEntityPet>)
    fun spawnEntity(pet: IPet, hookType: EntityHookType, location: Location): IEntityPet
    fun shutdown()
}