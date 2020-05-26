package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DiamondPickaxe extends SmashItem
{
    public DiamondPickaxe()
    {
        super(new ItemStack(Material.DIAMOND_PICKAXE));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        Location loc = player.getPlayer().getLocation();

        player.getPlayer().setItemInHand(null);

        Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc.clone().add(0, 2, 0), EntityType.FIREBALL);
        fireball.setVelocity(loc.getDirection().multiply(5));
        fireball.setShooter(player.getPlayer());

        Location f = player.getPlayer().getTargetBlockExact(50).getLocation();

        for (Entity e : f.getWorld().getNearbyEntities(f, 10, 10, 10))
        {
            if (e instanceof Player)
            {
                Player p = (Player) e;

                if (!Game.inGame(p))
                    continue;

                GamePlayer gp = Smash.gameplayers.get(p);
                Vector vector = f.toVector().subtract(gp.getPlayer().getLocation().toVector()).multiply(-1);

                gp.addDamage(5 / (gp.getPlayer().getLocation().distance(f) + 1) * 0.75);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () ->
                        p.setVelocity(vector.normalize().multiply(gp.getDamage() * 5 + 1).add(new Vector(0, 1, 0))), 1);
            }
        }
    }
}
