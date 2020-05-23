package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class SimpleBowItem extends SmashItem
{
    public SimpleBowItem()
    {
        super(new ItemStack(Material.BOW));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {

    }
}
