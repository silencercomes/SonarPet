package net.techcable.sonarpet;

import lombok.*;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

import com.dsh105.echopet.EchoPetPlugin;
import com.google.common.collect.ImmutableSet;

import net.techcable.sonarpet.LibraryLoader.LibraryArtifact;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import static net.techcable.sonarpet.LibraryLoader.LibraryArtifact.*;

public class Bootstrap extends JavaPlugin {
    /**
     * All of SonarPet's dependencies, including transitive ones
     */
    private static final ImmutableSet<LibraryArtifact> DEPENDENCIES = ImmutableSet.of(
            parseJarSpecifier("com.google.code.gson:gson:2.2.4"),
            parseJarSpecifier("net.techcable:pineapple:0.1.0-beta4"),
            parseJarSpecifier("com.dsh105:Commodus:1.0.5"),
            parseJarSpecifier("com.dsh105:PowerMessage:1.0.1-SNAPSHOT"),
            parseJarSpecifier("org.ow2.asm:asm-all:5.1"),
            parseJarSpecifier("org.slf4j:slf4j-api:1.7.5"),
            parseJarSpecifier("org.slf4j:slf4j-jdk14:1.7.5"),
            parseJarSpecifier("com.zaxxer:HikariCP:2.4.5"),
            parseJarSpecifier("org.jetbrains.kotlin:kotlin-stdlib-jre8:1.1.0-rc-91"),
            parseJarSpecifier("org.jetbrains.kotlin:kotlin-stdlib:1.1.0-rc-91")
    );
    private static final ImmutableSet<URL> REPOSITORIES = ImmutableSet.of(
            createUrl("https://repo.techcable.net/content/groups/public/"),
            createUrl("https://dl.bintray.com/kotlin/kotlin-eap-1.1/")
    );
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
    private BootstrapedPlugin plugin;
    @SneakyThrows
    private static void injectUrl(ClassLoader classLoader, URL url) {
        ADD_URL_METHOD.invoke((URLClassLoader) classLoader, url);
    }

    @Override
    public void onLoad() {
        getLogger().info("Downloading SonarPet's libraries");
        try {
            for (LibraryArtifact dependency : DEPENDENCIES) {
                Path path = LibraryLoader.downloadArtifact(dependency, REPOSITORIES);
                injectUrl(getClass().getClassLoader(), path.toUri().toURL());
            }
            plugin = new EchoPetPlugin(this);
        } catch (IOException t) {
            getLogger().log(Level.SEVERE, "Unable to load libraries", t);
            setEnabled(false);
        }
    }

    @Override
    public void onEnable() {
        plugin.onEnable();
    }

    @Override
    public void onDisable() {
        plugin.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return plugin.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return plugin.onTabComplete(sender, command, alias, args);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return plugin.getDefaultWorldGenerator(worldName, id);
    }

    //
    // Utilities
    //

    @SneakyThrows
    private static URL createUrl(String url) {
        return new URL(url);
    }
}
