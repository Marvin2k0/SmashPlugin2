package de.marvin2k0.smash.item.items;

import de.marvin2k0.smash.Smash;
import de.marvin2k0.smash.game.GamePlayer;
import de.marvin2k0.smash.item.SmashItem;
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
        super(new ItemStack(Material.CREEPER_SPAWN_EGG));

        monsters.add(EntityType.BLAZE);
        monsters.add(EntityType.ENDERMITE);
        monsters.add(EntityType.ZOMBIE);
        monsters.add(EntityType.MAGMA_CUBE);
        monsters.add(EntityType.CAVE_SPIDER);
        monsters.add(EntityType.CREEPER);
        monsters.add(EntityType.ENDERMAN);
        monsters.add(EntityType.GHAST);
        monsters.add(EntityType.PHANTOM);
    }

    @Override
    public void onUse(GamePlayer player, Action action)
    {
        Random random = new Random();
        int index = random.nextInt(monsters.size());

        Entity e = player.getPlayer().getLocation().getWorld().spawnEntity(player.getPlayer().getLocation(), monsters.get(index));
        e.setCustomNameVisible(true);
        e.setCustomName("§9Mob");

        Bukkit.getScheduler().runTaskLater(Smash.plugin, () -> e.remove(), 20 * 20);
    }
}
