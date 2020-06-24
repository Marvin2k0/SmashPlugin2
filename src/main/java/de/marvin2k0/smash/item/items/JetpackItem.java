package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
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
        super(ItemUtils.create(Material.FLINT_AND_STEEL, Text.get("items.jetpack.name", false)), Boolean.valueOf(Text.get("items.jetpack.activated", false)));
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

        flag = true;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Smash.plugin, () -> flag = false, 10);

        max--;

        if (max <= 0)
            player.getPlayer().setItemInHand(null);
    }
}
