package net.techcable.sonarpet.utils.compat;

import java.util.concurrent.Executor;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;

public class AncientGuava implements GuavaVersion {
    /* package */ AncientGuava() {}
    @Override
    public <T> Collector<T, ?, ImmutableList<T>> immutableListCollector() {
        return Collector.<T, ImmutableList.Builder<T>, ImmutableList<T>>of(
                ImmutableList::builder,
                ImmutableList.Builder::add,
                (first, second) -> first.addAll(second.build()),
                ImmutableList.Builder::build
        );
    }

    @Override
    public <T> Collector<T, ?, ImmutableSet<T>> immutableSetCollector() {
        return Collector.<T, ImmutableSet.Builder<T>, ImmutableSet<T>>of(
                ImmutableSet::builder,
                ImmutableSet.Builder::add,
                (first, second) -> first.addAll(second.build()),
                ImmutableSet.Builder::build
        );
    }

    @Override
    public Executor directExecutor() {
        return MoreExecutors.sameThreadExecutor();
    }
}
