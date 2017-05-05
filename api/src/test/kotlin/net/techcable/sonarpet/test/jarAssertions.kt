package net.techcable.sonarpet.test

import java.nio.file.Path
import java.util.jar.JarFile
import java.util.zip.ZipException


fun assertValidJar(path: Path) = assertValidJar(path) { "$path isn't a valid jar!" }
fun assertValidJar(path: Path, lazyMessage: (InvalidJarReason) -> String) {
    try {
        JarFile(path.toFile()).use {
            val entries = it.entries().toList()
            if (entries.isEmpty()) {
                fail(lazyMessage(InvalidJarReason.EMPTY_JAR))
            }
        }
    } catch (e: ZipException) {
        fail(lazyMessage(InvalidJarReason.INVALID_ZIP))
    } catch (e: SecurityException) {
        fail(lazyMessage(InvalidJarReason.INVALID_SIGNATURE))
    } catch (e: NoSuchFileException) {
        fail(lazyMessage(InvalidJarReason.FILE_NOT_FOUND))
    }
}

/**
 * The reason why [assertValidJar] failed
 */
enum class InvalidJarReason {
    /**
     * The jar isn't a valid zip file.
     */
    INVALID_ZIP,
    /**
     * The jar is empty.
     */
    EMPTY_JAR,
    /**
     * The jar's signature isn't valid
     */
    INVALID_SIGNATURE,
    /**
     * The jar doesn't exist.
     */
    FILE_NOT_FOUND;

    override fun toString(): String {
        return when (this) {
            INVALID_ZIP -> "Invalid zipfile"
            EMPTY_JAR -> "Empty jar"
            INVALID_SIGNATURE -> "Invalid signature"
            FILE_NOT_FOUND -> "File not found"
        }
    }
}
