@file:Suppress("NOTHING_TO_INLINE")
package net.techcable.sonarpet.utils

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet

/**
 * Capture a snapshot of this collection as a raw array.
 *
 * This method is a kotlin wrapper around [java.util.Collection.toArray], which is otherwise hidden.
 */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
inline fun Collection<*>.toRawArray(): Array<Any?> = (this as java.util.Collection<*>).toArray()

/**
 * Convert a snapshot of this collection into an array with the given transformation.
 *
 * This method is thread safe, since it captures a snapshot of the collection with [toRawArray].
 * @param transform the transformation to apply
 */
inline fun <T, reified R> Collection<T>.mapToArray(transform: (T) -> R): Array<R> {
    return toRawArray().mapToArray {
        @Suppress("UNCHECKED_CAST") // We had to use a raw array
        transform(it as T)
    }
}

inline fun <T, reified R> Iterable<T>.mapToArray(transform: (T) -> R) = iterator().mapToArray(transform)

inline fun <T, reified R> Iterator<T>.mapToArray(transform: (T) -> R): Array<R> {
    var result = emptyArray<R?>()
    var resultSize = 0
    while (hasNext()) {
        val resultElement = transform(next())
        if (resultSize < result.size) {
            result[resultSize++] = resultElement
        } else {
            result = result.copyOf(resultSize + 16)
            result[resultSize++] = resultElement
        }
    }
    @Suppress("UNCHECKED_CAST") // Known to be non-null up to resultSize
    return result.copyOf(resultSize) as Array<R>
}

inline fun <reified T> Iterator<T>.toTypedArray(): Array<T> = this.mapToArray { it }

inline fun <T, reified R> Array<T>.mapToArray(transform: (T) -> R): Array<R> {
    return Array(size) { transform(this[it]) }
}

inline fun <T, R> Array<T>.mapToImmutableSet(transform: (T) -> R): ImmutableSet<R> {
    @Suppress("UNCHECKED_CAST") // Erasure :(
    return when (size) {
        0 -> immutableSetOf()
        1 -> immutableSetOf(transform(this[0]))
        else -> ImmutableSet.copyOf(mapToArray<T, Any?>(transform)) as ImmutableSet<R>
    }
}

inline fun <T, R> Array<T>.mapToImmutableList(transform: (T) -> R): ImmutableList<R> {
    @Suppress("UNCHECKED_CAST") // Erasure :(
    return when (size) {
        0 -> immutableListOf()
        1 -> immutableListOf(transform(this[0]))
        else -> ImmutableList.copyOf(mapToArray<T, Any?>(transform)) as ImmutableList<R>
    }
}

inline fun <T: Any> Array<T?>.requireNoNulls() = this.requireNoNulls { "Null value at index $it" }

inline fun <T: Any> Array<T?>.requireNoNulls(lazyMessage: (Int) -> String): Array<T> {
    this.forEachIndexed { index, value ->
        requireNotNull(value) { lazyMessage(index) }
    }
    @Suppress("UNCHECKED_CAST") // We just checked ;)
    return this as Array<T>
}

/**
 * Consider that this array may have null values,
 * even if the compiler thinks it doesn't.
 */
inline fun <T: Any> Array<T>.mayHaveNulls(): Array<T?> {
    @Suppress("UNCHECKED_CAST") // It's always safe to be careful ;)
    return this as Array<T?>
}
