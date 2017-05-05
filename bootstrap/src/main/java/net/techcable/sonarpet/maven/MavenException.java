package net.techcable.sonarpet.maven;

/**
 * An exception that indicates a maven error.
 */
public class MavenException extends Exception {
    public MavenException(String message) {
        super(message);
    }

    public MavenException(String message, Exception cause) {
        super(message, cause);
    }
}
