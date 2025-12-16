package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SlimeListener implements Listener {

    private final MainPlugin plugin;

    public SlimeListener(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamageBySlime(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Slime)) {
            return;
        }

        Slime originalSlime = (Slime) event.getDamager();

        Player player = (Player) event.getEntity();
;
        Location spawnLocation = originalSlime.getLocation();
        int size = originalSlime.getSize();

        Entity newEntity = spawnLocation.getWorld().spawnEntity(spawnLocation, originalSlime.getType());
        Slime newSlime = (Slime) newEntity;
        newSlime.setSize(size); // Sets the size of the new slime
    }
}