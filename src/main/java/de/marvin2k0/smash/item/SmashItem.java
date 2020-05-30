package de.marvin2k0.smash.item;

import de.marvin2k0.smash.game.GamePlayer;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class SmashItem
{
    private static HashMap<ItemStack, SmashItem> smashItems = new HashMap<>();
    private static ArrayList<ItemStack> items = new ArrayList<>();
    public static HashMap<Entity, GamePlayer> entities = new HashMap<>();

    protected ItemStack item;
    private Item itemToRemove;

    public SmashItem(ItemStack item)
    {
        try
        {
            net.minecraft.server.v1_15_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
            tag.setString(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            stack.setTag(tag);

            this.item = CraftItemStack.asBukkitCopy(stack);

            items.add(this.item);
            smashItems.put(this.item, this);
        }
        catch (Exception ignored)
        {
        }
    }

    public void drop(Location loc)
    {
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
