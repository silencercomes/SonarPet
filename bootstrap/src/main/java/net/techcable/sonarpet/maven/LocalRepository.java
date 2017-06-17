package net.techcable.sonarpet.maven;

import lombok.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nullable;

import net.techcable.sonarpet.RecursiveDeleteOnExitHook;

import static java.util.Objects.*;

public final class LocalRepository extends MavenRepository {
    private final Path location;
    /* package */ LocalRepository(String name, Path location) throws MalformedURLException, URISyntaxException {
        super(requireNonNull(name, "Null name"), location.toUri());
        this.location = location.normalize();
    }

    @NonNull
    @Override
    public String getName() {
        return super.getName();
    }

    public Path downloadFrom(ResolvedMavenArtifact resolvedArtifact) throws IOException, MavenException {
        Path file = location.resolve(resolvedArtifact.getRelativePath());
        Files.createDirectories(file.getParent());
        resolvedArtifact.downloadTo(file);
        return file;
    }

    @Nullable
    public Path findPath(MavenArtifact artifact) throws IOException, MavenException {
        ResolvedMavenArtifact resolved = this.find(artifact);
        if (resolved != null) {
            return requireNonNull(resolved.getFileLocation(), () -> "Resolved location isn't a file: " + resolved.getLocation());
        } else {
            return null;
        }
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Nullable
    @Override
    public ResolvedMavenArtifact find(MavenArtifact artifact) throws IOException, MavenException {
        Path artifactDir = location.resolve(artifact.getRelativeDirectoryPath());
        Path simpleLocation = location.resolve(artifact.getRelativePath());
        if (!Files.isDirectory(artifactDir)) {
            return null; // Can't possibly exist without parent dir
        } else if (Files.exists(simpleLocation)) {
            return artifact.resolveTo(this, simpleLocation.toUri().toURL());
        } else {
            // Fallback to generic handling w/ maven-metadata.xml support (who puts a maven metadata in a local repo?)
            return super.find(artifact);
        }
    }

    @Override
    public String toString() {
        return getName() + "@" + location.normalize().toString();
    }

    public static final Path STANDARD_LOCATION = Paths.get(
            requireNonNull(System.getProperty("user.home"), "Null user.name"),
            ".m2", "repository"
    );
    private static final LocalRepository standardRepository = create("local", STANDARD_LOCATION);
    /**
     * Return the standard maven local repository, used by the command line tool.
     *
     * @return the standard local repository
     */
    public static LocalRepository standard() {
        return standardRepository;
    }

    public static LocalRepository create(String name, Path path) {
        try {
            return new LocalRepository(name, path);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Unexpected error converting path into URL: " + path, e);
        }
    }

    /**
     * Create a temporary local repository, in a temporary directory.
     *
     * The directory and its contents will be automatically deleted on exit.
     *
     * @return a temporary local repository
     */
    public static LocalRepository createTemp(String name) throws IOException {
        Path tempDir = Files.createTempDirectory("maven-" + requireNonNull(name));
        RecursiveDeleteOnExitHook.addTarget(tempDir);
        return create(name, tempDir);
    }
}
