package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class SlowIceItem extends SmashItem
{
    public SlowIceItem()
    {
        super(ItemUtils.create(Material.PACKED_ICE, Text.get("items.ice.name", false)), Boolean.valueOf(Text.get("items.ice.activated", false)));
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        Location loc = player.getPlayer().getEyeLocation();

        FallingBlock block = loc.getWorld().spawnFallingBlock(loc, Material.PACKED_ICE, (byte) 0);
        block.setVelocity(player.getPlayer().getLocation().getDirection());

        player.getPlayer().setItemInHand(null);
    }
}
