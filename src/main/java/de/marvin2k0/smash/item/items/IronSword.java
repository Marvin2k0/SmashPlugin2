package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class IronSword extends SmashItem
{
    public IronSword()
    {
        super(ItemUtils.create(Material.IRON_SWORD, Text.get("items.ironsword.name", false)), Boolean.valueOf(Text.get("items.ironsword.activated", false)));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
    }
}
