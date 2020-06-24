package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Material;

public class BreadItem extends FoodItem
{
    public BreadItem()
    {
        super(ItemUtils.create(Material.BREAD, Text.get("items.food.name", false)), Boolean.valueOf(Text.get("items.food.activated", false)));
    }
}
