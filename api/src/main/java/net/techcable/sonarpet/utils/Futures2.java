package net.techcable.sonarpet.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.Uninterruptibles;

import net.techcable.pineapple.SneakyThrow;

public final class Futures2 {
    private Futures2() {}

    /**
     * Get the result of the future, unwrapping and sneakily throwing any exceptions.
     *
     * Similar to {@link com.google.common.util.concurrent.Futures#getUnchecked(Future)},
     * but it does't wrap the exception at all.
     *
     * @param <T> the type of the future
     * @param future the future to get the result of
     * @return the result
     */
    public static <T> T getSneaky(Future<T> future) {
        try {
            return Uninterruptibles.getUninterruptibly(future);
        } catch (ExecutionException e) {
            throw SneakyThrow.sneakyThrow(e.getCause() != null ? e.getCause() : e);
        }
    }
}
