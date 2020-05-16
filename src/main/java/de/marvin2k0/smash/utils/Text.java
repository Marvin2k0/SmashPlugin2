package de.marvin2k0.smash.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Text
{
    static FileConfiguration config;
    static Plugin plugin;

    public static String get(String path)
    {
        return path.equalsIgnoreCase("prefix") ? get(path, false) : get(path, true);
    }

    public static String get(String path, boolean prefix)
    {
        return ChatColor.translateAlternateColorCodes('&', prefix ? config.getString("prefix") + " " + config.getString(path) : config.getString(path));
    }

    public static void setUp(Plugin plugin)
    {
        Text.plugin = plugin;
        Text.config = plugin.getConfig();

        config.options().copyDefaults(true);
        config.addDefault("prefix", "&8[&9Smash&8]&f");
        config.addDefault("noplayer", "&7This command is only for players");
        config.addDefault("lobbyset", "&7Lobby has been set for game &b%game%");
        config.addDefault("spawnset", "&7Spawn has been set for game &b%game%!");
        config.addDefault("alreadyingame", "&7You are already in a game!");
        config.addDefault("notingame", "&7You are not in a game!");
        config.addDefault("lobbynotset", "&7Not all spawns have been set for this game!");
        config.addDefault("joinmessage", "&7[&a+&7] &b%player% &7joined the game.");
        config.addDefault("gamefull", "&7This game is full!");
        config.addDefault("alreadystarted", "&7Game has already started!");
        config.addDefault("countdown", "&7Game starts in &9%seconds% &7seconds");
        config.addDefault("dead", "&7%player% died");
        config.addDefault("murdered", "&7%player% &7was killed by %killer%");
        config.addDefault("minplayers", 3);
        config.addDefault("maxplayers", 8);

        saveConfig();
    }

    private static void saveConfig()
    {
        plugin.saveConfig();
    }
}
