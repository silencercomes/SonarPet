package net.techcable.sonarpet.nms.entity.generators

import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin
import com.google.common.collect.ImmutableList
import net.techcable.sonarpet.utils.Versioning
import org.objectweb.asm.Type
import net.techcable.sonarpet.utils.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type.*

/**
 * Some entity specific logic is kept in the living update/motion method, like enderdragon destruction.
 * This generator overrides the default (entity-specific) update logic, with a minimalistic alternative.
 */
class CustomMotionPetGenerator(plugin: IEchoPetPlugin, currentType: Type, hookClass: Class<*>, entityClass: Class<*>) : EntityPetGenerator(plugin, currentType, hookClass, entityClass) {
    override val generatedMethods: ImmutableList<GeneratedMethod>
        get() = super.generatedMethods + GeneratedMethod(livingUpdateMethod) {
            // TODO: Fix the bug with these pets not actually moving (maybe something to do with a missing setPosition?)
            // this.doTick()
            loadThis()
            invokeVirtual("doTick", ENTITY_LIVING_TYPE, VOID_TYPE)
            val sidewaysMotionField = Versioning.NMS_VERSION.getObfuscatedField("ENTITY_SIDEWAYS_MOTION_FIELD")
            val forwardMotionField = Versioning.NMS_VERSION.getObfuscatedField("ENTITY_FORWARD_MOTION_FIELD")
            val upwardsMotionField = Versioning.NMS_VERSION.tryGetObfuscatedField("ENTITY_UPWARDS_MOTION_FIELD")
            // this.moveStrafing *= 0.98
            // this.moveForward *= 0.98
            // this.moveUpwards *= 0.9
            val movementUpdates = mutableMapOf(
                    sidewaysMotionField to 0.98f,
                    forwardMotionField to 0.98f
            )
            // NOTE: Upwards motion is only present post 1.12 and is multiplied by 0.9 not 0.98
            upwardsMotionField?.let { movementUpdates.put(it, 0.9f) }
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
            getField(ENTITY_LIVING_TYPE, sidewaysMotionField, FLOAT_TYPE)
            loadThis()
            getField(ENTITY_LIVING_TYPE, forwardMotionField, FLOAT_TYPE)
            upwardsMotionField?.let {
                loadThis()
                getField(ENTITY_LIVING_TYPE, upwardsMotionField, FLOAT_TYPE)
            }
            invokeVirtual(
                    name = entityMoveMethodName,
                    ownerType = ENTITY_LIVING_TYPE,
                    parameterTypes = entityMoveMethodParameters.toList()
            )
        }

    // Version specific method names
    val livingUpdateMethod = Versioning.NMS_VERSION.getObfuscatedMethod("LIVING_UPDATE_METHOD")
}