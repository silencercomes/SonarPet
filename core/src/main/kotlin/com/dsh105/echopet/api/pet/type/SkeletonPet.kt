package com.dsh105.echopet.api.pet.type

import com.dsh105.echopet.api.pet.Pet
import com.dsh105.echopet.compat.api.entity.SkeletonType
import com.dsh105.echopet.compat.api.entity.type.pet.ISkeletonPet
import net.techcable.sonarpet.EntityHookType
import net.techcable.sonarpet.utils.NmsVersion
import net.techcable.sonarpet.utils.Versioning.*
import org.bukkit.entity.Player
import org.bukkit.entity.Skeleton

class SkeletonPet(owner: Player): Pet(owner), ISkeletonPet {
    override var skeletonType = SkeletonType.NORMAL
        set(newType) {
            require(newType.isSupported) { "$newType isn't supported on $NMS_VERSION" }
            if (newType == field) return
            if (NMS_VERSION >= NmsVersion.v1_11_R1) {
                val hookType = when (skeletonType) {
                    SkeletonType.NORMAL -> EntityHookType.SKELETON
                    SkeletonType.WITHER -> EntityHookType.WITHER_SKELETON
                    SkeletonType.STRAY -> EntityHookType.STRAY_SKELETON
                }
                switchHookType(owner, hookType)
            } else {
                @Suppress("DEPRECATION") // We did a version check ;)
                (entityPet.bukkitEntity as Skeleton).skeletonType = skeletonType.bukkitType
            }
            field = newType
        }
}