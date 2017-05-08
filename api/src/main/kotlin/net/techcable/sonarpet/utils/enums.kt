package net.techcable.sonarpet.utils

import com.google.common.collect.ImmutableMap
import java.util.*

inline fun <reified K: Enum<K>, V> immutableEnumMap(valueFunc: (K) -> V?): ImmutableMap<K, V> {
    val resultBuilder = EnumMap<K, V>(K::class.java)
    for (key in enumValues<K>()) {
        val value = valueFunc(key)
        if (value != null) {
            resultBuilder.put(key, value)
        }
    }
    // ImmutableMap.copyOf special-cases EnumMap into a highly optimized 'ImmutableEnumMap'
    return ImmutableMap.copyOf(resultBuilder)
}
inline fun <reified K: Enum<K>, V: Any> completeImmutableEnumMap(valueFunc: (K) -> V): CompleteEnumMap<K, V> {
    val keys = enumValues<K>()
    val values = Array<Any>(keys.size) { valueFunc(keys[it]) }
    @Suppress("UNCHECKED_CAST") // Erasure :(
    return CompleteEnumMap.copyOf<K, V>(values as Array<V>)
}
