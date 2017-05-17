package net.techcable.sonarpet.utils.compat

import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.MoreExecutors
import net.techcable.sonarpet.getMethodHandle
import java.lang.invoke.MethodHandle
import java.util.concurrent.Executor
import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collector

object AncientGuava: GuavaVersion {
    private val immutableListCollectorInstance = Collector.of(
            Supplier { ImmutableList.builder<Any>() },
            BiConsumer<ImmutableList.Builder<Any>, Any> { builder, element -> builder.add(element) },
            BinaryOperator<ImmutableList.Builder<Any>> { first, second -> first.addAll(second.build()) },
            Function<ImmutableList.Builder<Any>, ImmutableList<Any>> { it.build() }
    )
    @Suppress("UNCHECKED_CAST")
    override fun <E> immutableListCollector() = immutableListCollectorInstance as Collector<E, *, ImmutableList<E>>

    private val SAME_THREAD_EXECUTOR_METHOD: MethodHandle = MoreExecutors::class.getMethodHandle("sameThreadExecutor")
    override fun directExecutor() = SAME_THREAD_EXECUTOR_METHOD.invoke() as Executor
}