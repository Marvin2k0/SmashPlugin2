package de.marvin2k0.smash.item;

import de.marvin2k0.smash.game.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public abstract class SmashItem
{
    private static HashMap<ItemStack, SmashItem> smashItems = new HashMap<>();
    private static ArrayList<ItemStack> items = new ArrayList<>();
    public static ArrayList<SmashItem> instances = new ArrayList<>();
    public static HashMap<Entity, GamePlayer> entities = new HashMap<>();
    public Set<Material> nullMap = new HashSet();

    protected ItemStack item;
    private Item itemToRemove;
    private boolean activated;

    public SmashItem(ItemStack item, boolean flag)
    {
        try
        {
            this.item = item;
            items.add(item);
            smashItems.put(item, this);
            this.activated = flag;

            instances.add(this);
        }
        catch (Exception ignored)
        {
        }
    }

    public ItemStack getItemStack()
    {
        return item;
    }

    public boolean isActivated()
    {
        return this.activated;
    }

    public void drop(Location loc)
    {
        if (item == null)
        {
            System.out.println("item ist null");
            return;
        }

        itemToRemove = loc.getWorld().dropItem(loc, item);
        itemToRemove.setVelocity(new Vector(0, 0, 0));
    }

    public Item getItem()
    {
        return itemToRemove;
    }


    public abstract void onUse(GamePlayer player, Action action);

    public static SmashItem getSmashItem(ItemStack item)
    {
        return smashItems.get(item);
    }

    public static ArrayList<ItemStack> getItems()
    {
        return items;
    }
}
