package net.techcable.sonarpet.utils;

import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;

public class MoreCollectors {
    private MoreCollectors() {}
    private static final Collector IMMUTABLE_LIST_COLLECTOR = Collector.of(
            ImmutableList::builder,
            ImmutableList.Builder::add,
            (first, second) -> first.addAll(second.build()),
            ImmutableList.Builder::build
    );
    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
        return IMMUTABLE_LIST_COLLECTOR;
    }
}
