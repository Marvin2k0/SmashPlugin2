package de.marvin2k0.smash;

import de.marvin2k0.smash.commands.SmashCommand;
import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GameListener;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.UseListener;
import de.marvin2k0.smash.listener.SignListener;
import de.marvin2k0.smash.utils.Locations;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Smash extends JavaPlugin
{
    public static HashMap<Player, GamePlayer> gameplayers = new HashMap<>();
    public static Smash plugin;

    //TODO: spawn items, 10 sec protection phase, health, cmd -> /setdrop <game>
    @Override
    public void onEnable()
    {
        Text.setUp(this);
        Locations.setUp(this);
        UseListener.setUp();

        plugin = this;

        addGames();

        getCommand("smash").setExecutor(new SmashCommand());
        getCommand("setspawn").setExecutor(this);
        getCommand("setlobby").setExecutor(this);
        getCommand("leave").setExecutor(this);

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new UseListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
    }

    @Override
    public void onDisable()
    {
        for (Game g : Game.games)
            g.reset();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Text.get("noplayer"));
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("setlobby"))
        {
            if (args.length != 1)
            {
                player.sendMessage("§cUsage: /setlobby <game>");
                return true;
            }

            Game.createGame(args[0]);
            Locations.setLocation("games." + args[0] + ".lobby", player.getLocation());
            player.sendMessage(Text.get("lobbyset"));
            return true;
        }

        else if (label.equalsIgnoreCase("setspawn"))
        {
            if (args.length != 1)
            {
                player.sendMessage("§cUsage: /setspawn <game>");
                return true;
            }

            Game.createGame(args[0]);

            Locations.setLocation("games." + args[0] + ".spawn", player.getLocation());
            player.sendMessage(Text.get("spawnset"));
            return true;
        }

        else if (label.equalsIgnoreCase("leave"))
        {
            if (!Game.inGame(player))
            {
                player.sendMessage(Text.get("notingame"));
                return true;
            }

            GamePlayer gp = gameplayers.get(player);

            gp.getGame().leaveCommand(gp);
            return true;
        }

        player.sendMessage("§cInvalid command §4/" + label);
        return true;
    }

    private void addGames()
    {
        if (!getConfig().isSet("games"))
            return;

        Map<String, Object> section = getConfig().getConfigurationSection("games").getValues(false);

        for (Map.Entry<String, Object> entry : section.entrySet())
        {
            Game.createGame(entry.getKey());
            System.out.println("Loaded game " + entry.getKey());
        }
    }
}
