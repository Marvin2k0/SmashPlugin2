package de.marvin2k0.smash.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils
{
    public static ItemStack create(Material type, String name)
    {
        return create(type, (byte) 0, name);
    }

    public static ItemStack create(Material type, byte damage, String name)
    {
        ItemStack item = new ItemStack(type, 1, damage);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }
}
