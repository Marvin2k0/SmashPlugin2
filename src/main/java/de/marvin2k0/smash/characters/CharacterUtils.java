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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class CharacterUtils
{

    public static void openInv(GamePlayer gp)
    {
        Player player = gp.getPlayer();

        ArrayList<ItemStack> items = getHeads();

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

    private static ArrayList<ItemStack> getHeads()
    {
        FileConfiguration config = Smash.plugin.getConfig();
        ArrayList<ItemStack> items = new ArrayList<>();

        for (String name : config.getStringList("chars"))
        {
            ItemStack head = ItemUtils.create(Material.PLAYER_HEAD, "§9" + name);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
            head.setItemMeta(meta);

            items.add(head);
        }

        return items;
    }
}
