package de.marvin2k0.smash.game;

import com.sun.media.jfxmediaimpl.HostUtils;
import de.marvin2k0.smash.Smash;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
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
    public void onExplode(EntityExplodeEvent event)
    {
        if (!(event.getEntity() instanceof Fireball))
            return;

        Fireball fireball = (Fireball) event.getEntity();

        if (!(fireball.getShooter() instanceof Player))
            return;

        Player player = (Player) fireball.getShooter();

        if (!Game.inGame(player))
            return;

        event.blockList().clear();
        event.setCancelled(true);

        event.getLocation().getWorld().playSound(event.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        event.setDamage(0);
        GamePlayer gp = Smash.gameplayers.get(player);

        if (event.getCause() == EntityDamageEvent.DamageCause.POISON)
            gp.addDamage(0.02);
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

        double damage = 0;

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

        else if (event.getDamager() instanceof Arrow)
        {
            damage = 0.2;
        }

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
                player.setVelocity(event.getDamager().getLocation().getDirection().normalize().multiply(gp.getDamage() * 3)), 1);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        ItemStack item = event.getBow();

        System.out.println("shot");

        item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 15));
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        if (event.getItem().getItemStack().getType() != Material.BOW)
            return;

        ItemStack arrows = new ItemStack(Material.ARROW);
        arrows.setAmount(15);

        Bukkit.getScheduler().runTaskLater(Smash.plugin, () -> {

            player.getInventory().addItem(arrows);
            player.updateInventory();

        }, 5);
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

