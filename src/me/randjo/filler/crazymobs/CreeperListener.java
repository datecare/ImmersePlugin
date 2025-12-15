package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;

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
            creeper.setSilent(true);
            AttributeInstance speedAttribute = creeper.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

            if (speedAttribute != null) {
                // 2. Set the new base value
                // The default movement speed for a Creeper (and most mobs) is around 0.25.
                // If you want double speed, you'd use 0.5.

                double newBaseSpeed = 0.5; // e.g., 0.5 for double speed

                speedAttribute.setBaseValue(newBaseSpeed);
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