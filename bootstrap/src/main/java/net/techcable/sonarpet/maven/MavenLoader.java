package net.techcable.sonarpet.maven;

import lombok.*;

import java.io.IOException;
import java.nio.file.Path;

@FunctionalInterface
public interface MavenLoader {
    @NonNull
    Path load(MavenArtifact artifact) throws IOException, MavenException;
}
