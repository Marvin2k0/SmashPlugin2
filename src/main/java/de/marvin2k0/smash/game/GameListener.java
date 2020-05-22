package de.marvin2k0.smash.game;

import com.sun.media.jfxmediaimpl.HostUtils;
import de.marvin2k0.smash.Smash;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GameListener implements Listener
{
    private ArrayList<Player> jump = new ArrayList<>();
    private ArrayList<GamePlayer> cooldown = new ArrayList<>();

    @EventHandler
    public void onGrap(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);

        if (!gp.getGame().inGame)
            return;

        if (!(event.getRightClicked() instanceof Player))
            return;

        Player target = (Player) event.getRightClicked();

        if (!Game.inGame(target))
            return;

        player.setPassenger(target);
    }

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
        if (!(event.getEntity() instanceof Player))
            return;

        ItemStack item = null;

        Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);

        if (!gp.getGame().inGame)
        {
            event.setCancelled(true);
            return;
        }

        event.setDamage(0);

        if (event.getDamager() instanceof Player)
        {
            Player damager = (Player) event.getDamager();

            if (damager.getItemInHand() != null && damager.getItemInHand().hasItemMeta())
                item = damager.getItemInHand();

            for (Entity e : damager.getPassengers())
            {
                if (!(e instanceof Player))
                    continue;

                final Player p = (Player) e;

                if (p.getUniqueId().equals(player.getUniqueId()))
                    damager.removePassenger(p);
            }
        }

        double damage = 0;

        if (item != null)
        {
            if (item.getType() == Material.DIAMOND_SWORD)
            {
                damage = 0.16;
                item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 4));
            }
            else if (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.GOLDEN_SWORD)
            {
                damage = 0.1;
                item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 4));
            }
            else if (item.getType() == Material.STONE_SWORD)
            {
                damage = 0.12;
                item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 4));
            }
            else if (item.getType() == Material.IRON_SWORD)
            {
                damage = 0.14;
                item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 4));
            }
        }

        gp.addDamage(damage == 0 ? 0.05 : damage);

        String name = gp.getName();
        double damageAfter = gp.getDamage() * 100;

        Game.objective.getScore(name).setScore((int) damageAfter);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () ->
                player.setVelocity(event.getDamager().getLocation().getDirection().multiply(gp.getDamage() * 3)), 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);

        if (!gp.getGame().inGame)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);

        if (!gp.getGame().inGame)
            event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        final Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);

        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
        {
            player.setAllowFlight(false);
            return;
        }

        if (jump.contains(player) && player.isOnGround())
        {
            jump.remove(player);
            player.setAllowFlight(true);
        }

        GamePlayer gp = Smash.gameplayers.get(player);

        if (gp.getGame().inGame && player.isOnGround() && !cooldown.contains(gp))
        {
            if (gp.getGame().itemSpawns.size() >= 10)
            {
                gp.getGame().itemSpawns.remove(0);
            }

            Location location = player.getLocation();

            for (Location loc : gp.getGame().itemSpawns)
            {
                if (loc.distance(location) <= 8)
                    return;
            }

            cooldown.add(gp);
            gp.getGame().itemSpawns.add(player.getLocation());

            Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () -> cooldown.remove(gp), 10 * 20);
        }
    }
}

