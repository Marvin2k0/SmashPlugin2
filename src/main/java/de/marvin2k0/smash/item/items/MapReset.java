package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;
import org.bukkit.event.block.Action;

public class MapReset extends SmashItem
{

    public MapReset()
    {
        super(ItemUtils.create(Material.EMERALD, Text.get("items.reset.name", false)), Boolean.valueOf(Text.get("items.reset.activated", false)));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        player.getPlayer().setItemInHand(null);
        player.getGame().resetBlocks();
    }
}
