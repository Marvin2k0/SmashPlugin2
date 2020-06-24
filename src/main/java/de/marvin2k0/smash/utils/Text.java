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
        if (config.getString(path).contains("&"))
            return ChatColor.translateAlternateColorCodes('&', (prefix ? config.getString("prefix") + " " + config.getString(path) : config.getString(path)));
        else
            return (prefix ? ChatColor.translateAlternateColorCodes('&', config.getString("prefix")) + " " + config.getString(path) : config.getString(path));
    }

    public static void setUp(Plugin plugin)
    {
        Text.plugin = plugin;
        Text.config = plugin.getConfig();

        config.options().copyDefaults(true);
        config.addDefault("prefix", "&b[Smash&8]&f");
        config.addDefault("kills", "&9%player% &7hat &9%kills% &7kills.");
        config.addDefault("deaths", "&9%player% &7hat &9%deaths% &7deaths.");
        config.addDefault("noplayer", "&7Dieser Befehl ist nur für Spieler");
        config.addDefault("nogame", "&7Dieses Spiel existiert nicht!");
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
        config.addDefault("maxplayersnotreached", "&7Dafür befinden sich nicht genug Spieler in der Runde!");
        config.addDefault("maxtime", 15);
        config.addDefault("started", "&7Das Spiel beginnt!");
        config.addDefault("pickupcooldown", 5);
        config.addDefault("forceend", "&7Spiel wurde beendet!");
        config.addDefault("break-sensitivity", 0.8);
        config.addDefault("lives", 3);
        config.addDefault("minplayers", 3);
        config.addDefault("maxplayers", 8);
        config.addDefault("itemsintervall", 5);

        config.addDefault("items.food.name", "Food");
        config.addDefault("items.food.activated", true);

        config.addDefault("items.rocketlauncher.name", "Rocketlauncher");
        config.addDefault("items.rocketlauncher.activated", true);

        config.addDefault("items.diamondsword.name", "Sword");
        config.addDefault("items.diamondsword.activated", true);

        config.addDefault("items.ironsword.name", "Sword");
        config.addDefault("items.ironsword.activated", true);

        config.addDefault("items.goldsword.name", "Sword");
        config.addDefault("items.goldsword.activated", true);

        config.addDefault("items.stonesword.name", "Sword");
        config.addDefault("items.stonesword.activated", true);

        config.addDefault("items.woodsword.name", "Sword");
        config.addDefault("items.woodsword.activated", true);

        config.addDefault("items.enderpearl.name", "Pearl");
        config.addDefault("items.enderpearl.activated", true);

        config.addDefault("items.flower.name", "Fire Flower");
        config.addDefault("items.flower.activated", true);

        config.addDefault("items.jetpack.name", "Jetpack");
        config.addDefault("items.jetpack.activated", true);

        config.addDefault("items.reset.name", "Reset Map");
        config.addDefault("items.reset.activated", true);

        config.addDefault("items.spawner.name", "Pokéball");
        config.addDefault("items.spawner.activated", true);

        config.addDefault("items.rod.name", "Rod");
        config.addDefault("items.rod.activated", true);

        config.addDefault("items.singularity.name", "Singularity Bomb");
        config.addDefault("items.singularity.activated", true);

        config.addDefault("items.ice.name", "Ice");
        config.addDefault("items.ice.activated", true);

        config.addDefault("items.speed.name", "Speed");
        config.addDefault("items.speed.activated", true);

        config.addDefault("items.tnt.name", "TNT");
        config.addDefault("items.tnt.activated", true);

        saveConfig();
    }

    private static void saveConfig()
    {
        plugin.saveConfig();
    }
}
