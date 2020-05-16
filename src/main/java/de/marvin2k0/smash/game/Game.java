package de.marvin2k0.smash.game;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.utils.CountdownTimer;
import de.marvin2k0.smash.utils.Locations;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Game
{
    public static ArrayList<Game> games = new ArrayList<>();
    public static ArrayList<GamePlayer> gameplayers = new ArrayList<>();
    private static ArrayList<Player> players = new ArrayList<>();
    private static FileConfiguration config = Smash.plugin.getConfig();
    private static int MIN_PLAYERS = Integer.valueOf(Text.get("minplayers", false));
    private static int MAX_PLAYERS = Integer.valueOf(Text.get("maxplayers", false));

    private String name;
    private boolean hasStarted;
    private boolean inGame;
    public GamePlayer hunter;
    private CountdownTimer timer;

    private Game(String name)
    {
        this.name = name;
        this.hasStarted = false;
        this.inGame = false;
    }

    public void join(Player player)
    {
        if (inGame)
        {
            player.sendMessage(Text.get("alreadystarted"));
            return;
        }

        if (players.size() >= MAX_PLAYERS)
        {
            player.sendMessage(Text.get("gamefull"));
            return;
        }

        if (Smash.gameplayers.containsKey(player))
        {
            player.sendMessage(Text.get("alreadyingame"));
            return;
        }

        Smash.plugin.reloadConfig();

        if (!config.isSet("games." + getName() + ".lobby")  || !config.isSet("games." + getName() + ".spawn"))
        {
            player.sendMessage(Text.get("lobbynotset"));
            return;
        }

        players.add(player);

        GamePlayer gamePlayer = new GamePlayer(this, player, player.getLocation(), player.getInventory().getContents());
        gamePlayer.setGame(this);
        gameplayers.add(gamePlayer);
        gamePlayer.inLobby = true;

        sendMessage(Text.get("joinmessage").replace("%player%", player.getName()));
        player.teleport(Locations.get("games." + getName() + ".lobby"));
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setHealth(player.getHealthScale());

        if (players.size() >= MIN_PLAYERS && !hasStarted)
        {
            start();
        }
    }

    public void reset()
    {
        for (GamePlayer gp : gameplayers)
        {
            leave(gp, false);
        }

        Location spawn = Locations.get("games." + getName() + ".lobby");

        for (Entity e : spawn.getWorld().getNearbyEntities(spawn, 50, 50, 50))
        {
            if (e instanceof Arrow)
                e.remove();
        }

        gameplayers.clear();
        players.clear();
        hunter = null;
        hasStarted = false;
        inGame = false;
    }

    private void check()
    {

    }

    public void die(GamePlayer gp, Player player)
    {


        check();
    }

    private void startGame()
    {
        System.out.println(gameplayers.size());

        if (gameplayers.size() <= 1)
        {
            reset();
            return;
        }

        inGame = true;

        for (GamePlayer gp : gameplayers)
            gp.getPlayer().teleport(Locations.get("games." + getName() + ".spawn"));
    }

    private void start()
    {
        hasStarted = true;

        CountdownTimer timer = new CountdownTimer(Smash.plugin, 30,
                () -> {
                },
                () -> startGame(),
                (t) -> countdown(t.getSecondsLeft()));

        timer.scheduleTimer();
    }

    private void countdown(int seconds)
    {
        if (seconds <= 5)
            sendMessage(Text.get("countdown").replace("%seconds%", seconds + ""));
        else if (seconds % 5 == 0)
            sendMessage(Text.get("countdown").replace("%seconds%", seconds + ""));
    }

    public void leave(GamePlayer gp)
    {
        gameplayers.remove(gp);
        players.remove(gp.getPlayer());

        leave(gp, true);
    }

    public void leaveCommand(GamePlayer gp)
    {
        gameplayers.remove(gp);
        players.remove(gp.getPlayer());

        leave(gp, true);
    }

    public void leave(GamePlayer gp, boolean check)
    {
        Player player = gp.getPlayer();


        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        gp.teleportBack();

        if (check)
            check();
    }

    public void sendMessage(String msg)
    {
        for (Player player : players)
            player.sendMessage(msg);
    }

    public String getName()
    {
        return name;
    }

    public static Game getGameFromName(String name)
    {
        for (Game game : games)
        {
            if (game.getName().equalsIgnoreCase(name))
                return game;
        }

        return null;
    }

    public static void createGame(String name)
    {
        if (!exists(name))
        {
            Game game = new Game(name);
            games.add(game);
        }
    }

    public static boolean exists(String name)
    {
        for (Game game : games)
        {
            if (game.getName().equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    public static boolean inGame(Player player)
    {
        return Smash.gameplayers.containsKey(player);
    }
}
