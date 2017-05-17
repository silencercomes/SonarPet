@file:Suppress("NOTHING_TO_INLINE")

package net.techcable.sonarpet

import net.techcable.pineapple.reflection.Reflection
import java.lang.invoke.MethodHandle
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KClass

fun Class<*>.getDeclaredMethod(name: String, vararg parameterTypes: KClass<*>): Method {
    return this.getDeclaredMethod(name, *parameterTypes.map { it.java }.toTypedArray())
}

fun <T> Class<T>.getDeclaredConstructor(vararg parameterTypes: KClass<*>): Constructor<T> {
    return this.getDeclaredConstructor(*parameterTypes.map { it.java }.toTypedArray())
}

inline operator fun <T> Constructor<out T>.invoke(vararg parameters: Any?): T {
    return this.newInstance(*parameters)
}

fun KClass<*>.getMethodHandle(name: String, vararg parameterTypes: KClass<*>): MethodHandle {
    return this.java.getMethodHandle(name, *parameterTypes.map { it.java }.toTypedArray())
}

fun Class<*>.getMethodHandle(name: String, vararg parameterTypes: Class<*>): MethodHandle {
    return Reflection.getMethod(this, name, *parameterTypes)
}
