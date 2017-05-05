package net.techcable.sonarpet.maven;

import lombok.*;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import static java.util.Objects.*;

/**
 * A maven dependency artifact (usually a jar)
 */
public class MavenArtifact {
    @Getter
    private final String groupId, artifactId, version;
    @Getter
    private final String extension;

    private static final Pattern VALIDATION_PATTERN = Pattern.compile(
            "(?:[\\w-]+\\.)*(?:[\\w-]+)"
    );

    /* package */ MavenArtifact(String groupId, String artifactId, String version, String extension) {
        this.groupId = requireNonNull(groupId);
        this.artifactId = requireNonNull(artifactId);
        this.version = requireNonNull(version);
        this.extension = requireNonNull(extension);
        Matcher m = VALIDATION_PATTERN.matcher(groupId);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid groupId: " + groupId);
        }
        if (!m.reset(artifactId).matches()) {
            throw new IllegalArgumentException("Invalid artifactId: " + artifactId);
        }
        if (!m.reset(version).matches()) {
            throw new IllegalArgumentException("Invalid version: " + artifactId);
        }
        if (!extension.equals("jar") && !m.reset(extension).matches()) {
            throw new IllegalArgumentException("Invalid extension: " + extension);
        }
    }

    public static MavenArtifact create(String groupId, String artifactId, String version, String extension) {
        return new MavenArtifact(groupId, artifactId, version, extension);
    }

    public static MavenArtifact createJar(String groupId, String artifactId, String version) {
        return new MavenArtifact(groupId, artifactId, version, "jar");
    }


    public static MavenArtifact parseSpecifier(String specifier, String extension) {
        String[] parts = specifier.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid specifier: " + specifier);
        }
        String groupId = parts[0];
        String artifactId = parts[1];
        String version = parts[2];
        return new MavenArtifact(
                groupId,
                artifactId,
                version,
                extension
        );
    }

    public static MavenArtifact parseJarSpecifier(String specifier) {
        return parseSpecifier(specifier, "jar");
    }

    public ResolvedMavenArtifact resolveTo(MavenRepository repository, URL location) {
        return new ResolvedMavenArtifact(repository, groupId, artifactId, version, extension, location);
    }

    public boolean isSnapshot() {
        return version.endsWith("-SNAPSHOT");
    }

    /**
     * Return the bare version of this artifact, without the 'SNAPSHOT' for snapshot versions.
     *
     * Does nothing for normal artifact versions.
     *
     * @return the bare version of this artifact, with SNAPSHOT removed
     */
    public String bareVersion() {
        if (isSnapshot()) {
            return version.substring(0, version.length() - "-SNAPSHOT".length());
        } else {
            return version;
        }
    }

    public String getRelativePath() {
        return getRelativeDirectoryPath() + getRelativeFileName();
    }

    public String getRelativeDirectoryPath() {
        return groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/';
    }

    public String getRelativeFileName() {
        return getRelativeFileName(null);
    }

    /**
     * Get the name of this artifact's file in a maven repository,
     * given the specified information on the snapshot.
     *
     * @param snapshotInfo information on the snapshot, or null if none
     * @return the name of this artifact's file
     */
    public String getRelativeFileName(@Nullable MavenMetadata.SnapshotInfo snapshotInfo) {
        if (snapshotInfo != null) {
            return String.join(
                    "-",
                    artifactId,
                    bareVersion(),
                    snapshotInfo.getTimestamp(),
                    Integer.toString(snapshotInfo.getBuildNumber())
            ) + '.' + this.extension;
        } else {
            return artifactId + '-' + version + '.' + extension;
        }
    }

    public String getSpecifier() {
        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public String toString() {
        return getSpecifier();
    }
}
