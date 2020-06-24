package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
import de.marvin2k0.smash.utils.ItemUtils;
import de.marvin2k0.smash.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class MonsterSpawnItem extends SmashItem
{
    private ArrayList<EntityType> monsters = new ArrayList<>();

    public MonsterSpawnItem()
    {
        super(ItemUtils.create(Material.MONSTER_EGG, Text.get("items.spawner.name", false)), Boolean.valueOf(Text.get("items.spawner.activated", false)));

        monsters.add(EntityType.BLAZE);
        monsters.add(EntityType.ENDERMITE);
        monsters.add(EntityType.ZOMBIE);
        monsters.add(EntityType.MAGMA_CUBE);
        monsters.add(EntityType.CAVE_SPIDER);
        monsters.add(EntityType.CREEPER);
        monsters.add(EntityType.ENDERMAN);
        monsters.add(EntityType.GHAST);
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        player.getPlayer().setItemInHand(null);

        Random random = new Random();
        int index = random.nextInt(monsters.size());

        Entity e = player.getPlayer().getLocation().getWorld().spawnEntity(player.getPlayer().getLocation(), monsters.get(index));
        e.setCustomNameVisible(true);
        e.setCustomName("§9Mob");

        entities.put(e, player);

        Bukkit.getScheduler().runTaskLater(Smash.plugin, () -> e.remove(), 20 * 20);
    }
}
