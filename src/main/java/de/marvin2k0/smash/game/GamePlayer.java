package de.marvin2k0.smash.game;

import de.marvin2k0.smash.Smash;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GamePlayer
{
    private Game game;
    private double damage;
    private Player player;
    private Location location;
    private ItemStack[] inventory;
    public boolean inLobby = true;

    public GamePlayer(Game game, Player player, Location location, ItemStack[] inventory)
    {
        this.game = game;
        this.player = player;
        this.location = location;
        this.inventory = inventory;
        this.damage = 0;

        if (!Smash.gameplayers.containsKey(player))
            Smash.gameplayers.put(player, this);
    }

    public void addDamage(double damage)
    {
        this.damage += damage;

        if (this.damage < 0)
            this.damage = 0;
    }

    public double getDamage()
    {
        return Double.valueOf(Math.floor(damage * 100) / 100);
    }

    public void teleportBack()
    {
        player.teleport(location);
        player.getInventory().clear();
        player.getInventory().setContents(inventory);
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
        this.game  = game;
    }

    public Player getPlayer()
    {
        return player;
    }
}
