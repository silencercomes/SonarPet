/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.echopet;

import lombok.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dsh105.commodus.config.YAMLConfig;
import com.dsh105.commodus.config.YAMLConfigManager;
import com.dsh105.echopet.api.PetManager;
import com.dsh105.echopet.api.SqlPetManager;
import com.dsh105.echopet.commands.CommandComplete;
import com.dsh105.echopet.commands.PetAdminCommand;
import com.dsh105.echopet.commands.PetCommand;
import com.dsh105.echopet.commands.util.CommandManager;
import com.dsh105.echopet.commands.util.DynamicPluginCommand;
import com.dsh105.echopet.compat.api.config.ConfigOptions;
import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin;
import com.dsh105.echopet.compat.api.plugin.IPetManager;
import com.dsh105.echopet.compat.api.plugin.ISqlPetManager;
import com.dsh105.echopet.compat.api.plugin.ModuleLogger;
import com.dsh105.echopet.compat.api.plugin.uuid.UUIDMigration;
import com.dsh105.echopet.compat.api.reflection.utility.CommonReflection;
import com.dsh105.echopet.compat.api.util.Lang;
import com.dsh105.echopet.compat.api.util.Logger;
import com.dsh105.echopet.compat.api.util.ReflectionUtil;
import com.dsh105.echopet.compat.api.util.TableMigrationUtil;
import com.dsh105.echopet.compat.api.util.VersionIncompatibleCommand;
import com.dsh105.echopet.hook.VanishProvider;
import com.dsh105.echopet.hook.WorldGuardProvider;
import com.dsh105.echopet.listeners.MenuListener;
import com.dsh105.echopet.listeners.PetEntityListener;
import com.dsh105.echopet.listeners.PetOwnerListener;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.techcable.sonarpet.BootstrapedPlugin;
import net.techcable.sonarpet.EntityHook;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.HookRegistry;
import net.techcable.sonarpet.HookRegistryImpl;
import net.techcable.sonarpet.bstats.Metrics;
import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.nms.NMSPetEntity;
import net.techcable.sonarpet.utils.reflection.MinecraftReflection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public class EchoPetPlugin extends BootstrapedPlugin implements IEchoPetPlugin {


    public EchoPetPlugin(Plugin delegate) {
        super(delegate);
    }


    private static boolean isUsingNetty;

    private static PetManager MANAGER;
    private static SqlPetManager SQL_MANAGER;
    private static ConfigOptions OPTIONS;

    public static final ModuleLogger LOGGER = new ModuleLogger("SonarPet");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");

    private final HookRegistry hookRegistry = new HookRegistryImpl(this);

    private CommandManager COMMAND_MANAGER;
    private YAMLConfigManager configManager;
    private YAMLConfig petConfig;
    private YAMLConfig mainConfig;
    private YAMLConfig langConfig;
    private HikariDataSource dbPool;

    private VanishProvider vanishProvider;
    private WorldGuardProvider worldGuardProvider;

    public String cmdString = "pet";
    public String adminCmdString = "petadmin";

    // Update data
    public boolean update = false;
    public String name = "";
    public long size = 0;
    public boolean updateChecked = false;

    @Override
    public void configureMetrics(@Nonnull Metrics metrics) {
        metrics.addCustomChart(new Metrics.AdvancedPie("Pet Type") {
            @Override
            public HashMap<String, Integer> getValues(HashMap<String, Integer> valueMap) {
                Preconditions.checkState(Bukkit.isPrimaryThread(), "Can only gather stats on main thread!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    IPet pet = getPetManager().getPet(player);
                    final String petTypeName;
                    if (pet != null) {
                        petTypeName = pet.getPetType().toPrettyString();
                    } else {
                        petTypeName = "None";
                    }
                    valueMap.put(petTypeName, valueMap.getOrDefault(petTypeName, 0) + 1);
                }
                return valueMap;
            }
        });
    }

    @Override
    @SneakyThrows(IOException.class)
    public void onEnable() {
        EchoPet.setPlugin(this);
        isUsingNetty = CommonReflection.isUsingNetty();

        this.configManager = new YAMLConfigManager(this);
        COMMAND_MANAGER = new CommandManager(this);
        // Make sure that the plugin is running under the correct version to prevent errors

        if (!INMS.isSupported()) {
            EchoPet.LOG.log(ChatColor.RED + "SonarPet " + ChatColor.GOLD
                    + this.getDescription().getVersion() + ChatColor.RED
                    + " is not compatible with this version of CraftBukkit");
            EchoPet.LOG.log(ChatColor.RED + "Initialisation failed. Please update the plugin.");

            DynamicPluginCommand cmd = new DynamicPluginCommand(this.cmdString, new String[0], "", "",
                    new VersionIncompatibleCommand(this, this.cmdString, ChatColor.YELLOW +
                            "SonarPet " + ChatColor.GOLD + this.getDescription().getVersion() + ChatColor.YELLOW + " is not compatible with this version of CraftBukkit. Please update the plugin.",
                            "echopet.pet", ChatColor.YELLOW + "You are not allowed to do that."),
                    null, this);
            COMMAND_MANAGER.register(cmd);
            return;
        }

        this.loadConfiguration();

        PluginManager manager = getServer().getPluginManager();

        MANAGER = new PetManager();
        SQL_MANAGER = new SqlPetManager();

        if (OPTIONS.useSql()) {
            this.prepareSqlDatabase();
        }

        // Register custom commands
        // Command string based off the string defined in config.yml
        // By default, set to 'pet'
        // PetAdmin command draws from the original, with 'admin' on the end
        this.cmdString = OPTIONS.getCommandString();
        this.adminCmdString = OPTIONS.getCommandString() + "admin";
        DynamicPluginCommand petCmd = new DynamicPluginCommand(this.cmdString, new String[0], "Create and manage your own custom pets.", "Use /" + this.cmdString + " help to see the command list.", new PetCommand(this.cmdString), null, this);
        petCmd.setTabCompleter(new CommandComplete());
        COMMAND_MANAGER.register(petCmd);
        COMMAND_MANAGER.register(new DynamicPluginCommand(this.adminCmdString, new String[0], "Create and manage the pets of other players.", "Use /" + this.adminCmdString + " help to see the command list.", new PetAdminCommand(this.adminCmdString), null, this));

        // Initialize hook classes
        for (ClassPath.ClassInfo hookType : ClassPath.from(getClass().getClassLoader()).getTopLevelClasses("net.techcable.sonarpet.nms.entity.type")) {
            if (!hookType.load().isAnnotationPresent(EntityHook.class)) continue;
            for (EntityHookType type : hookType.load().getAnnotation(EntityHook.class).value()) {
                if (!type.isActive()) continue;
                hookRegistry.registerHookClass(type, hookType.load().asSubclass(IEntityPet.class));
            }
        }

        // Register listeners
        manager.registerEvents(new MenuListener(), this);
        manager.registerEvents(new PetEntityListener(this), this);
        manager.registerEvents(new PetOwnerListener(), this);
        //manager.registerEvents(new ChunkListener(), this);

        this.vanishProvider = new VanishProvider(this);
        this.worldGuardProvider = new WorldGuardProvider(this);
    }

    @Override
    public void onDisable() {
        if (MANAGER != null) {
            MANAGER.removeAllPets();
        }
        if (dbPool != null) {
            dbPool.close();
        }
        hookRegistry.shutdown();
        // Unregister the commands
        this.COMMAND_MANAGER.unregister();
    }

    private void loadConfiguration() {
        String[] header = {
                "SonarPet By DSH105 and Techcable",
                "---------------------",
                "Configuration for SonarPet",
                "See the SonarPet Wiki before editing this file"
        };
        try {
            mainConfig = this.configManager.getNewConfig("config.yml", header);
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.WARNING, "Configuration File [config.yml] generation failed.", e, true);
        }

        OPTIONS = new ConfigOptions(mainConfig);

        mainConfig.reloadConfig();

        try {
            petConfig = this.configManager.getNewConfig("pets.yml");
            petConfig.reloadConfig();
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.WARNING, "Configuration File [pets.yml] generation failed.", e, true);
        }

        // Make sure to convert those UUIDs!
        if (ReflectionUtil.MC_VERSION_NUMERIC >= 172 && UUIDMigration.supportsUuid() && mainConfig.getBoolean("convertDataFileToUniqueId", true) && petConfig.getConfigurationSection("autosave") != null) {
            EchoPet.LOG.info("Converting data files to UUID system...");
            UUIDMigration.migrateConfig(petConfig);
            mainConfig.set("convertDataFileToUniqueId", false);
            mainConfig.saveConfig();
        }

        String[] langHeader = {
                "SonarPet By DSH105", "---------------------",
                "Language Configuration File"
        };
        try {
            langConfig = this.configManager.getNewConfig("language.yml", langHeader);
            try {
                for (Lang l : Lang.values()) {
                    String[] desc = l.getDescription();
                    langConfig.set(l.getPath(), langConfig.getString(l.getPath(), l.toStringRaw()), desc);
                }
                langConfig.saveConfig();
            } catch (Exception e) {
                Logger.log(Logger.LogLevel.WARNING, "Configuration File [language.yml] generation failed.", e, true);
            }

        } catch (Exception e) {
            Logger.log(Logger.LogLevel.WARNING, "Configuration File [language.yml] generation failed.", e, true);
        }
        langConfig.reloadConfig();
    }

    private void prepareSqlDatabase() {
        String host = mainConfig.getString("sql.host", "localhost");
        int port = mainConfig.getInt("sql.port", 3306);
        String db = mainConfig.getString("sql.database", "EchoPet");
        String user = mainConfig.getString("sql.username", "none");
        String pass = mainConfig.getString("sql.password", "none");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db);
        config.setUsername(user);
        config.setPassword(pass);
        dbPool = new HikariDataSource(config);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS EchoPet_version3 (" +
                    "OwnerName varchar(36)," +
                    "PetType varchar(255)," +
                    "PetName varchar(255)," +
                    "PetData BIGINT," +
                    "RiderPetType varchar(255)," +
                    "RiderPetName varchar(255), " +
                    "RiderPetData BIGINT," +
                    "PRIMARY KEY (OwnerName)" +
                    ");");

            // Convert previous database versions
            TableMigrationUtil.migrateTables();
        } catch (SQLException e) {
            Logger.log(Logger.LogLevel.SEVERE, "Table generation failed [MySQL DataBase: " + db + "].", e, true);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
        }

        // Make sure to convert those UUIDs!

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("echopet")) {
            if (sender.hasPermission("echopet.petadmin")) {
                PluginDescriptionFile pdFile = this.getDescription();
                sender.sendMessage(ChatColor.RED + "-------- SonarPet --------");
                sender.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.YELLOW + "DSH105");
                sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.YELLOW + pdFile.getVersion());
                sender.sendMessage(ChatColor.GOLD + "Website: " + ChatColor.YELLOW + pdFile.getWebsite());
                sender.sendMessage(ChatColor.GOLD + "Commands are registered at runtime to provide you with more dynamic control over the command labels.");
                sender.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Command Registration:");
                sender.sendMessage(ChatColor.GOLD + "Main: " + OPTIONS.getCommandString());
                sender.sendMessage(ChatColor.GOLD + "Admin: " + OPTIONS.getCommandString() + "admin");
            } else {
                Lang.sendTo(sender, Lang.NO_PERMISSION.toString().replace("%perm%", "echopet.petadmin"));
                return true;
            }
        }
        return false;
    }

    @Override
    public YAMLConfig getPetConfig() {
        return this.petConfig;
    }

    @Override
    public YAMLConfig getMainConfig() {
        return mainConfig;
    }

    @Override
    public YAMLConfig getLangConfig() {
        return langConfig;
    }

    @Override
    public INMS getSpawnUtil() {
        return INMS.getInstance();
    }

    @Override
    public VanishProvider getVanishProvider() {
        return vanishProvider;
    }

    @Override
    public WorldGuardProvider getWorldGuardProvider() {
        return worldGuardProvider;
    }

    @Override
    public String getPrefix() {
        final String defaultPrefix = Lang.PREFIX.getDefault();
        if (langConfig == null) return defaultPrefix;
        if (Lang.PREFIX.toStringRaw().equals(defaultPrefix.trim())) {
            langConfig.set(Lang.PREFIX.getPath(), defaultPrefix, Lang.PREFIX.getDescription());
            return defaultPrefix;
        }
        return Lang.PREFIX.toString();
    }

    public static PetManager getManager() {
        return MANAGER;
    }

    @Override
    public HookRegistry getHookRegistry() {
        return this.hookRegistry;
    }

    @Override
    public IPetManager getPetManager() {
        return MANAGER;
    }

    @Override
    public ConfigOptions getOptions() {
        return OPTIONS;
    }

    @Override
    public ISqlPetManager getSqlPetManager() {
        return SQL_MANAGER;
    }

    @Override
    public HikariDataSource getDbPool() {
        return dbPool;
    }

    @Override
    public String getCommandString() {
        return cmdString;
    }

    @Override
    public String getAdminCommandString() {
        return adminCmdString;
    }

    @Override
    public boolean isUsingNetty() {
        return isUsingNetty;
    }

    @Override
    public boolean isUpdateAvailable() {
        return update;
    }

    @Override
    public String getUpdateName() {
        return name;
    }

    @Override
    public long getUpdateSize() {
        return size;
    }

    @Nullable
    @Override
    public IEntityPet getPetEntity(Entity e) {
        Object handle = MinecraftReflection.getHandle(e);
        if (handle instanceof NMSPetEntity) {
            return ((NMSPetEntity) handle).getHook();
        } else {
            return null;
        }
    }
}
