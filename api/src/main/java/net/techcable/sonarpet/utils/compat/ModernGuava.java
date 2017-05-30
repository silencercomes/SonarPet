package net.techcable.sonarpet.utils.compat;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.Executor;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;

public class ModernGuava implements GuavaVersion {
    /* package */ ModernGuava() {}

    private static final MethodHandle IMMUTABLE_LIST_COLLECTOR_METHOD;
    private static final MethodHandle IMMUTABLE_SET_COLLECTOR_METHOD;
    private static final MethodHandle DIRECT_EXECUTOR_METHOD;
    static {
        MethodType collectorType = MethodType.methodType(Collector.class);
        try {
            IMMUTABLE_LIST_COLLECTOR_METHOD = MethodHandles.publicLookup().findStatic(
                    ImmutableList.class,
                    "toImmutableList",
                    collectorType
            );
            IMMUTABLE_SET_COLLECTOR_METHOD = MethodHandles.publicLookup().findStatic(
                    ImmutableSet.class,
                    "toImmutableSet",
                    collectorType
            );
            DIRECT_EXECUTOR_METHOD = MethodHandles.publicLookup().findStatic(
                    MoreExecutors.class,
                    "directExecutor",
                    MethodType.methodType(Executor.class)
            );
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError("Unable to find modern guava methods", e);
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> Collector<T, ?, ImmutableList<T>> immutableListCollector() {
        return (Collector<T, ?, ImmutableList<T>>) IMMUTABLE_LIST_COLLECTOR_METHOD.invokeExact();
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public <T> Collector<T, ?, ImmutableSet<T>> immutableSetCollector() {
        return (Collector<T, ?, ImmutableSet<T>>) IMMUTABLE_SET_COLLECTOR_METHOD.invokeExact();
    }

    @Override
    @SneakyThrows
    public Executor directExecutor() {
        return (Executor) DIRECT_EXECUTOR_METHOD.invokeExact();
    }
}
