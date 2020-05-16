package de.marvin2k0.smash.game;

import de.marvin2k0.smash.Smash;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GamePlayer
{
    private Game game;
    private Player player;
    private Location location;
    private ItemStack[] inventory;
    public boolean inLobby = true;
    private String role;

    public GamePlayer(Game game, Player player, Location location, ItemStack[] inventory)
    {
        this.game = game;
        this.player = player;
        this.location = location;
        this.inventory = inventory;
        this.role = "Zufällig";

        if (!Smash.gameplayers.containsKey(player))
            Smash.gameplayers.put(player, this);
    }

    public String getRole()
    {
        return role;
    }

    public void addHase()
    {
        Smash.plugin.getConfig().set("stats." + player.getUniqueId() + ".hase", Smash.plugin.getConfig().getInt("stats." + player.getUniqueId() + ".hase") + 1);
        Smash.plugin.saveConfig();
    }

    public void addHunter()
    {
        Smash.plugin.getConfig().set("stats." + player.getUniqueId() + ".hunter", Smash.plugin.getConfig().getInt("stats." + player.getUniqueId() + ".hunter") + 1);
        Smash.plugin.saveConfig();
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
