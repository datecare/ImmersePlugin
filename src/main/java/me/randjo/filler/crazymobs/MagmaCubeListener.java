package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import org.bukkit.event.Listener;

public class MagmaCubeListener implements Listener {

    private final MainPlugin plugin;

    public MagmaCubeListener(MainPlugin plugin) {
        this.plugin = plugin;
    }


    /*
    @EventHandler
    public void onPlayerDamageByMagma(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof MagmaCube)) {
            return;
        }

        Slime originalMagma = (MagmaCube) event.getDamager();

        Player player = (Player) event.getEntity();

        Location spawnLocation = originalMagma.getLocation();
        int size = originalMagma.getSize();

        Entity newEntity = spawnLocation.getWorld().spawnEntity(spawnLocation, originalMagma.getType());
        MagmaCube newMagma  = (MagmaCube) newEntity;
        newMagma.setSize(size);
    }
     */
}