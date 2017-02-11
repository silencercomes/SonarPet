/*
 * The MIT License
 * Copyright (c) 2016 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.techcable.sonarpet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A utility to load maven dependencies via a custom class loader.
 * <p>
 * Only downloads libraries on first run, caching in the local repository after that.
 * Using libraries offline works fine, as long as the library's already been downlaoded.
 * Using this utility decreases the size of a packaged jar file,
 * and saves space wasted by duplicate libraries.
 * </p>
 */
public final class LibraryLoader {
    private LibraryLoader() {}

    /**
     * The version of the LibraryLoader
     */
    public static final String VERSION = "0.1.0-alpha1-SNAPSHOT";
    /**
     * The User-Agent of the LibraryLoader
     */
    public static final String USER_AGENT = "LibraryLoader/" + VERSION + " (+Techcable)";

    private static final Path HOME_DIRECTORY = Paths.get(Objects.requireNonNull(
            System.getProperty("user.home"),
            "System property `user.home` isn't present!"
    ));
    private static final Path LOCAL_REPOSITORY = HOME_DIRECTORY.resolve(".m2/repository");
    private static final URL CENTRAL_REPOSITORY_URL = createUrl(
        /* Use HTTPS to access maven central */
            "https://repo1.maven.org/maven2/"
    );

    /**
     * A class loader which loads classes from libraries.
     */
    public static class LibraryLoadingClassLoader extends URLClassLoader {
        /**
         * Create a library-loading class loader, which loads classes from the specified libraries.
         * <p>
         * This ClassLoader uses the local repository as a cache,
         * and only downloads libraries if they aren't in the local repository.
         * </p>
         *
         * @param parent the parent ClassLoader
         * @param libraryArtifacts the libraries to load classes from
         * @param repositories the repositories to download the libraries from
         * @throws IOException if unable to download libraries
         * @throws IllegalArgumentException if any of the libraries don't exist
         */
        public LibraryLoadingClassLoader(
                ClassLoader parent,
                Set<LibraryArtifact> libraryArtifacts,
                Set<URL> repositories
        ) throws IOException {
            super(
                    libraryArtifacts.stream()
                            .map((libraryArtifact) -> sneakyThrow(() -> {
                                return downloadArtifact(libraryArtifact, repositories);
                            }))
                            .map(LibraryLoader::toUrl)
                            .toArray(URL[]::new),
                    parent
            );
        }

