package de.marvin2k0.smash.game;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class GamePlayer
{
    private static final File file = new File(Smash.plugin.getDataFolder().getPath() + "/stats.yml");
    public static final FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    private Game game;
    private double damage;
    private final Player player;
    private final Location location;
    private final ItemStack[] inventory;
    private GamePlayer lastDamage;
    private long cooldown;
    public boolean inLobby = true;
    public int lives;
    public int level;

    public GamePlayer(Game game, Player player, Location location, ItemStack[] inventory, int level)
    {
        this.game = game;
        this.player = player;
        this.location = location;
        this.inventory = inventory;
        this.cooldown = 0;
        this.level = level;
        this.lastDamage = null;
        this.damage = 0;
        this.lives = Integer.parseInt(Text.get("lives", false));

        if (!Smash.gameplayers.containsKey(player))
            Smash.gameplayers.put(player, this);
    }

    public void setLastDamage(GamePlayer gp)
    {
        this.lastDamage = gp;
    }

    public GamePlayer getLastDamage()
    {
        return this.lastDamage;
    }

    public void addDamage(double damage)
    {
        this.damage += damage;

        if (this.damage < 0)
            this.damage = 0;

        player.setLevel((int) (this.damage * 100));
        Game.objective.getScore(player.getName()).setScore((int) (getDamage() * 100));
    }

    public void reduceLives()
    {
        this.lives -= 1;
    }

    public int getLives()
    {
        return lives;
    }

    public double getDamage()
    {
        return Math.floor(damage * 100) / 100;
    }

    public void teleportBack()
    {
        player.teleport(location);
        player.getInventory().clear();
        player.getInventory().setContents(inventory);
        player.setLevel(level);
    }

    public void addKill()
    {
        int kills = 0;

        if (config.isSet(this.getPlayer().getUniqueId() + ".kills"))
            kills = config.getInt(this.getPlayer().getUniqueId() + ".kills");

        kills += 1;

        config.set(this.getPlayer().getUniqueId() + ".kills", kills);
        saveConfig();
    }

    public void addDeath()
    {
        int deaths = 0;

        if (config.isSet(this.getPlayer().getUniqueId() + ".deaths"))
            deaths = config.getInt(this.getPlayer().getUniqueId() + ".deaths");

        deaths += 1;

        config.set(this.getPlayer().getUniqueId() + ".deaths", deaths);
        saveConfig();
    }

    public void protect()
    {
        if (can())
        {
            this.cooldown = System.currentTimeMillis() + 30 * 1000;

            GameListener.glowing.add(player);
            sendMessage(Text.get("schutz"));

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    GameListener.glowing.remove(player);
                }
            }.runTaskLater(Smash.plugin, 100);
        }
        else
        {
            long left = (this.cooldown - System.currentTimeMillis()) / 1000;

            sendMessage("§7Bitte warte §c" + left + " §7Sekunden!");
        }
    }

    private boolean can()
    {
        long left = this.cooldown - System.currentTimeMillis();

        return left <= 0;
    }

    public void sendMessage(String msg)
    {
        getPlayer().sendMessage(msg);
    }

    public String getName()
    {
        return getPlayer().getName();
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public Player getPlayer()
    {
        return player;
    }

    private void saveConfig()
    {
        try
        {
            config.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
