package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.block.Action;

public class ShotBowItem extends SmashItem
{
    public ShotBowItem()
    {
        super(ItemUtils.create(Material.BOW, "§9Shotbow"));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {

    }
}
