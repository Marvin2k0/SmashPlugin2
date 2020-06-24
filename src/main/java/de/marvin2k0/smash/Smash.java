package de.marvin2k0.smash;

import de.marvin2k0.smash.characters.CharacterUtils;
import de.marvin2k0.smash.commands.SmashCommand;
import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GameListener;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.item.UseListener;
import de.marvin2k0.smash.listener.SignListener;
import de.marvin2k0.smash.utils.Locations;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Smash extends JavaPlugin
{
    public static HashMap<Player, GamePlayer> gameplayers = new HashMap<>();
    public static Smash plugin;

    public static boolean ondisable = false;

    //TODO: spawn items, 10 sec protection phase, health, cmd -> /setdrop <game>
    @Override
    public void onEnable()
    {
        plugin = this;

        Text.setUp(this);
        Locations.setUp(this);
        UseListener.setUp();

        System.out.println("Loading characters...");
        CharacterUtils.load();
        System.out.println("done!");

        addGames();

        getCommand("smash").setExecutor(new SmashCommand());
        getCommand("setspawn").setExecutor(this);
        getCommand("setlobby").setExecutor(this);
        getCommand("leave").setExecutor(this);
        getCommand("stats").setExecutor(this);
        getCommand("setranked").setExecutor(this);
        getCommand("setylevel").setExecutor(this);
        getCommand("setitemspawn").setExecutor(this);
        getCommand("startgame").setExecutor(this);
        getCommand("endgame").setExecutor(this);

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new UseListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
    }

    @Override
    public void onDisable()
    {
        ondisable = true;

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

            Game.createGame(args[0], false);

            Locations.setLocation("games." + args[0] + ".lobby", player.getLocation());
            player.sendMessage(Text.get("lobbyset").replace("%game%", args[0]));
            saveConfig();

            return true;
        }

        else if (label.equalsIgnoreCase("setspawn"))
        {
            if (args.length != 1)
            {
                player.sendMessage("§cUsage: /setspawn <game>");
                return true;
            }

            Game.createGame(args[0], false);

            Locations.setLocation("games." + args[0] + ".playerspawns." + UUID.randomUUID().toString(), player.getLocation());
            player.sendMessage(Text.get("spawnset").replace("%game%", args[0]));
            saveConfig();

            return true;
        }

        else if (label.equalsIgnoreCase("setranked"))
        {
            if (args.length != 2)
            {
                player.sendMessage("§cUsage: /setranked <game> <true|false>");
                return true;
            }

            boolean ranked = Boolean.parseBoolean(args[1]);

            getConfig().set("games." + args[0] + ".ranked", ranked);
            saveConfig();
            reloadConfig();

            player.sendMessage("§7Einstellung erfolgreich geändert!");
            return true;
        }

        else if (label.equalsIgnoreCase("setylevel"))
        {
            if (args.length != 2)
            {
                player.sendMessage("§cUsage: /setylevel <game> <Y-Koordinate>");
                return true;
            }

            int y = Integer.parseInt(args[1]);

            getConfig().set("games." + args[0] + ".level", y);
            saveConfig();
            reloadConfig();

            player.sendMessage("§7Einstellung erfolgreich geändert!");
            return true;
        }

        else if (label.equals("stats"))
        {
            OfflinePlayer target = player;

            if (args.length >= 1)
                target = Bukkit.getOfflinePlayer(args[0]);

            int kills = 0;

            if (GamePlayer.config.isSet(target.getUniqueId() + ".kills"))
                kills = GamePlayer.config.getInt(target.getUniqueId() + ".kills");

            int deaths = 0;

            if (GamePlayer.config.isSet(target.getUniqueId() + ".deaths"))
                deaths = GamePlayer.config.getInt(target.getUniqueId() + ".deaths");

            player.sendMessage(Text.get("kills").replace("%player%", target.getName()).replace("%kills%", kills + ""));
            player.sendMessage(Text.get("deaths").replace("%player%", target.getName()).replace("%deaths%", deaths + ""));
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

        else if (label.equalsIgnoreCase("setitemspawn"))
        {
            if (args.length != 1)
            {
                player.sendMessage("§cUsage: /setitemspawn <game>");
                return true;
            }

            String game = args[0];

            if (!Game.exists(game))
            {
                player.sendMessage(Text.get("nogame"));
                return true;
            }

            if (!player.isOnGround())
            {
                player.sendMessage("§cDafür musst du auf dem Boden sein!");
                return true;
            }

            Game gameObj = Game.getGameFromName(game);
            gameObj.addLocation(player.getLocation(), false);
            player.sendMessage("§aErfolgreich hinzugefügt!");
            return true;
        }

        else if (label.equalsIgnoreCase("startgame"))
        {
            if (!Game.inGame(player))
                return true;

            Game game = gameplayers.get(player).getGame();
            int time = player.getLevel();

            if (time > Integer.parseInt(Text.get("maxtime", false)))
            {
                if (game.timer != null)
                    game.timer.cancel();

                game.startGame();
                game.sendMessage(Text.get("started"));
                return true;
            }
            else if (game.players.size() < 2)
            {
                player.sendMessage(Text.get("maxplayersnotreached"));
                return true;
            }
        }

        else if (label.equalsIgnoreCase("endgame"))
        {
            if (!Game.inGame(player))
                return true;

            Game game = gameplayers.get(player).getGame();
            game.sendMessage(Text.get("forceend"));
            game.reset();
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
            Game.createGame(entry.getKey(), getConfig().getBoolean("games." + entry.getKey() + ".ranked"));
            Game game = Game.getGameFromName(entry.getKey());

            if (!getConfig().isSet("games." + entry.getKey() + ".spawns"))
                continue;

            for (Map.Entry<String, Object> entry2 : getConfig().getConfigurationSection("games." + entry.getKey() + ".spawns").getValues(false).entrySet())
                game.addLocation(Locations.get("games." + entry.getKey() + ".spawns." + entry2.getKey()), true);

            System.out.println("Loaded game " + entry.getKey());
        }
    }
}
