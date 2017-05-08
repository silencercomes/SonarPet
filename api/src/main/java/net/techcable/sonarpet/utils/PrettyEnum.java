package net.techcable.sonarpet.utils;

import org.apache.commons.lang.WordUtils;

/**
 * An enum with a pretty representation for humans.
 *
 * By default, this takes the result of 'toString()',
 * capitalizes it, and replaces underscores with spaces.
 */
public interface PrettyEnum {
    /**
     * Return a pretty representation of this enum.
     * 
     * @return a pretty representation of this enum.
     */
    default String toPrettyString() {
        return WordUtils.capitalizeFully(this.toString()).replace('_', ' ');
    }

    /**
     * Return a pretty representation of the specified enum.
     * 
     * If the enum is a {@link PrettyEnum}, it uses that representation.
     * Otherwise it falls back to the default representation.
     * 
     * @param e the enum to represent
     * @return the pretty representation
     */
    static String toPrettyString(Enum<?> e) {
        if (e instanceof PrettyEnum) {
            return ((PrettyEnum) e).toPrettyString();
        } else {
            return WordUtils.capitalizeFully(e.toString()).replace('_', ' ');
        } 
    }
}
