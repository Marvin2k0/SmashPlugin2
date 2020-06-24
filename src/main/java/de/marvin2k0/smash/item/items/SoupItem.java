package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;
import org.bukkit.event.block.Action;

public class SoupItem extends FoodItem
{
    public SoupItem()
    {
        super(ItemUtils.create(Material.MUSHROOM_SOUP, Text.get("items.food.name", false)), Boolean.valueOf(Text.get("items.food.activated", false)));
        ;
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        player.addDamage(-1);
        player.getPlayer().setItemInHand(null);
    }
}
