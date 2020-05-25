package de.marvin2k0.smash.game;

import com.sun.media.jfxmediaimpl.HostUtils;
import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.characters.CharacterUtils;
import de.marvin2k0.smash.utils.Text;
import net.minecraft.server.v1_15_R1.EntityArrow;
import org.apache.commons.lang.CharUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

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

        if (player.getItemInHand().getType() != Material.AIR)
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
        if (event.getEntity() instanceof Fireball)
        {
            Fireball fireball = (Fireball) event.getEntity();

            if (fireball.getShooter() instanceof Player)
            {
                Player player = (Player) fireball.getShooter();

                if (!Game.inGame(player))
                    return;

                event.setCancelled(true);
            }
            else
            {
                Entity e = (Entity) fireball.getShooter();

                if (e.getCustomName().equals("§9Mob"))
                {
                    event.setCancelled(true);
                }
            }
        }
        else
        {
            if (event.getEntity().getCustomName().equals("§9Mob"))
                event.setCancelled(true);
        }

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

        item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 15));

        if (!item.getItemMeta().getDisplayName().equals("§9Shotbow"))
            return;
        else
        {
            for (int i = 0; i < 9; i++)
            {
                Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            }
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        if (event.getItem().getItemStack().getType() == Material.ARROW)
        {
            event.setCancelled(true);
            return;
        }

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

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);

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

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (!event.hasItem())
            return;

        ItemStack item = event.getItem();

        if (!item.hasItemMeta())
            return;

        if (item.getType() != Material.NETHER_STAR && item.getItemMeta().getDisplayName().equals("§9Charakter wählen"))
            return;

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);
        Game game = gp.getGame();

        if (game.inGame)
            return;

        CharacterUtils.openInv(gp);
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent event)
    {
        Bukkit.getScheduler().runTaskLater(Smash.plugin, () -> {
            event.getBlock().breakNaturally();
            Location loc = event.getBlock().getLocation();
            loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1, 1);

            for (Entity e : loc.getWorld().getNearbyEntities(loc, 4, 4, 4))
            {
                if (e instanceof Player)
                {
                    ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8 * 20, 3), false);
                }
            }
        }, 1);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);

        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY)
        {
            if (!(event.getCaught() instanceof Player))
                return;

            Player p = (Player) event.getCaught();

            if (!Game.inGame(p))
                return;

            GamePlayer caught = Smash.gameplayers.get(p);
            ItemStack item = player.getItemInHand();
            item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 3));

            Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () ->
                    p.setVelocity(player.getLocation().getDirection().normalize().multiply(caught.getDamage() * -5 - 0.5)), 1);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);
        gp.getGame().leaveCommand(gp);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);
        Game game = gp.getGame();

        if (game.inGame)
            return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null)
            return;

        ItemStack item = event.getCurrentItem();

        if (item.getType() == Material.PLAYER_HEAD && event.getView().getTitle().equals(Text.get("charinvname", false)))
        {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            String owner = meta.getOwningPlayer().getUniqueId().toString();

            CharacterUtils.setCharacter(gp, owner);
        }

        player.closeInventory();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        event.setCancelled(true);
    }
}

