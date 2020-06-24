package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class FireFlowerItem extends SmashItem
{
    public FireFlowerItem()
    {
        super(ItemUtils.create(Material.getMaterial(38), Text.get("items.flower.name", false)), Boolean.valueOf(Text.get("items.flower.activated", false)));
    }

    int max = 5;

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        Location loc = player.getPlayer().getEyeLocation().add(0, 1, 0);

        Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
        fireball.setShooter(player.getPlayer());
        fireball.setBounce(false);

        fireball.setVelocity(loc.getDirection().normalize());

        max--;

        if (max <= 0)
            player.getPlayer().setItemInHand(null);
    }
}
