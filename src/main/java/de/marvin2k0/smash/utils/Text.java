package de.marvin2k0.smash.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

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
        config.addDefault("kills", "&9%player% &7hat &9%kills% &7kills.");
        config.addDefault("deaths", "&9%player% &7hat &9%deaths% &7deaths.");
        config.addDefault("noplayer", "&7Dieser Befehl ist nur für Spieler!");
        config.addDefault("lobbyset", "&7Lobby wurde für Spiel &b%game% &7gesetzt!");
        config.addDefault("spawnset", "&7Spawn wurde für Spiel &b%game% &7gesetzt!");
        config.addDefault("alreadyingame", "&7Du bist schon in einem Spiel!");
        config.addDefault("notingame", "&7Du bist in keinem Spiel!");
        config.addDefault("lobbynotset", "&7Es wurden noch nicht alle Spawns für dieses Spiel gesetzt!");
        config.addDefault("joinmessage", "&7[&a+&7] &b%player% &7hat das Spiel betreten.");
        config.addDefault("gamefull", "&7Das Spiel ist voll!");
        config.addDefault("killed", "&7Du hast &9%player% &7getötet!");
        config.addDefault("alreadystarted", "&7Das Spiel hat schon angefangen!");
        config.addDefault("countdown", "&7Spiel startet in &9%seconds% &7Sekunden");
        config.addDefault("dead", "&7%player% ist gestorben");
        config.addDefault("murdered", "&7%player% &7wurde von %killer% getötet");
        config.addDefault("charinvname", "&9Charakterauswahl");
        config.addDefault("schutzitem", "&fSchutzblase");
        config.addDefault("schutz", "&7Schutz aktiviert!");
        config.addDefault("death", "&9%player% &7ist gestorben!");
        config.addDefault("wintitle", "&9%player% &7gewinnt");
        config.addDefault("winsubtitle", "&9%player% &7 hat das Spiel gewonnen!");
        config.addDefault("lives", 3);
        config.addDefault("minplayers", 3);
        config.addDefault("maxplayers", 8);

        saveConfig();
    }

    private static void saveConfig()
    {
        plugin.saveConfig();
    }
}
