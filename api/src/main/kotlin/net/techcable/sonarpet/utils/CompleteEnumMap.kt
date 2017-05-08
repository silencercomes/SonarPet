package net.techcable.sonarpet.utils

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Maps
import java.util.function.BiConsumer

/**
 * An immutable [java.util.EnumMap], where all keys have a non-null value.
 *
 * This allows the guarantee that [CompleteEnumMap.get] will never return a null value.
 */
final class CompleteEnumMap<K: Enum<K>, out V: Any> private constructor(
        val keyType: Class<K>,
        valueArray: Array<out Any?>,
        private val keyArray: Array<K> = keyType.enumConstants,
        private var cachedImmutableMap: ImmutableMap<K, V>? = null
): Map<K, V> {
    private val valueArray: Array<out V>
    private val hashCode = keyType.hashCode() xor valueArray.contentHashCode()
    init {
        require(valueArray.size == keyArray.size) {
            if (valueArray.size < keyArray.size) {
                "Insufficient values for ${keyType.typeName}: ${valueArray.size}"
            } else {
                "Too many values for ${keyType.typeName}: ${valueArray.size}"
            }
        }
        @Suppress("UNCHECKED_CAST") // We can't check the cast because of erasure
        this.valueArray = (valueArray as Array<V?>).requireNoNulls {
            "Null value for ${keyType.typeName}.${keyArray[it].name}"
        }
    }

    override val size: Int
        get() = keyArray.size

    override fun containsKey(key: K): Boolean {
        if (!keyType.isInstance(key)) return false
        assert(keyArray[key.ordinal] == key)
        return true
    }

    override fun containsValue(value: @UnsafeVariance V) = value in valueArray

    override fun get(key: K): V = valueArray[key.ordinal]

    override fun isEmpty() = size != 0

    private var cachedEntries: ImmutableSet<Map.Entry<K, V>>? = null
    override val entries: ImmutableSet<Map.Entry<K, @UnsafeVariance V>>
        get() {
            var entries = this.cachedEntries
            if (entries == null) {
                entries = this.keyArray.mapToImmutableSet { Maps.immutableEntry(it, this[it]) }
                check(entries.size == this.size)
                this.cachedEntries = entries
            }
            return entries
        }

    private var cachedKeys: ImmutableSet<K>? = null
    override val keys: ImmutableSet<K>
        get() {
            var keys = this.cachedKeys
            if (keys == null) {
                keys = this.keyArray.toImmutableSet()
                check(keys.size == keyArray.size)
                this.cachedKeys = keys
            }
            return keys
        }
    private var cachedValues: ImmutableList<V>? = null
    override val values: ImmutableList<@UnsafeVariance V>
        get() {
            var values = this.cachedValues
            if (values == null) {
                values = this.valueArray.toImmutableList()
                this.cachedValues = values
            }
            return values
        }

    fun toImmutableMap(): ImmutableMap<K, @UnsafeVariance V> {
        var result = this.cachedImmutableMap
        if (result == null) {
            result = Maps.immutableEnumMap(this)!!
            check(result.size == this.size)
            this.cachedImmutableMap = result
        }
        return result
    }

    override fun getOrDefault(key: K, defaultValue: @UnsafeVariance V) = this[key] // Keys are always present

    override fun forEach(action: BiConsumer<in K, in V>) {
        keyArray.forEachIndexed { index, key ->
            val value = this.valueArray[index]
            action.accept(key, value)
        }
    }

    override fun equals(other: Any?) = when (other) {
        is CompleteEnumMap<*, *> -> this.keyType == other.keyType && this.valueArray.contentEquals(other.valueArray)
        is Map<*, *> -> this.size == other.size && other.all { (key, value) ->
            keyType.isInstance(key) && this[key] == value
        }
        else -> false
    }

    override fun hashCode() = hashCode

    companion object {
        @JvmStatic
        inline fun <reified K: Enum<K>, V: Any> copyOf(map: Map<K, V>) = copyOf(map, K::class.java)

        @JvmStatic
        fun <K: Enum<K>, V: Any> copyOf(map: Map<K, V>, keyType: Class<K>): CompleteEnumMap<K, V> {
            val keyArray = keyType.enumConstants
            val valueArray = Array<Any?>(keyArray.size) { map[keyArray[it]] }
            return CompleteEnumMap(
                    keyType = keyType,
                    keyArray = keyArray,
                    valueArray = valueArray,
                    cachedImmutableMap = if (map is ImmutableMap) map else null
            )
        }

        @JvmStatic
        inline fun <reified K: Enum<K>, V: Any> copyOf(values: Array<V>) = copyOf(values, K::class.java)

        @JvmStatic
        fun <K: Enum<K>, V: Any> copyOf(
                values: Array<out V>,
                keyType: Class<K>
        ): CompleteEnumMap<K, V> {
            return copyOfNullable(values.mayHaveNulls(), keyType)
        }

        /**
         * Create a copy of the specified array of values,
         * checking that the values aren't null.
         *
         * @param values the
         * @param keyType the type of the enum keys
         * @throws IllegalArgumentException if any key has a null/empty value
         * @throws IllegalArgumentException if the number of values doesn't equal the number of keys
         */
        @JvmStatic
        fun <K: Enum<K>, V: Any> copyOfNullable(
                values: Array<out V?>,
                keyType: Class<K>
        ): CompleteEnumMap<K, V> {
            return CompleteEnumMap(
                    keyType = keyType,
                    valueArray = values.copyOf() // Defensive copy
            )
        }
    }
}
