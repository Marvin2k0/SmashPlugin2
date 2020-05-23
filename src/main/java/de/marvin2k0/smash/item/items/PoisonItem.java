package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonItem extends SmashItem
{
    public PoisonItem()
    {
        super(new ItemStack(Material.SPLASH_POTION));

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(Color.GREEN);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 15 * 20, 1), false);
        item.setItemMeta(meta);
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {

    }
}
