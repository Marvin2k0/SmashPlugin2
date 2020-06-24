package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedSugar extends SmashItem
{
    public SpeedSugar()
    {
        super(ItemUtils.create(Material.SUGAR, Text.get("items.speed.name", false)), Boolean.valueOf(Text.get("items.speed.activated", false)));
    }

    @Override
    public void onUse(GamePlayer gp, Action action)
    {
        Player player = gp.getPlayer();
        System.out.println("used");
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));

        item.setAmount(item.getAmount() - 1);

        if (item.getAmount() <= 0)
            player.setItemInHand(null);
    }
}
