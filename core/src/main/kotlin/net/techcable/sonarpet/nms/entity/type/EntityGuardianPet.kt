package net.techcable.sonarpet.nms.entity.type

import com.dsh105.echopet.compat.api.entity.IPet
import com.dsh105.echopet.compat.api.entity.SizeCategory
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityGuardianPet
import net.techcable.sonarpet.EntityHook
import net.techcable.sonarpet.EntityHookType
import net.techcable.sonarpet.nms.NMSInsentientEntity
import net.techcable.sonarpet.nms.entity.EntityInsentientPet
import net.techcable.sonarpet.nms.entity.generators.CustomMotionPetGenerator
import net.techcable.sonarpet.nms.entity.generators.GeneratorClass
import net.techcable.sonarpet.utils.NmsVersion
import net.techcable.sonarpet.utils.Versioning
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.Guardian

@EntityHook(EntityHookType.GUARDIAN, EntityHookType.ELDER_GUARDIAN)
@GeneratorClass(CustomMotionPetGenerator::class)
class EntityGuardianPet internal constructor(pet: IPet, entity: NMSInsentientEntity, hookType: EntityHookType) : EntityInsentientPet(pet, entity, hookType), IEntityGuardianPet {

    override fun getSizeCategory(): SizeCategory {
        return if (isElder) SizeCategory.GIANT else SizeCategory.LARGE
    }

    @Suppress("DEPRECATION")
    override fun isElder(): Boolean {
        if (Versioning.NMS_VERSION >= NmsVersion.v1_11_R1) {
            return bukkitEntity is ElderGuardian
        } else {
            return bukkitEntity.isElder
        }
    }

    override fun getBukkitEntity(): Guardian {
        return super.getBukkitEntity() as Guardian
    }
}
