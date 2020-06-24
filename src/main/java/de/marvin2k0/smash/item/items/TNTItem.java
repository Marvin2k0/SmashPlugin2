package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TNTItem extends SmashItem
{
    private ArrayList<Location> locs = new ArrayList<>();

    public TNTItem()
    {
        super(ItemUtils.create(Material.TNT, Text.get("items.tnt.name", false)), Boolean.valueOf(Text.get("items.tnt.activated", false)));
    }

    public void onUse(GamePlayer player, Action action)
    {
        player.getPlayer().setItemInHand(null);
        Location interacted = player.getPlayer().getTargetBlock(nullMap, 8).getLocation();

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

        time(player);
    }

    private void time(GamePlayer gamePlayer)
    {
        Bukkit.getScheduler().runTaskLater(Smash.plugin, () -> {
            Location loc = null;

            for (Location l : locs)
            {
                loc = l;
                l.getBlock().setType(Material.AIR);
            }

            TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
            tnt.setFuseTicks(0);

            entities.put(tnt, gamePlayer);

            for (Entity e : loc.getWorld().getNearbyEntities(loc, 10, 10, 10))
            {
                if (e instanceof Player)
                {
                    Player player = (Player) e;

                    if (!Game.inGame(player))
                        continue;

                    GamePlayer gp = Smash.gameplayers.get(player);
                    Vector vector = loc.toVector().subtract(player.getLocation().toVector()).multiply(-1);

                    gp.addDamage(5 / (player.getLocation().distance(loc) + 1)  * 0.75);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () ->
                            player.setVelocity(vector.normalize().multiply(gp.getDamage() * 5 + 1).add(new Vector(0, 1, 0))), 1);
                }
            }
        }, 5 * 20);
    }
}
