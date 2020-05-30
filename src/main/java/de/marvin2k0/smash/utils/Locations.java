package de.marvin2k0.smash.utils;

import de.marvin2k0.smash.Smash;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Locations
{
    private static Plugin plugin;

    public static Location get(String path)
    {
        Smash.plugin.reloadConfig();

        if (!Smash.plugin.getConfig().isSet(path))
            return null;

        World world = Bukkit.getWorld(Smash.plugin.getConfig().getString(path + ".world"));

        double y = Smash.plugin.getConfig().getDouble(path + ".y");
        double x = Smash.plugin.getConfig().getDouble(path + ".x");
        double z = Smash.plugin.getConfig().getDouble(path + ".z");
        double yaw = Smash.plugin.getConfig().getDouble(path + ".yaw");
        double pitch = Smash.plugin.getConfig().getDouble(path + ".pitch");

        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public static void setLocation(String path, Location location)
    {
        Smash.plugin.getConfig().set(path + ".world", location.getWorld().getName());
        Smash.plugin.getConfig().set(path + ".x", location.getX());
        Smash.plugin.getConfig().set(path + ".y", location.getY());
        Smash.plugin.getConfig().set(path + ".z", location.getZ());
        Smash.plugin.getConfig().set(path + ".yaw", location.getYaw());
        Smash.plugin.getConfig().set(path + ".pitch", location.getPitch());

        plugin.saveConfig();
    }

    public static void setUp(Plugin plugin)
    {
        Locations.plugin = plugin;
    }
}
