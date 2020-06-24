package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;

public class ChickenItem extends FoodItem
{
    public ChickenItem()
    {
        super(ItemUtils.create(Material.COOKED_CHICKEN, Text.get("items.food.name", false)), Boolean.valueOf(Text.get("items.food.activated", false)));
    }
}
