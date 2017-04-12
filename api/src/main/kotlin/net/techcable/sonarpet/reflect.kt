@file:Suppress("NOTHING_TO_INLINE")

package net.techcable.sonarpet

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