package de.marvin2k0.smash.item;

import de.marvin2k0.smash.game.GamePlayer;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class SmashItem
{
    private static HashMap<ItemStack, SmashItem> smashItems = new HashMap<>();
    private static ArrayList<ItemStack> items = new ArrayList<>();

    protected ItemStack item;

    public SmashItem(ItemStack item)
    {
        net.minecraft.server.v1_15_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
        tag.setString(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        stack.setTag(tag);

        this.item = CraftItemStack.asBukkitCopy(stack);

        items.add(this.item);
        smashItems.put(this.item, this);
    }

    public void drop(Location loc)
    {
        loc.getWorld().dropItemNaturally(loc, item);
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
