package net.techcable.sonarpet.test

import org.junit.AssumptionViolatedException
import org.junit.ComparisonFailure
import kotlin.AssertionError
import kotlin.reflect.KClass

fun assertThat(condition: Boolean) = assertThat(condition) { "Assertion failed!" }

inline fun assertThat(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        fail(lazyMessage())
    }
}

fun assertMatches(pattern: Regex, value: String) {
    assertMatches(pattern, value) { "'$value' doesn't match $pattern" }
}
inline fun assertMatches(pattern: Regex, value: String, lazyMessage: () -> String) {
    assertNotNull(pattern.matchEntire(value), lazyMessage = lazyMessage)
}

fun assertEqual(expected: Any?, actual: Any?) {
    assertEqual(expected, actual) { "Expected $expected, but got $actual" }
}
inline fun assertEqual(expected: Any?, actual: Any?, lazyMessage: (actual: Any?) -> String) {
    if (expected != actual) {
        // NOTE: Only invoke lazyMessage once to avoid code bloat
        val message = lazyMessage(actual)
        if (expected is String && actual is String) {
            throw ComparisonFailure(message, expected, actual)
        } else {
            throw AssertionError(message)
        }
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

fun assumeThat(condition: Boolean) {
    assumeThat(condition) { "Assumption violated!" }
}
inline fun assumeThat(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) throw AssumptionViolatedException(lazyMessage())
}

inline fun assumeNoErrors(vararg errorTypes: KClass<out Throwable>, block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        val matchedType = errorTypes.find { it.java.isInstance(e) }
        if (matchedType != null) {
            assumptionViolated("${matchedType.java.simpleName}: ${e.message}")
        } else {
            // Continue to propagate unexpected exception
            throw e
        }
    }
}
inline fun <reified T: Throwable> assumeNoError(block: () -> Unit) {
    assumeNoErrors(T::class, block = block)
}

@Suppress("NOTHING_TO_INLINE")
inline fun fail(message: String): Nothing = throw AssertionError(message)

@Suppress("NOTHING_TO_INLINE")
inline fun assumptionViolated(message: String): Nothing = throw AssumptionViolatedException(message)
