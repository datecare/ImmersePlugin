package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import me.randjo.Settings;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

public class CreeperListener implements Listener {

    private final MainPlugin plugin;
    private final Random r = new Random();

    public CreeperListener(MainPlugin plugin) {
        this.plugin = plugin;
    }


    // Chance that creepers spawn charged above ocean level
    @EventHandler
    public void onCreeperSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            int rand_int1 = r.nextInt(20);
            if (rand_int1 == 1) {
                if (event.getLocation().getY() > 50) {
                    creeper.setPowered(true);
                }
            }

            // Set speed between 0.25 to 0.75 (Normal to 3x)
            AttributeInstance speedAttribute = creeper.getAttribute(Attribute.MOVEMENT_SPEED);
            double newBaseSpeed = 0.25 * Math.max(1, r.nextDouble(Settings.CREEPER_MAX_SPEED_MULTIPLIER));
            speedAttribute.setBaseValue(newBaseSpeed);

            if (Math.random() < Settings.CREEPER_SILENT_CHANCE) {
                creeper.setSilent(true);
            }

        }
    }

//    @EventHandler
//    public void onCreeperPrime(CreeperPowerEvent event) {
//        Creeper creeper = event.getEntity();
//        Player target = getNearestPlayer(creeper);
//        if (target != null) {
//            creeper.setTarget(target);  // Vanilla targeting
//            Pathfinder pf = creeper.get
//            pf.moveTo(target.getLocation(), 1.2);  // Chase speed (creeper default ~0.25 walk, 1.0+ for aggressive)
//        }
//    }
}