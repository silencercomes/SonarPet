package net.techcable.sonarpet.utils.compat

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import java.util.concurrent.Executor
import java.util.stream.Collector

interface GuavaVersion {
    fun <E> immutableListCollector(): Collector<E, *, ImmutableList<E>>
    fun <E> immutableSetCollector(): Collector<E, *, ImmutableSet<E>>
    fun directExecutor(): Executor

    companion object {
        val DETECTED_VERSION = detectVersion()
        private fun detectVersion(): GuavaVersion {
            try {
                ImmutableList::class.java.getMethod("toImmutableList")
                return ModernGuava
            } catch (e: NoSuchMethodException) {
                return AncientGuava
            }
        }
    }
}