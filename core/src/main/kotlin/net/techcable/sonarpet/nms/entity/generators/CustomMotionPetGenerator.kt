package net.techcable.sonarpet.nms.entity.generators

import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin
import com.google.common.collect.ImmutableList
import net.techcable.sonarpet.nms.*
import net.techcable.sonarpet.utils.Versioning.*
import net.techcable.sonarpet.utils.invokeVirtual
import net.techcable.sonarpet.utils.plus
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*

/**
 * Some entity specific logic is kept in the living update/motion method, like enderdragon destruction.
 * This generator overrides the default (entity-specific) update logic, with a minimalistic alternative.
 */
class CustomMotionPetGenerator(plugin: IEchoPetPlugin, currentType: Type, hookClass: Class<*>, entityClass: Class<*>) : EntityPetGenerator(plugin, currentType, hookClass, entityClass) {
    override val generatedMethods: ImmutableList<GeneratedMethod>
        get() = super.generatedMethods + GeneratedMethod(NMS_VERSION.livingUpdateMethod) {
            // TODO: Fix the bug with these pets not actually moving (maybe something to do with a missing setPosition?)
            // this.doTick()
            loadThis()
            invokeVirtual("doTick", ENTITY_LIVING_TYPE, VOID_TYPE)
            // this.moveStrafing *= 0.98
            // this.moveForward *= 0.98
            // this.moveUpwards *= 0.9
            val movementUpdates = mutableMapOf(
                    NMS_VERSION.sidewaysMotionField to 0.98f,
                    NMS_VERSION.forwardMotionField to 0.98f
            )
            // NOTE: Upwards motion is only present post 1.12 and is multiplied by 0.9 not 0.98
            NMS_VERSION.upwardsMotionField?.let { movementUpdates.put(it, 0.9f) }
            for ((fieldName, coefficient) in movementUpdates) {
                loadThis()
                visitInsn(DUP)
                getField(ENTITY_LIVING_TYPE, fieldName, FLOAT_TYPE)
                visitLdcInsn(coefficient)
                visitInsn(FMUL)
                putField(ENTITY_LIVING_TYPE, fieldName, FLOAT_TYPE)
            }
            // this.move(this.moveStrafing, this.moveForward, this.moveUpwards), moveUpwards missing before 1.12
            loadThis()
            visitInsn(DUP)
            getField(ENTITY_LIVING_TYPE, NMS_VERSION.sidewaysMotionField, FLOAT_TYPE)
            loadThis()
            getField(ENTITY_LIVING_TYPE, NMS_VERSION.forwardMotionField, FLOAT_TYPE)
            NMS_VERSION.upwardsMotionField?.let {
                loadThis()
                getField(ENTITY_LIVING_TYPE, it, FLOAT_TYPE)
            }
            invokeVirtual(
                    name = NMS_VERSION.entityMoveMethodName,
                    ownerType = ENTITY_LIVING_TYPE,
                    parameterTypes = NMS_VERSION.entityMoveMethodParameters.toList()
            )
        }
}