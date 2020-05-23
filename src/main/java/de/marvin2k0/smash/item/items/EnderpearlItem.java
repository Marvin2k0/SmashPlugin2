package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class EnderpearlItem extends SmashItem
{
    public EnderpearlItem()
    {
        super(new ItemStack(Material.ENDER_PEARL));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {

    }
}
