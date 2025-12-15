package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ZombieListener implements Listener {

    private final MainPlugin plugin;

    public ZombieListener(MainPlugin plugin) {
        this.plugin = plugin;
    }

    /*
    @EventHandler
    public void onZombieSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Zombie entity) {
            // Logic for Zombie here
        }
    }
     */

    // If it is a Player Zombie, drops the head on death
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Zombie zombie) {
            if (zombie.getEquipment().getHelmet() != null) {
                ItemStack helmet = zombie.getEquipment().getHelmet();
                Material m = helmet.getType();
                if (m == Material.PLAYER_HEAD) {
                    Location deathLocation = zombie.getLocation();
                    World world = deathLocation.getWorld();

                    if (world != null) {
                        world.dropItemNaturally(deathLocation, helmet);
                    }
                }
            }
        }
    }
}