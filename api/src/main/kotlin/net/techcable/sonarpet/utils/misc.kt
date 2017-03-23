@file:Suppress("NOTHING_TO_INLINE") // I know what i'm doing ;)
package net.techcable.sonarpet.utils

//
// Miscellaneous Utilities
//

object SystemProperties {
    inline fun get(name: String): String? = System.getProperty(name)

    fun getInt(name: String): Int? {
        try {
            return get(name)?.toInt()
        } catch (_: NumberFormatException) {
            invalidProperty(name, "integer")
        }
    }

    fun getBoolean(name: String): Boolean? {
        return when (get(name)) {
            null -> null
            "true" -> true
            "false" -> false
            else -> invalidProperty(name, "boolean")
        }
    }

    private fun invalidProperty(name: String, type: String): Nothing {
        val value = get(name)!!
        throw IllegalArgumentException("$name is not a valid $type: $value")
    }
}
