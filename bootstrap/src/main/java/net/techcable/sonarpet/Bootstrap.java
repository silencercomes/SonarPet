package net.techcable.sonarpet;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;

import com.dsh105.echopet.EchoPetPlugin;
import com.google.common.base.Verify;

import net.sf.cglib.core.Local;
import net.techcable.sonarpet.bstats.Metrics;
import net.techcable.sonarpet.maven.LocalRepository;
import net.techcable.sonarpet.maven.MavenDependencyInfo;
import net.techcable.sonarpet.maven.MavenException;
import net.techcable.sonarpet.maven.ResolvedMavenArtifact;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    private Metrics metrics;
    private BootstrapedPlugin plugin;


    @Override
    public void onLoad() {
        getLogger().info("Downloading SonarPet's libraries");
        try {
            boolean accessDenied = false;
            try {
                Files.createDirectories(LocalRepository.standard().getLocation());
                Verify.verify(Files.isDirectory(LocalRepository.standard().getLocation()));
            } catch (AccessDeniedException e) {
                accessDenied = true;
            }
            final LocalRepository localRepo;
            if (accessDenied || !Files.isWritable(LocalRepository.standard().getLocation())) {
                getLogger().warning("Using fallback local repo since the standard one isn't writable");
                localRepo = LocalRepository.create("fallback-repo", getFile().toPath().resolve("maven"));
            } else {
                localRepo = LocalRepository.standard();
            }
            MavenDependencyInfo dependencyInfo = MavenDependencyInfo.parseResource("dependencies.json");
            dependencyInfo.injectDependencies((URLClassLoader) getClass().getClassLoader(), (dependency) -> {
                Path path;
                if ((path = localRepo.findPath(dependency)) != null) {
                    getLogger().fine(() -> "Using cached version of " + dependency);
                    return path;
                }
                ResolvedMavenArtifact resolved = dependencyInfo.find(dependency);
                getLogger().info(() -> "Downloading " + dependency + " from " + resolved.getRepository().getName());
                return localRepo.downloadFrom(resolved);
            });
            plugin = new EchoPetPlugin(this);
        } catch (IOException | MavenException t) {
            getLogger().log(Level.SEVERE, "Unable to load libraries", t);
            setEnabled(false);
            return;
        }
        if (metrics != null) {
            metrics = new Metrics(this);
            plugin.configureMetrics(metrics);
        }
    }

    @Override
    public void onEnable() {
        plugin.onEnable();
    }

    @Override
    public void onDisable() {
        plugin.onDisable();
        metrics = null; // Free the metrics instance, in case we get reloaded
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
}
