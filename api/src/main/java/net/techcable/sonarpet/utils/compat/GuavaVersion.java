package net.techcable.sonarpet.utils.compat;

import java.util.concurrent.Executor;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public interface GuavaVersion {
    <T> Collector<T, ?, ImmutableList<T>> immutableListCollector();
    <T> Collector<T, ?, ImmutableSet<T>> immutableSetCollector();
    Executor directExecutor();

    GuavaVersion INSTANCE = acquireInstance();
    static GuavaVersion acquireInstance() {
        if (INSTANCE != null) return INSTANCE;
        try {
            ImmutableList.class.getMethod("toImmutableList");
            return new ModernGuava();
        } catch (NoSuchMethodException e) {
            return new AncientGuava();
        }
    }
}
