package de.marvin2k0.smash.game;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.characters.CharacterUtils;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.Locations;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class GameListener implements Listener
{
    private ArrayList<Player> jump = new ArrayList<>();
    private ArrayList<GamePlayer> cooldown = new ArrayList<>();
    public static ArrayList<Player> arr = new ArrayList<>();
    private static HashMap<Player, ItemStack[]> invs = new HashMap<>();

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

        if (player.hasPotionEffect(PotionEffectType.JUMP))
            return;

        if (!Game.inGame(player))
            return;

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        player.setAllowFlight(false);
        event.setCancelled(true);

        if (!jump.contains(player))
        {
            jump.add(player);
            player.setVelocity(player.getLocation().getDirection().add(new Vector(0, 1, 0)));
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

                GamePlayer gp = Smash.gameplayers.get(player);

                explode(event, gp.getGame());
            }
            else
            {
                Entity e = (Entity) fireball.getShooter();

                if (e == null)
                    return;

                if (e.getCustomName().equals("§9Mob"))
                {
                    GamePlayer gp = SmashItem.entities.get(event.getEntity());

                    if (gp == null)
                        return;

                    explode(event, gp.getGame());
                }
            }
        }
        else
        {
            if (event.getEntity().getType() == EntityType.PRIMED_TNT)
            {
                GamePlayer gp = SmashItem.entities.get(event.getEntity());

                if (gp == null)
                    return;

                explode(event, gp.getGame());
            }
            else if (event.getEntity().getCustomName().equals("§9Mob"))
            {
                GamePlayer gp = SmashItem.entities.get(event.getEntity());

                if (gp == null)
                    return;

                explode(event, gp.getGame());
            }
        }

        event.getLocation().getWorld().playSound(event.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }

    public void death(Player player)
    {
        GamePlayer gp = Smash.gameplayers.get(player);
        Game game = gp.getGame();

        game.sendMessage(Text.get("death").replace("%player%", player.getName()));

        game.die(gp);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);
        Game game = gp.getGame();

        if (gp.getLives() >= 1)
            event.setRespawnLocation(Locations.get("games." + game.getName() + ".spawn"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!Game.inGame(player))
            return;

        if (event.getCause() != EntityDamageEvent.DamageCause.VOID)
            event.setDamage(0);

        GamePlayer gp = Smash.gameplayers.get(player);

        if (player.isGlowing() && event.getCause() != EntityDamageEvent.DamageCause.VOID)
        {
            event.setCancelled(true);
            return;
        }

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

        if (player.isGlowing())
            return;

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

            if (Game.inGame(damager))
            {
                GamePlayer lastdamage = Smash.gameplayers.get(damager);
                gp.setLastDamage(lastdamage);

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        if (gp.getLastDamage() != lastdamage)
                        {
                            this.cancel();
                            return;
                        }

                        gp.setLastDamage(null);
                    }
                }.runTaskLater(Smash.plugin, 15 * 20);
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

                if (item.getDurability() >= 100)
                    GameListener.arr.remove(player);
            }
            else if (item.getType() == Material.WOODEN_SWORD || item.getType() == Material.GOLDEN_SWORD)
            {
                damage = 0.1;
                item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 4));

                if (item.getDurability() >= 100)
                    GameListener.arr.remove(player);
            }
            else if (item.getType() == Material.STONE_SWORD)
            {
                damage = 0.12;
                item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 4));

                if (item.getDurability() >= 100)
                    GameListener.arr.remove(player);
            }
            else if (item.getType() == Material.IRON_SWORD)
            {
                damage = 0.14;
                item.setDurability((short) (item.getDurability() + item.getType().getMaxDurability() / 4));

                if (item.getDurability() >= 100)
                    GameListener.arr.remove(player);
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
            return;

        if (jump.contains(player) && player.isOnGround())
        {
            jump.remove(player);
            player.setAllowFlight(true);
        }

        GamePlayer gp = Smash.gameplayers.get(player);

        if (gp.getGame().inGame && player.isOnGround() && !cooldown.contains(gp))
        {
            if (gp.getGame().itemSpawns.size() >= 5)
            {
                gp.getGame().itemSpawns.remove(0);
            }

            Location location = player.getLocation();

            for (Location loc : gp.getGame().itemSpawns)
            {
                if (loc.distance(location) <= 8)
                    break;
            }

            cooldown.add(gp);
            gp.getGame().itemSpawns.add(player.getLocation());

            Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () -> cooldown.remove(gp), 10 * 20);
        }

        Game game = Smash.gameplayers.get(player).getGame();

        if (player.getLocation().getY() <= game.getLevel())
        {
            death(player);
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

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);
        Game game = gp.getGame();

        if (item.getType() == Material.GLASS)
        {
            gp.protect();
            return;
        }

        if (game.inGame)
            return;

        if (item.getType() == Material.NETHER_STAR && item.getItemMeta().getDisplayName().equals("§9Charakter wählen"))
        {
            CharacterUtils.openInv(gp);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event)
    {
        Player player = event.getPlayer();

        if (!Game.inGame(player))
            return;

        GamePlayer gp = Smash.gameplayers.get(player);
        Game game = gp.getGame();

        if (!game.inGame)
            return;

        invs.remove(player);
        invs.put(player, player.getInventory().getContents());

        if (getFullSlots(player) >= 2)
        {
            event.setCancelled(true);
            return;
        }

        invs.remove(player);
        invs.put(player, player.getInventory().getContents());
    }

    public int getFullSlots(Player p)
    {
        if (!invs.containsKey(p))
            invs.put(p, p.getInventory().getContents());

        ItemStack[] cont = invs.get(p);
        int i = 0;

        for (ItemStack item : cont)
        {
            if (item != null && item.getType() != Material.AIR)
            {
                i++;
            }
        }

        return i;
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent event)
    {
        if (event.getEntityType() == EntityType.FALLING_BLOCK)
            event.setCancelled(true);
        else
            return;


        FallingBlock block = (FallingBlock) event.getEntity();

        if (block.getBlockData().getMaterial() == Material.PACKED_ICE)
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
                        ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 8 * 20, 128), false);
                    }
                }
            }, 1);
        }

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

            if (item.getDurability() >= 100)
                GameListener.arr.remove(player);

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

        if (event.getItemDrop().getItemStack().getType() == Material.BOW)
            player.getInventory().remove(Material.ARROW);

        event.getItemDrop().remove();

        invs.remove(player);
        invs.put(player, player.getInventory().getContents());
    }

    private void explode(EntityExplodeEvent event, Game game)
    {
        event.setYield(0);

        int i = 0;

        for (Block block : event.blockList())
        {
            game.blocks.put(block.getLocation(), block.getType());

            if (!(i % 3 == 0))
            {
                i++;
                continue;
            }

            Location loc = block.getLocation();

            FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, block.getType(), (byte) block.getData());
            fb.setDropItem(false);

            Vector vector = loc.toVector().subtract(event.getEntity().getLocation().toVector()).multiply(.2).add(new Vector(0, 1, 0));

            fb.setVelocity(vector);

            i++;
        }
    }
}

