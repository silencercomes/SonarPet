package net.techcable.sonarpet.maven;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import javax.annotation.Nullable;

import static java.util.Objects.*;

/**
 * A maven repository, with a url and (optionally) a name.
 * <p>
 * Equality and hashing is based only on the URL,
 * so two repositories with the same url will always be equal,
 * even if they have different names.
 */
public class MavenRepository {
    @Nullable
    private final String name;
    private final URI uri;
    private final URL url;

    /* package */ MavenRepository(@Nullable String name, URL url) throws URISyntaxException, MalformedURLException {
        this(name, url.toURI());
    }

    /* package */ MavenRepository(@Nullable String name, URI uri) throws MalformedURLException, URISyntaxException {
        this.name = name;
        // Ensure the URI refers to a directory
        requireNonNull(uri, "Null URI");
        if (!uri.getPath().endsWith("/")) {
            uri = new URI(
                    uri.getScheme(),
                    uri.getHost(),
                    uri.getPath() + "/",
                    uri.getFragment()
            );
        }
        this.uri = uri;
        this.url = uri.toURL();
    }

    public boolean contains(MavenArtifact artifact) throws IOException, MavenException {
        return find(artifact) != null;
    }

    @Nullable
    public ResolvedMavenArtifact find(MavenArtifact artifact) throws IOException, MavenException {
        // Make sure we have that '/' at the end, or it'll strip the directory name
        URI dir = uri.resolve(artifact.getRelativeDirectoryPath() + "/");
        final MavenMetadata.SnapshotInfo snapshotInfo;
        if (artifact.isSnapshot()) {
            // Try and fetch the metadata
            URL metadataUrl = dir.resolve("maven-metadata.xml").toURL();
            MavenMetadata metadata = MavenMetadata.parse(metadataUrl);
            snapshotInfo = metadata != null ? metadata.getSnapshotInfo() : null;
        } else {
            snapshotInfo = null;
        }
        URI artifactUri = dir.resolve(artifact.getRelativeFileName(snapshotInfo));
        ResolvedMavenArtifact resolvedArtifact = artifact.resolveTo(this, artifactUri.toURL());
        if (resolvedArtifact.checkExistence()) {
            return resolvedArtifact;
        } else {
            return null;
        }
    }

    @Nullable
    public String getName() {
        return name;
    }

    public URI getUri() {
        return uri;
    }

    public URL getUrl() {
        return url;
    }

    private static final MavenRepository CENTRAL_REPOSITORY = create("maven-central", "https://repo1.maven.org/maven2/");
    /**
     * Return the maven central repository, which is always present by default.
     *
     * @return the maven central repository
     */
    public static MavenRepository central() {
        return CENTRAL_REPOSITORY;
    }

    public static MavenRepository create(String name, String url) {
        try {
            return create(name, new URL(requireNonNull(url)));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url: " + url);
        }
    }

    public static MavenRepository create(String name, URL url) {
        requireNonNull(name, "Null name");
        try {
            return new MavenRepository(name, url.toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url: " + url, e);
        }
    }

    public static MavenRepository createAnonymous(URL url) {
        try {
            return new MavenRepository(null, url.toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url: " + url, e);
        }
    }

    @Override
    public String toString() {
        return Objects.toString(name, "unknown") + "@" + uri;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj != null
                && obj instanceof MavenRepository
                && ((MavenRepository) obj).uri.equals(this.uri));
    }

    public boolean exactlyEquals(MavenRepository other) {
        return other == this || other.uri.equals(this.uri) && Objects.equals(other.name, this.name);
    }
}
