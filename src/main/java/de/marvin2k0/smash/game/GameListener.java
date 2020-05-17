package de.marvin2k0.smash.game;

import com.sun.media.jfxmediaimpl.HostUtils;
import de.marvin2k0.smash.Smash;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GameListener implements Listener
{
    private ArrayList<Player> jump = new ArrayList<>();

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        player.setAllowFlight(false);
        event.setCancelled(true);

        if (!jump.contains(player))
        {
            jump.add(player);
            player.setVelocity(player.getLocation().getDirection().multiply(2).add(new Vector(0, 1, 0)));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        //TODO: damage items einfügen

        if (!(event.getEntity() instanceof Player))
            return;

        ItemStack item = null;

        if (event.getDamager() instanceof Player)
        {
            Player damager = (Player) event.getDamager();

           if (damager.getItemInHand() != null && damager.getItemInHand().hasItemMeta())
            item = damager.getItemInHand();
        }

        Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        event.setDamage(0);

        double damage = 0;

        if (item != null)
        {
            if (item.getType() == Material.DIAMOND_SWORD)
                damage = 0.16;
            else if (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.GOLDEN_SWORD)
                damage = 0.1;
            else if (item.getType() == Material.STONE_SWORD)
                damage = 0.12;
            else if (item.getType() == Material.IRON_SWORD)
                damage = 0.14;
        }

        GamePlayer gp = Smash.gameplayers.get(player);
        gp.addDamage(damage == 0 ? 0.05 : damage);

        String name = gp.getName();
        double damageAfter = gp.getDamage() * 100;

        Game.objective.getScore(name).setScore((int) damageAfter);

        player.setVelocity(event.getDamager().getLocation().getDirection().multiply(gp.getDamage() * 3));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (jump.contains(player) && player.isOnGround())
        {
            jump.remove(player);
            player.setAllowFlight(true);
        }
    }
}

