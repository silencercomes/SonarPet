@file:Suppress("NOTHING_TO_INLINE")
package net.techcable.sonarpet.test

import org.junit.AssumptionViolatedException
import kotlin.AssertionError

inline fun assertThat(condition: Boolean) = assertThat(condition) { "Assertion failed!" }

inline fun assertThat(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        fail(lazyMessage())
    }
}

inline fun <reified T> assertNotNull(value: T?): T {
    return assertNotNull(value) {
        "${T::class.java.typeName} was null!"
    }
}

inline fun <T> assertNotNull(value: T?, lazyMessage: () -> String): T {
    if (value == null) {
        fail(lazyMessage())
    } else {
        return value
    }
}

inline fun <reified T: Throwable> assumeNoError(block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        if (e is T) {
            assumptionViolated("${T::class.java.simpleName}: ${e.message}")
        } else {
            // Continue to propagate unexpected exception
            throw e
        }
    }
}

inline fun fail(message: String): Nothing = throw AssertionError(message)

inline fun assumptionViolated(message: String): Nothing = throw AssumptionViolatedException(message)
