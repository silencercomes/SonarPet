package net.techcable.sonarpet.nms

import net.techcable.sonarpet.utils.NmsVersion
import org.objectweb.asm.Type

// Method names
val NmsVersion.livingUpdateMethod: String
    get() = getObfuscatedMethod("LIVING_UPDATE_METHOD")

val NmsVersion.entityTickMethodName
    get() = getObfuscatedMethod("ENTITY_TICK_METHOD")
val NmsVersion.entityMoveMethodName
    get() = getObfuscatedMethod("ENTITY_MOVE_METHOD")
val NmsVersion.onStepMethodName
    get() = getObfuscatedMethod("ON_STEP_METHOD")
val NmsVersion.onInteractMethodName
    get() = getObfuscatedMethod("ON_INTERACT_METHOD")
val NmsVersion.proceduralAIMethodName
    get() = getObfuscatedMethod("ENTITY_PROCEDURAL_AI_METHOD")
val NmsVersion.rabbitSetMovementSpeed
    get() = getObfuscatedMethod("RABBIT_SET_MOVEMENT_SPEED")

// Field names

val NmsVersion.sidewaysMotionField
    get() = getObfuscatedField("ENTITY_SIDEWAYS_MOTION_FIELD")
val NmsVersion.forwardMotionField
    get() = getObfuscatedField("ENTITY_FORWARD_MOTION_FIELD")
val NmsVersion.upwardsMotionField: String?
    get() = tryGetObfuscatedField("ENTITY_UPWARDS_MOTION_FIELD")


// Other
/**
 * Before 1.12, the entity move method accepted two floats for both sideways and forwards direction.
 * After 1.12, it also accepts an additional float for up/down movement, giving it three paramteers in total.
 */
val NmsVersion.entityMoveMethodParameters: Array<Type>
    get() {
        val amount = if (this >= NmsVersion.v1_12_R1) 3 else 2
        return Array(amount) { Type.FLOAT_TYPE }
    }
