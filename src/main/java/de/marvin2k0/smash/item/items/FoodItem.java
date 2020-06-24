package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.Game;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class FoodItem extends SmashItem
{
    public FoodItem(ItemStack item, boolean flag)
    {
        super(item, flag);
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        player.addDamage(-0.1);
        player.getPlayer().setItemInHand(null);

        if (player.getDamage() <= 0)
            player.addDamage(-player.getDamage());
    }
}
