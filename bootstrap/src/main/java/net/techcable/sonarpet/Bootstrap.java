package net.techcable.sonarpet;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.dsh105.echopet.EchoPetPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    private BootstrapedPlugin plugin;

    @Override
    public void onLoad() {
        int javaVersion;
        try {
            javaVersion = Integer.parseInt(System.getProperty("java.version").split("\\.")[1]);
        } catch (NumberFormatException e) {
            getLogger().warning("Unable to parse java version " + System.getProperty("java.version"));
            getLogger().warning("Assuming Java 8.");
            javaVersion = 8;
        }
        if (javaVersion < 8) {
            getLogger().severe("Your server is running java " + javaVersion + ", but java 7 is required to run SonarPets!");
            getLogger().severe("Either update your java or uninstall SonarPets!");
            getLogger().severe("Shutting down.");
            getServer().shutdown();
            return;
        }
        plugin = new EchoPetPlugin(this);
        plugin.onLoad();
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
}
