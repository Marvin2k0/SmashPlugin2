package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class RodItem extends SmashItem
{
    public RodItem()
    {
        super(ItemUtils.create(Material.FISHING_ROD, Text.get("items.rod.name", false)), Boolean.valueOf(Text.get("items.rod.activated", false)));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {

    }
}
