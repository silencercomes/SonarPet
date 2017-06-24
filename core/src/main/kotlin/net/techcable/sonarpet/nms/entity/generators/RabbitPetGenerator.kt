package net.techcable.sonarpet.nms.entity.generators

import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin
import com.google.common.collect.ImmutableList
import net.techcable.sonarpet.nms.rabbitSetMovementSpeed
import net.techcable.sonarpet.utils.Versioning.*
import net.techcable.sonarpet.utils.plus
import org.objectweb.asm.Type

class RabbitPetGenerator(plugin: IEchoPetPlugin, currentType: Type, hookClass: Class<*>, entityClass: Class<*>) : EntityPetGenerator(plugin, currentType, hookClass, entityClass) {
    override val generatedMethods: ImmutableList<GeneratedMethod>
        // Disable the setMovementSpeed method so rabbit movement is never disabled
        get() {
            return super.generatedMethods + GeneratedMethod.noOp(
                    NMS_VERSION.rabbitSetMovementSpeed,
                    parameterTypes = listOf(Type.DOUBLE_TYPE),
                    invokeCheckSanity = false
            )
        }
}
