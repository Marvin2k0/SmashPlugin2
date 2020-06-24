package de.marvin2k0.smash.item.items;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class SingularityItem extends SmashItem
{
    public SingularityItem()
    {
        super(ItemUtils.create(Material.FIREWORK_CHARGE, Text.get("items.singularity.name", false)), Boolean.valueOf(Text.get("items.singularity.activated", false)));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {

        Location loc = player.getPlayer().getTargetBlock(nullMap, 100).getLocation();
        player.getPlayer().setItemInHand(null);

        /*
        Item item = (Item) loc.getWorld().spawnEntity(player.getPlayer().getEyeLocation(), EntityType.DROPPED_ITEM);
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setItemStack(new ItemStack(Material.FIREWORK_CHARGE));
        item.setVelocity(player.getPlayer().getLocation().getDirection());
        item.remove();
         */

        TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
        tnt.setFuseTicks(0);

        entities.put(tnt, player);

        loc.add(0, 1, 0);

        new BukkitRunnable()
        {
            int i = 0;

            @Override
            public void run()
            {
                if (i >= 20)
                    cancel();

                gravity(loc);
                loc.getWorld().playEffect(loc, Effect.SMOKE, 100);

                i++;
            }
        }.runTaskTimer(Smash.plugin, 0, 5);
    }

    private void gravity(Location loc)
    {
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 10, 10, 10))
        {
            if (!(e instanceof Player))
                continue;

            Player player = (Player) e;

            if (!Game.inGame(player))
                continue;

            GamePlayer gp = Smash.gameplayers.get(player);

            Vector vector = loc.toVector().subtract(e.getLocation().toVector());
            e.setVelocity(vector.normalize());

            gp.addDamage(.1);
            player.damage(1);
        }
    }
}
