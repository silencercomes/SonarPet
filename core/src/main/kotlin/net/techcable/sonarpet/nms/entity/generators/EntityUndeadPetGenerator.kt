package net.techcable.sonarpet.nms.entity.generators

import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin
import com.google.common.collect.ImmutableList
import org.objectweb.asm.Type
import net.techcable.sonarpet.utils.*
import org.objectweb.asm.Type.*

class EntityUndeadPetGenerator(plugin: IEchoPetPlugin, currentType: Type, hookClass: Class<*>, entityClass: Class<*>) : EntityPetGenerator(plugin, currentType, hookClass, entityClass) {
    override val generatedMethods: ImmutableList<GeneratedMethod>
        // Disable setOnFire to prevent the pet from burning
        get() = super.generatedMethods + GeneratedMethod.noOp("setOnFire", parameterTypes = listOf(INT_TYPE))
}