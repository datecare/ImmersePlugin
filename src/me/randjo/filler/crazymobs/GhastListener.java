package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class GhastListener implements Listener {

    private final MainPlugin plugin;
    private final Random r = new Random();

    public GhastListener(MainPlugin plugin) {
        this.plugin = plugin;
    }

    // Randomize Ghast size on spawn
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Ghast ghast) {
            double size = r.nextDouble(0.2, 0.9);
            AttributeInstance scale = ghast.getAttribute(Attribute.GENERIC_SCALE);
            scale.setBaseValue(size);
        }
    }

    // Starts Ghast barrage if target is Player
    @EventHandler
    public void onGhastTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Ghast && event.getTarget() instanceof Player) {
            Ghast ghast = (Ghast) event.getEntity();
            Player player = (Player) event.getTarget();

            startFasterShooting(ghast);
        }
    }

    // Shoots faster depending on Ghast size
    public void startFasterShooting(Ghast ghast) {
        double size = ghast.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();

        new BukkitRunnable() {
            boolean firstShotFired = false;

            @Override
            public void run() {
                if (ghast.isDead() || !(ghast.getTarget() instanceof Player)) {
                    cancel();
                    return;
                }

                if (!firstShotFired) {
                    firstShotFired = true;
                    Fireball fireball = ghast.launchProjectile(Fireball.class);
                    fireball.setVelocity(ghast.getTarget().getLocation().toVector().subtract(ghast.getLocation().toVector()).normalize().multiply(1));
                }

                // Fire a fireball at a faster rate (every 10 ticks = 0.5 seconds)
                Fireball fireball = ghast.launchProjectile(Fireball.class);
                fireball.setVelocity(ghast.getTarget().getLocation().toVector().subtract(ghast.getLocation().toVector()).normalize().multiply(1));
            }
        }.runTaskTimer(plugin, 0L, Math.round(size*60)); // Fires every 10 ticks (0.5 seconds)
    }
}