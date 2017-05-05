package net.techcable.sonarpet;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.techcable.sonarpet.maven.LocalRepository;

/**
 * A shutdown hook that recursively deletes registered files.
 *
 * Can be used to cleanup temporary directories and resources.
 * Although failures will be logged, failure of one deletion won't affect others.
 */
public class RecursiveDeleteOnExitHook implements Runnable {
    @Nullable
    private static RecursiveDeleteOnExitHook INSTANCE;
    private final List<Path> toDelete = new ArrayList<>();

    @Override
    public void run() {
        // NOTE: Iterate backwards so last added is first deleted
        // For some reason this is important (see JDK DeleteOnExitHook(
        for (int i = toDelete.size() - 1; i >= 0; i--) {
            Path target = toDelete.get(i);
            try {
                if (Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
                    Files.walkFileTree(target, new RecursiveDirectoryDeleter());
                } else {
                    Files.deleteIfExists(target);
                }
            } catch (IOException e) {
                //noinspection UseOfSystemOutOrSystemErr - VM is shutting down
                System.err.println("Unable to delete " + toDelete + ": " + e);
            }
        }
    }

    public static synchronized void addTarget(Path toDelete) {
        RecursiveDeleteOnExitHook hook;
        if ((hook = INSTANCE) == null) {
            hook = new RecursiveDeleteOnExitHook();
            Runtime.getRuntime().addShutdownHook(new Thread(hook));
            INSTANCE = hook;
        }
        hook.toDelete.add(toDelete);
    }

    private static class RecursiveDirectoryDeleter extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc != null) throw exc;
            Files.deleteIfExists(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
