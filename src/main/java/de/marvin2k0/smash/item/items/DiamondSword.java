package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class DiamondSword extends SmashItem
{
    public DiamondSword()
    {
        super(ItemUtils.create(Material.DIAMOND_SWORD, Text.get("items.diamondsword.name", false)), Boolean.valueOf(Text.get("items.diamondsword.activated", false)));
    }
    @Override
    public void onUse(GamePlayer player, Action action)
    {
    }
}
