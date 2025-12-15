package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class PigListener implements Listener {

    private final MainPlugin plugin;
    private final Random r = new Random();


    public PigListener(MainPlugin plugin) {
        this.plugin = plugin;
    }

    /*
    @EventHandler
    public void onPigSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Pig entity) {
            // Logic for Pig here
        }
    }
     */

    // Piglin Brute event on Pig death
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Pig) {
            int chance = r.nextInt(100);
            if (chance == 1) {
                Location l = e.getEntity().getLocation();
                e.getEntity().getWorld().strikeLightning(l);
                e.getEntity().getWorld().spawnEntity(l, EntityType.PIGLIN_BRUTE);
                e.getEntity().getWorld().spawnEntity(l, EntityType.PIGLIN_BRUTE);
                e.getEntity().getWorld().spawnEntity(l, EntityType.PIGLIN_BRUTE);
            }
        }
    }
}