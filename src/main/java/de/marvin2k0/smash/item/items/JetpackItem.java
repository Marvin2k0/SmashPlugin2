package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class JetpackItem extends SmashItem
{
    public JetpackItem()
    {
        super(new ItemStack(Material.FLINT_AND_STEEL));
    }

    int max = 11;
    boolean flag = false;

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        if (flag)
            return;

        Vector velocity = player.getPlayer().getVelocity().add(new Vector(0, 1, 0));
        player.getPlayer().setVelocity(new Vector(0, 0, 0));
        player.getPlayer().setVelocity(velocity);
        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);

        flag = true;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () -> flag = false, 10);

        max--;

        if (max <= 0)
            player.getPlayer().setItemInHand(null);
    }
}
