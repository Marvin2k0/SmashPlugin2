package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TNTItem extends SmashItem
{
    private ArrayList<Location> locs = new ArrayList<>();

    public TNTItem()
    {
        super(new ItemStack(Material.TNT));
    }

    @Override
    public void onUse(GamePlayer player, Location interacted, Action action)
    {
        player.getPlayer().setItemInHand(null);

        for (int x = 0; x < 2; x++)
        {
            for (int y = 1; y < 3; y++)
            {
                for (int z = 0; z < 2; z++)
                {
                    if (interacted.clone().add(x, y, z).getBlock().getType() == Material.AIR)
                    {
                        locs.add(new Location(interacted.getWorld(), (int) interacted.getX() + x, (int) interacted.getY() + y, (int) interacted.getZ() + z));

                        interacted.getWorld().getBlockAt((int) interacted.getX() + x, (int) interacted.getY() + y, (int) interacted.getZ() + z).setType(Material.TNT);
                    }
                }
            }
        }

        time();
    }

    private void time()
    {
        Bukkit.getScheduler().runTaskLater(Smash.plugin, () -> {
            Location loc = null;

            for (Location l : locs)
            {
                loc = l;
                l.getBlock().setType(Material.AIR);
            }

            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 100, 100);
            loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1000);

            for (Entity e : loc.getWorld().getNearbyEntities(loc, 10, 10, 10))
            {
                if (e instanceof Player)
                {
                    Player player = (Player) e;

                    if (!Game.inGame(player))
                        continue;

                    System.out.println(player.getName());

                    GamePlayer gp = Smash.gameplayers.get(player);
                    Vector vector = loc.toVector().subtract(player.getLocation().toVector()).multiply(-1);

                    gp.addDamage(5 / (player.getLocation().distance(loc) + 1)  * 0.75);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () ->
                            player.setVelocity(vector.normalize().multiply(gp.getDamage() * 5 + 1).add(new Vector(0, 1, 0))), 1);
                }
            }
        }, 5 * 20);
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {

    }
}
