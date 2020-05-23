package de.marvin2k0.smash.game;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.item.items.*;
import de.marvin2k0.smash.utils.CountdownTimer;
import de.marvin2k0.smash.utils.Locations;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Game
{
    public static Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    public static Objective objective = scoreboard.registerNewObjective("damage", "dummy");
    private static Team team = scoreboard.registerNewTeam("damage");
    public static HashMap<GamePlayer, ArmorStand> armorstands = new HashMap<>();
    public static ArrayList<Game> games = new ArrayList<>();
    public static ArrayList<GamePlayer> gameplayers = new ArrayList<>();
    private static ArrayList<Player> players = new ArrayList<>();
    private static FileConfiguration config = Smash.plugin.getConfig();
    private static int MIN_PLAYERS = Integer.valueOf(Text.get("minplayers", false));
    private static int MAX_PLAYERS = Integer.valueOf(Text.get("maxplayers", false));

    public ArrayList<GamePlayer> prot;
    public ArrayList<Location> itemSpawns;
    private String name;
    private boolean hasStarted;
    public boolean inGame;
    private int lastLoc;
    public GamePlayer hunter;
    private CountdownTimer timer;

    private Game(String name)
    {
        this.prot = new ArrayList<>();
        this.itemSpawns = new ArrayList<>();
        this.name = name;
        this.hasStarted = false;
        this.inGame = false;
        this.lastLoc = -1;

        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
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

        if (!config.isSet("games." + getName() + ".lobby") || !config.isSet("games." + getName() + ".spawn"))
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
            if (!(e instanceof Player))
                e.remove();
        }

        gameplayers.clear();
        players.clear();
        prot.clear();
        itemSpawns.clear();
        hunter = null;
        hasStarted = false;
        inGame = false;
    }

    private void check()
    {
        if (gameplayers.size() <= 1)
            reset();
    }

    public void die(GamePlayer gp, Player player)
    {


        check();
    }

    private void startGame()
    {
        if (gameplayers.size() <= 1)
        {
            reset();
            return;
        }

        inGame = true;

        for (GamePlayer gp : gameplayers)
        {
            team.addPlayer(gp.getPlayer());
            gp.getPlayer().setAllowFlight(true);
            setDamageTag(gp);

            gp.getPlayer().setScoreboard(scoreboard);
            gp.getPlayer().teleport(Locations.get("games." + getName() + ".spawn"));

            prot.add(gp);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () -> prot.clear(), 10 * 20);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Smash.plugin, () -> {
            if (inGame)
                spawnItems();
        }, 0, 200);
    }

    String[] smashItems = {"bow"};
    Random random = new Random();

    //smashItems = {"bow", "jetpack", "flower", "poison", "soup", "pearl", "dia", "gold", "iron", "sugar", "stone", "wood", "apple", "bread", "chicken", "pork", "steak"};

    private void spawnItems()
    {
        if (itemSpawns.size() == 0)
            return;

        int randomItem = random.nextInt(smashItems.length);
        int randomLoc = random.nextInt(itemSpawns.size());
        String itemName = smashItems[randomItem];
        SmashItem item = null;

        switch (itemName)
        {
            case "bow": item = new SimpleBowItem(); break;
            case "jetpack": item = new JetpackItem(); break;
            case "flower": item = new FireFlowerItem(); break;
            case "poison": item = new PoisonItem(); break;
            case "soup": item = new SoupItem(); break;
            case "pearl": item = new EnderpearlItem(); break;
            case "dia": item = new DiamondSword(); break;
            case "gold": item = new GoldenSword(); break;
            case "iron": item = new IronSword(); break;
            case "sugar": item = new SpeedSugar(); break;
            case "stone": item = new StoneSword(); break;
            case "wood": item = new WoodenSword(); break;
            case "apple": item = new AppleItem(); break;
            case "bread": item = new BreadItem(); break;
            case "chicken": item = new ChickenItem(); break;
            case "pork": item = new PorkItem(); break;
            case "steak": item = new SteakItem(); break;
        }

        Location dropLocation = itemSpawns.get(randomLoc);
        lastLoc = randomLoc;

        item.drop(dropLocation);
    }

    public void setDamageTag(GamePlayer gp)
    {
        String name = gp.getName();

        objective.setDisplayName("%");
        objective.getScore(name).setScore(0);

        gp.getPlayer().setScoreboard(scoreboard);
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
        player.setAllowFlight(false);
        gp.teleportBack();

        Smash.gameplayers.remove(gp.getPlayer());

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
