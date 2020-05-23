package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import org.bukkit.Material;
import org.bukkit.event.block.Action;

public class SoupItem extends FoodItem
{
    public SoupItem()
    {
        super(Material.LEGACY_MUSHROOM_SOUP);
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        player.addDamage(-1);
        player.getPlayer().setItemInHand(null);
    }
}
