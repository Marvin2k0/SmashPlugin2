package de.marvin2k0.smash.item;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class UseListener implements Listener
{
    private static ArrayList<Material> ignore = new ArrayList<>();

    public static void setUp()
    {
        ignore.add(Material.BOW);
        ignore.add(Material.FISHING_ROD);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event)
    {
        if (!event.hasItem())
            return;

        ItemStack item = event.getItem();

        if (!SmashItem.getItems().contains(item))
            return;

        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        if (ignore.contains(item.getType()))
            return;


        SmashItem smashItem = SmashItem.getSmashItem(item);
        GamePlayer gp = Smash.gameplayers.get(player);

        if (event.getItem().getType() != Material.ENDER_PEARL)
            event.setCancelled(true);

        try
        {
            smashItem.onUse(gp, event.getAction());
        }
        catch (NullPointerException ignored)
        {
        }
    }
}