        /**
         * Invoke the method with the specified name in this classloader
         *
         * @param className the name of the class to invoke the method in
         * @param methodName the name of the method to invoke
         * @param methodType the type of the method to invoke
         * @param args the arguments to pass to the method
         * @return the object returned by the method, or null if void
         * @throws Throwable whatever error or exception is thrown by the target method
         * @throws IllegalArgumentException if there is no method with the specified name
         * @throws WrongMethodTypeException if the arguments don't correspond to the specified method type
         */
        public Object invokeStaticMethod(
                String className,
                String methodName,
                MethodType methodType,
                Object... args
        ) {
            final ClassLoader previousContextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                /*
                 * Temporarily set this object as the current context class loader.
                 * This is reverted in the finally block, regardless of whether we succed.
                 */
                Thread.currentThread().setContextClassLoader(this);
                Class<?> classObject = this.findClass(className);
                MethodHandle handle = MethodHandles.lookup().findStatic(
                        classObject,
                        methodName,
                        methodType
                );
                return sneakyThrow(() -> handle.invokeWithArguments(args));
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                        "Can't find static method "
                                + methodName
                                + methodType.toMethodDescriptorString()
                                + " in class"
                                + className
                );
            } catch (ClassNotFoundException | IllegalAccessException e) {
                throw sneakyThrow(e);
            } finally {
                Thread.currentThread().setContextClassLoader(previousContextClassLoader);
            }
        }

        /**
         * Create a library-loading class loader, which loads classes from the specified libraries located in the central repository.
         * <p>
         * This ClassLoader uses the local repository as a cache,
         * and only downloads libraries if they aren't in the local repository.
         * </p>
         *
         * @param libraryArtifacts the libraries to load classes from
         * @param repositories the repositories to download the libraries from
         * @throws IOException if unable to download libraries
         * @throws IllegalArgumentException if any of the libraries don't exist
         */
        public static LibraryLoadingClassLoader create(
                LibraryArtifact... libraries
        ) throws IOException {
            return create(Collections.emptySet(), libraries);
        }

        /**
         * Create a library-loading class loader, which loads classes from the specified libraries, searching the central repositories and the specified libraries.
         * <p>
         * This ClassLoader uses the local repository as a cache,
         * and only downloads libraries if they aren't in the local repository.
         * </p>
         *
         * @param extraRepositories the extra repositories to use in addition to the central repo
         * @param libraries the libraries to load classes from
         * @throws IOException if unable to download libraries
         * @throws IllegalArgumentException if any of the libraries don't exist
         */
        public static LibraryLoadingClassLoader create(
                Set<URL> extraRepositories,
                LibraryArtifact... libraries
        ) throws IOException {
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            if (currentClassLoader == null) {
                currentClassLoader = LibraryArtifact.class.getClassLoader();
            }
            URL[] extraRepositoriesArray = extraRepositories.toArray(new URL[0]);
            return new LibraryLoadingClassLoader(
                    currentClassLoader,
                    uniqueArrayToSet(
                            libraries,
                            LibraryLoader::duplicateLibraryException
                    ),
                    uniqueArrayToSet(
                            joinArrays(extraRepositoriesArray, CENTRAL_REPOSITORY_URL),
                            LibraryLoader::duplicateRepositoryException
                    )
            );
        }
    }

    /**
     * Download the specified artifact to the local repository, and return its location.
     * <p>
     * Returns the existing file if the artifact has already been downloaded.
     * This works like a dependency cache, downloading the artifact only on the first use.
     * </p>
     *
     * @param artifact the artifact to download
     * @param extraRepositories any extra repositories to look for the library in
     * @throws IOException if an error occurs downloading the dependency
     * @throws IllegalArgumentException if the specified artifact doesn't exist
     * @return the location of the artifact
     */
    public static Path downloadArtifact(
            LibraryArtifact artifact,
            URL... extraRepositories
    ) throws IOException {
        Path path;
        final Set<URL> repositories;
        if (extraRepositories.length > 0) {
            repositories = uniqueArrayToSet(
                    joinArrays(extraRepositories, CENTRAL_REPOSITORY_URL),
                    LibraryLoader::duplicateRepositoryException
            );
        } else {
            repositories = Collections.singleton(CENTRAL_REPOSITORY_URL);
        }
        return downloadArtifact(artifact, repositories);
    }

    private static IllegalArgumentException duplicateLibraryException(
            LibraryArtifact[] libraries,
            LibraryArtifact duplicateLibrary
    ) {
        return new IllegalArgumentException(
                "Duplicate library "
                        + duplicateLibrary
                        + " in ["
                        + Arrays.stream(libraries)
                        .map(Object::toString)
                        .collect(Collectors.joining(", "))
                        + "]"
        );
    }
    private static IllegalArgumentException duplicateRepositoryException(
            URL[] repositories,
            URL duplicateRepository
    ) {
        return new IllegalArgumentException(
                "Duplicate repository "
                        + duplicateRepository
                        + " in ["
                        + Arrays.stream(repositories)
                        .map(URL::toString)
                        .collect(Collectors.joining(", "))
                        + "]"
        );
    }

    /**
     * Look for the artifact in the specified repositories, download it to the local repository, and return its location.
     *
     * @param artifact the artifact to download
     * @param repositories the set of repositories to look for the library in
     * @throws IOException if an error occurs downloading the dependency
     * @throws IllegalArgumentException if the specified artifact doesn't exist
     * @return the location of the artifact
     */
    public static Path downloadArtifact(
            LibraryArtifact artifact,
            Set<URL> repositories
    ) throws IOException {
        Objects.requireNonNull(artifact, "Null artifact");
        Path localRepositoryFile = artifact.getLocalRepositoryPath();
        if (Files.exists(localRepositoryFile)) {
            // It already exists :o
            return localRepositoryFile;
        }
        // Create the parent directories
        Files.createDirectories(localRepositoryFile.getParent());
        try {
            downloadArtifactTo(artifact, repositories, localRepositoryFile);
        } catch (FileAlreadyExistsException ignored) {
            /*
             * This is perfectly fine, and simply means someone else downloaded.
             * Just use the current file and cancel the download.
             */
        }
        return localRepositoryFile;
    }

    /**
     * Download the specified artifact from the repositories to the specified file.
     *
     * @param artifact the artifact to download
     * @param repositories the repositories to download from
     * @param destFile the file to download to
     * @param copyOptions the options to copy the file with
     * @throws IOException if an error occurs downloading the dependency
     * @throws IllegalArgumentException if the specified artifact doesn't exist, or there are no repositories to search
     */
    public static void downloadArtifactTo(
            LibraryArtifact artifact,
            Set<URL> repositories,
            Path destFile,
            CopyOption... copyOptions
    ) throws IOException {
        Objects.requireNonNull(artifact, "Null artifact");
        String relativePath = artifact.getRelativePath();
        Objects.requireNonNull(repositories, "Null repositories");
        URL[] repositoriesArray = repositories.toArray(new URL[0]);
        if (repositoriesArray.length == 0) {
            throw new IllegalArgumentException("Empty repositories");
        }
        Objects.requireNonNull(destFile, "Null destination file");
        repositorySearch: for (URL repository : repositoriesArray) {
            URLConnection connection = repository.openConnection();
            if (connection instanceof HttpURLConnection) {
                connection.setRequestProperty("User-Agent", USER_AGENT);
                ((HttpURLConnection) connection).setRequestMethod("GET");
                connection.connect(); // Connect to the repo
                final int responseCode = ((HttpURLConnection) connection).getResponseCode();
                switch (responseCode) {
                    case 404:
                        /*
                         * The artifact isn't present in this repository.
                         * Continue searching until we find it.
                         */
                        continue repositorySearch;
                    case 200:
                        /*
                         * We've successfully connected to the repository.
                         * Download the artifact it to the specified file.
                         */
                        try (InputStream in = connection.getInputStream()) {
                            Files.copy(in, destFile, copyOptions);
                            return; // We successfully downloaded it!
                        }
                    default:
                        throw new IOException(
                                "Encountered an error downloading "
                                        + artifact
                                        + " from repository "
                                        + repository
                                        + ": "
                                        + Objects.toString(
                                        ((HttpURLConnection) connection).getResponseMessage(),
                                        "Unknown HTTP error"
                                )
                        );
                }
            } else {
                throw new IOException(
                        "Don't know how to check if "
                                + artifact
                                + " exists in "
                                + repository
                                + " since it's not HTTP(s)"
                );
            }
        }
    }

    //
    // Maven Code
    //

    public static final class LibraryArtifact {
        private final String groupId, artifactId, version;
        private final String extension;

        private static final Pattern VALIDATION_PATTERN = Pattern.compile(
                "(?:\\p{Alnum}[\\p{Alnum}-]*\\.)*(?:\\p{Alnum}[\\p{Alnum}-]*)"
        );
        private LibraryArtifact(
                String groupId,
                String artifactId,
                String version,
                String extension
        ) {
            this.groupId = Objects.requireNonNull(groupId, "Null groupId");
            this.artifactId = Objects.requireNonNull(artifactId, "Null artifactId");
            this.version = Objects.requireNonNull(version, "Null version");
            this.extension = Objects.requireNonNull(extension, "Null extension");
            if (!VALIDATION_PATTERN.matcher(groupId).matches()) {
                throw new IllegalArgumentException("Invalid groupId: " + groupId);
            } else if (!VALIDATION_PATTERN.matcher(artifactId).matches()) {
                throw new IllegalArgumentException("Invalid artifactId: " + artifactId);
            } else if (!VALIDATION_PATTERN.matcher(version).matches()) {
                throw new IllegalArgumentException("Invalid version: " + version);
            }
        }
        public static LibraryArtifact createArtifact(
                String groupId,
                String artifactId,
                String version,
                String extension
        ) {
            return new LibraryArtifact(groupId, artifactId, version, extension);
        }

        public String getGroupId() {
            return groupId;
        }

        public static LibraryArtifact parseSpecifier(String specifier, String extension) {
            String[] parts = specifier.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid specifier: " + specifier);
            }
            String groupId = parts[0];
            String artifactId = parts[1];
            String version = parts[2];
            return new LibraryArtifact(
                    groupId,
                    artifactId,
                    version,
                    extension
            );
        }

        public static LibraryArtifact parseJarSpecifier(String specifier) {
            return parseSpecifier(specifier, "jar");
        }

        public static LibraryArtifact createJarArtifact(
                String groupId,
                String artifactId,
                String version
        ) {
            return createArtifact(groupId, artifactId, version, "jar");
        }

        public String getRelativePath() {
            StringBuilder builder = new StringBuilder(
                    groupId.length()
                            + artifactId.length() * 2
                            + version.length() * 2
                            + extension.length()
                            + 5);
            builder.append(groupId.replace('.', '/'));
            builder.append('/');
            builder.append(artifactId);
            builder.append('/');
            builder.append(version);
            builder.append('/');
            builder.append(artifactId);
            builder.append('-');
            builder.append(version);
            builder.append('.');
            builder.append(extension);
            return builder.toString();
        }

        public boolean isInLocalRepo() {
            return Files.exists(getLocalRepositoryPath());
        }

        public Path getLocalRepositoryPath() {
            return LOCAL_REPOSITORY.resolve(getRelativePath());
        }

        public URL relativeTo(URL other) {
            try {
                URI otherUri = Objects.requireNonNull(other, "Null url").toURI();
                return otherUri.resolve(getRelativePath()).toURL();
            } catch (MalformedURLException | URISyntaxException e) {
                throw new IllegalArgumentException(
                        "Invalid url "
                                + other
                                + " relative to "
                                + this.getRelativePath()
                );
            }
        }

        @Override
        public String toString() {
            return groupId + ":" + artifactId + ":" + version;
        }
    }

    // Internal Utilities

    private static <T> T[] joinArrays(T[] first, T... second) {
        Objects.requireNonNull(first, "Null first array");
        final int firstLength = first.length;
        Class<?> componentType = first.getClass().getComponentType();
        Objects.requireNonNull(second, "Null second array");
        final int secondLength = second.length;
        T[] result = (T[]) Array.newInstance(componentType, firstLength + secondLength);
        System.arraycopy(first, 0, result, 0, firstLength);
        System.arraycopy(second, 0, result, firstLength, secondLength);
        return result;
    }

    private static <E extends Throwable, T> Set<T> uniqueArrayToSet(
            T[] array,
            BiFunction<T[], T, E> errorMsgFunction
    ) throws E {
        Set<T> result = new HashSet(array.length);
        for (T element : array) {
            if (!result.add(element)) {
                throw errorMsgFunction.apply(array, element);
            }
        }
        return result;
    }

    private static URL toUrl(Path path) {
        return sneakyThrow(() -> {
            URI uri = Objects.requireNonNull(path, "Null path").toUri();
            return uri.toURL();
        });
    }

    private static URL createUrl(String str) {
        return sneakyThrow(() -> new URL(str));
    }

    @FunctionalInterface
    private static interface CheckedSupplier<T> {
        public T run() throws Throwable;

        public default Supplier<T> toSupplier() {
            return () -> sneakyThrow(this::run);
        }
    }

    private static <T> T sneakyThrow(CheckedSupplier<T> code) {
        try {
            return code.run();
        } catch (Throwable t) {
            throw sneakyThrow(t);
        }
    }

    private static AssertionError sneakyThrow(Throwable t) {
        return LibraryLoader.<RuntimeException>sneakyThrow0(t);
    }

    private static <T extends Throwable> AssertionError sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }
}