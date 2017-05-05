package net.techcable.sonarpet.maven;

import lombok.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MavenDependencyInfo {
    private final List<MavenRepository> repositories;
    private final Set<MavenArtifact> dependencies;
    public MavenDependencyInfo(List<MavenRepository> repositories, Set<MavenArtifact> artifacts) {
        this.repositories = Collections.unmodifiableList(new ArrayList<>(repositories));
        this.dependencies = Collections.unmodifiableSet(new LinkedHashSet<>(artifacts));
    }

    public void injectDependencies(URLClassLoader classLoader, MavenLoader loader) throws MavenException, IOException {
        List<Path> results = new ArrayList<>(dependencies.size());
        for (MavenArtifact dependency : dependencies) {
            results.add(loader.load(dependency));
        }
        for (Path result : results) {
            injectUrl(classLoader, result.toUri().toURL());
        }
    }

    public ResolvedMavenArtifact find(MavenArtifact artifact) throws MavenException, IOException {
        ResolvedMavenArtifact resolved = this.tryFind(artifact);
        if (resolved == null) {
            throw new MavenException("Unable to find " + artifact);
        }
        return resolved;
    }

    @Nullable
    public ResolvedMavenArtifact tryFind(MavenArtifact artifact) throws IOException, MavenException {
        for (MavenRepository repository : repositories) {
            ResolvedMavenArtifact resolved = repository.find(artifact);
            if (resolved != null) {
                return resolved;
            }
        }
        return null;
    }

    public static MavenDependencyInfo parseResource(String resourceName) throws IOException {
        URL resource = MavenDependencyInfo.class.getResource("/" + resourceName);
        if (resource == null) {
            throw new IllegalArgumentException("Resource " + resourceName + " not found!");
        }
        try (Reader reader = new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8)) {
            return parse(new JsonParser().parse(reader).getAsJsonObject());
        }
    }

    @SneakyThrows(MalformedURLException.class)
    public static MavenDependencyInfo parse(JsonObject dependencyInfo) {
        Set<MavenArtifact> dependencies = new HashSet<>();
        for (JsonElement dependencyJson : dependencyInfo.get("dependencies").getAsJsonArray()) {
            MavenArtifact dependency = MavenArtifact.parseJarSpecifier(dependencyJson.getAsString());
            if (!dependencies.add(dependency)) {
                throw new IllegalArgumentException("Duplicate dependencies: " + dependency);
            }
        }
        List<MavenRepository> repositories = new ArrayList<>();
        for (JsonElement repositoryElement : dependencyInfo.get("repositories").getAsJsonArray()) {
            JsonObject repository = repositoryElement.getAsJsonObject();
            String name = repository.get("name").getAsString();
            URL url = new URL(repository.get("url").getAsString());
            repositories.add(MavenRepository.create(name, url));
        }
        return new MavenDependencyInfo(repositories, dependencies);
    }

    private static final MethodHandle ADD_URL_METHOD;
    static {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            ADD_URL_METHOD = MethodHandles.lookup().unreflect(method);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
    @SneakyThrows
    private static void injectUrl(URLClassLoader classLoader, URL url) {
        ADD_URL_METHOD.invoke(classLoader, url);
    }
}
