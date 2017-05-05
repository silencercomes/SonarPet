package net.techcable.sonarpet.maven;

import lombok.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nullable;

import static java.util.Objects.*;

public class ResolvedMavenArtifact extends MavenArtifact {
    private final MavenRepository repository;
    private final URL location;
    private final boolean secure;
    @Nullable
    private final Path fileLocation;

    @SneakyThrows(URISyntaxException.class)
    /* package */ ResolvedMavenArtifact(MavenRepository repository, String groupId, String artifactId, String version, String extension, URL location) {
        super(groupId, artifactId, version, extension);
        this.repository = requireNonNull(repository);
        this.location = requireNonNull(location);
        String protocol = location.getProtocol();
        boolean secure = false;
        switch (protocol) {
            case "https":
                secure = true;
                // fallthrough
            case "http":
                fileLocation = null;
                break;
            case "file":
                fileLocation = Paths.get(location.toURI()).normalize();
                secure = true;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported protocol: " + protocol);
        }
        this.secure = secure;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isLocal() {
        return fileLocation != null;
    }

    @Nullable
    public Path getFileLocation() {
        return fileLocation;
    }

    public URL getLocation() {
        return location;
    }

    public MavenRepository getRepository() {
        return repository;
    }

    private static final String USER_AGENT;

    static {
        String name = ResolvedMavenArtifact.class.getName();
        USER_AGENT = name.substring(name.lastIndexOf('.') + 1);
    }

    public void downloadTo(Path dest) throws IOException, MavenException {
        requireNonNull(dest);
        if (fileLocation != null) {
            Files.copy(fileLocation, dest);
        } else {
            try (OutputStream out = Files.newOutputStream(dest, StandardOpenOption.CREATE_NEW)) {
                HttpURLConnection connection = (HttpURLConnection) location.openConnection();
                connection.addRequestProperty("User-Agent", USER_AGENT);
                connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    throw new MavenException(this + " doesn't exist");
                }
                String encoding = connection.getContentEncoding();
                try (InputStream rawInput = connection.getInputStream()) {
                    final InputStream in;
                    if (encoding != null) {
                        switch (encoding) {
                            case "gzip":
                                in = new GZIPInputStream(connection.getInputStream());
                                break;
                            case "deflate":
                                in = new DeflaterInputStream(connection.getInputStream());
                                break;
                            default:
                                throw new IOException("Can't handle encoding: " + encoding);
                        }
                    } else {
                        in = connection.getInputStream();
                    }
                    byte[] buffer = new byte[4096];
                    int readBytes;
                    while ((readBytes = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, readBytes);
                    }
                }
            }
        }
    }

    public void verifyExistence() throws IOException, MavenException {
        if (!checkExistence()) {
            throw new MavenException(this + " doesn't exist");
        }
    }

    private int exists = -1;
    public boolean checkExistence() throws IOException {
        // Cached existence check
        int exists = this.exists;
        if (exists < 0) {
            this.exists = exists = checkExistence0() ? 1 : 0;
        }
        assert exists >= 0;
        return exists != 0;
    }
    private boolean checkExistence0() throws IOException {
        if (fileLocation != null) {
            return Files.exists(fileLocation);
        } else {
            HttpURLConnection connection = (HttpURLConnection) location.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestMethod("HEAD");
            connection.connect();
            int responseCode = connection.getResponseCode();
            switch (responseCode) {
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return false;
                case HttpURLConnection.HTTP_OK:
                    return true;
                default:
                    throw new IOException("Unexpected response code: " + responseCode);
            }
        }
    }

    @Override
    public String toString() {
        return getSpecifier() + "@" + (fileLocation != null ? fileLocation.toString() : location.toString());
    }
}
