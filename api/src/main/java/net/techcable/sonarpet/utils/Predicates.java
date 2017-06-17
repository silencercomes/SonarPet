package net.techcable.sonarpet.utils;

import java.util.function.Predicate;

/**
 * Java 8 version of guava {@link com.google.common.base.Predicates}.
 */
public class Predicates {
    private Predicates() {}

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysFalse() {
        return AlwaysFalsePredicate.INSTANCE;
    }
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysTrue() {
        return AlwaysTruePredicate.INSTANCE;
    }
    /* package */ static class AlwaysFalsePredicate implements Predicate {
        private static final AlwaysFalsePredicate INSTANCE = new AlwaysFalsePredicate();
        @Override
        public boolean test(Object o) {
            return false;
        }
    }
    /* package */ static class AlwaysTruePredicate implements Predicate {
        private static final AlwaysTruePredicate INSTANCE = new AlwaysTruePredicate();

        @Override
        public boolean test(Object o) {
            return true;
        }
    }
}
