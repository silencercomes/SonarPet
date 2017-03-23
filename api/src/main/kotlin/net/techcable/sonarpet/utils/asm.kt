@file:Suppress("NOTHING_TO_INLINE") // I know what i'm doing
package net.techcable.sonarpet.utils

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import kotlin.reflect.KClass

val Class<*>.internalName: String
    inline get() = Type.getInternalName(this)

val KClass<*>.internalName: String
    inline get() = this.java.internalName

val Class<*>.asmType: Type
    inline get() = Type.getType(this)

val KClass<*>.asmType: Type
    inline get() = this.java.asmType

fun MethodVisitor.invokeVirtual(
        name: String,
        ownerType: Type,
        returnType: Type = VOID_TYPE,
        parameterTypes: List<Type> = listOf()
) {
    val desc = getMethodDescriptor(returnType, *parameterTypes.toTypedArray())
    return visitMethodInsn(INVOKEVIRTUAL, ownerType.internalName, name, desc, false)
}
