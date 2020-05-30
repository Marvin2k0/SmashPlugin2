package de.marvin2k0.smash.characters;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.ArrayList;
import java.util.Map;

public class CharacterUtils
{
    private static ArrayList<ItemStack> items = null;

    public static void load()
    {
        items = getHeads();

        Inventory inv = Bukkit.createInventory(null, 54, "");

        for (int i = 0; i < items.size(); i++)
        {
            inv.setItem(i, items.get(i));
        }
    }

    public static void openInv(GamePlayer gp)
    {
        Player player = gp.getPlayer();

        ScrollerInventory inv = new ScrollerInventory(items, Text.get("charinvname", false), player);
    }

    public static void setCharacter(GamePlayer player, String owner)
    {
        GameProfile gp = ((CraftPlayer) player.getPlayer()).getProfile();
        gp.getProperties().clear();
        Skin skin = new Skin(owner.replace("-", ""));

        if (skin.getSkinName() != null)
        {
            gp.getProperties().put(skin.getSkinName(), new Property(skin.getSkinName(), skin.getSkinValue(), skin.getSkinSignatur()));
        }

        try
        {
            Bukkit.getScheduler().runTaskLater(Smash.plugin, new Runnable()
            {

                @Override
                public void run()
                {
                    for (Player pl : Bukkit.getOnlinePlayers())
                    {
                        pl.hidePlayer(player.getPlayer());
                    }

                }
            }, 1);

            Bukkit.getScheduler().runTaskLater(Smash.plugin, new Runnable()
            {

                @Override
                public void run()
                {
                    for (Player pl : Bukkit.getOnlinePlayers())
                    {
                        pl.showPlayer(player.getPlayer());
                    }

                }
            }, 20);
        }
        catch (IllegalPluginAccessException e)
        {
            Bukkit.getConsoleSender().sendMessage("§4COULD NOT CHANGE PLAYER SKIN BACK TO NORMAL FOR PLAYER " + player.getName() + " PLEASE MAKE SURE YOU DON'T RELOAD THE SERVER WHEN SMASH GAMES ARE RUNNING!!!");
            player.getPlayer().kickPlayer("§4Skin error, please rejoin");
        }
    }

    private static ArrayList<ItemStack> getHeads()
    {
        FileConfiguration config = Smash.plugin.getConfig();
        ArrayList<ItemStack> items = new ArrayList<>();

        if (!config.isSet("chars"))
        {
            Bukkit.getConsoleSender().sendMessage("§4Bitte lege mindestens einen Charakter in der Config fest!");
            return new ArrayList<ItemStack>();
        }

        for (Map.Entry<String, Object> entry : config.getConfigurationSection("chars").getValues(false).entrySet())
        {
            String name;

            if (config.isSet("chars." + entry.getKey() + ".name"))
                name = config.getString("chars." + entry.getKey() + ".name");
            else
                name = entry.getKey();

            ItemStack head = ItemUtils.create(Material.PLAYER_HEAD, "§9" + name);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
            head.setItemMeta(meta);

            items.add(head);
        }

        return items;
    }
}
