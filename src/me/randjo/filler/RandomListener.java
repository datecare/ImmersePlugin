package me.randjo.filler;

import com.sun.tools.javac.Main;
import me.randjo.MainPlugin;
import me.randjo.economy.EconomyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Random;

public class RandomListener implements Listener {

    private final MainPlugin plugin;
    private final EconomyPlugin economyPlugin;
    private final Random r = new Random();

    public RandomListener(MainPlugin plugin, EconomyPlugin economyPlugin) {
        this.plugin = plugin;
        this.economyPlugin = economyPlugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Zombie zombie) {
            if (zombie.getEquipment().getHelmet() != null)
            {
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
        else if (e.getEntity() instanceof Pig) {
            Random r = new Random();
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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.SHORT_GRASS) {
            Random rand = new Random();
            int rand_int1 = rand.nextInt(10000);
            if (rand_int1 == 69) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "Ala te usralo, " + "dobio si 500 RSD");
                economyPlugin.addBalance(event.getPlayer().getUniqueId(), 500);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int balance = economyPlugin.getBalance(player.getUniqueId());
        if (balance > 100) {
            int lostMoney = balance / 35;
            economyPlugin.subtractBalance(player.getUniqueId(), lostMoney);
            player.sendMessage(ChatColor.YELLOW + "Na smrti izgubili ste " + ChatColor.RED + lostMoney + " RSD.");
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            int rand_int1 = r.nextInt(20);
            if (rand_int1 == 1) {
                if (event.getLocation().getY() > 50) {
                    creeper.setPowered(true);
                }
            }
        }
        if (event.getEntity() instanceof Ghast ghast) {
            double size = r.nextDouble(0.2, 0.9);
            AttributeInstance scale = ghast.getAttribute(Attribute.GENERIC_SCALE);
            scale.setBaseValue(size);
        }

        if (event.getEntity() instanceof WitherSkeleton witherSkeleton) {
            AttributeInstance maxHealth = witherSkeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            maxHealth.setBaseValue(maxHealth.getBaseValue() * 5);
            witherSkeleton.setHealth(maxHealth.getBaseValue());
            AttributeInstance baseDamage = witherSkeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            baseDamage.setBaseValue(baseDamage.getBaseValue() * 3);
            AttributeInstance knockback = witherSkeleton.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
            knockback.setBaseValue(0.55);
            // HELMET
            double chance = Math.random();
            if (chance < 0.2) {
                witherSkeleton.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
            }
            else if (chance < 0.5) {
                witherSkeleton.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            }
            else if (chance < 0.7) {
                witherSkeleton.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            }
            // CHESTPLATE
            chance = Math.random();
            if (chance < 0.2) {
                witherSkeleton.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
            }
            else if (chance < 0.5) {
                witherSkeleton.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
            }
            else if (chance < 0.7) {
                witherSkeleton.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            }
            // LEGGINGS
            chance = Math.random();
            if (chance < 0.2) {
                witherSkeleton.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
            }
            else if (chance < 0.5) {
                witherSkeleton.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            }
            else if (chance < 0.7) {
                witherSkeleton.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            }
            // BOOTS
            chance = Math.random();
            if (chance < 0.2) {
                witherSkeleton.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));
            }
            else if (chance < 0.5) {
                witherSkeleton.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
            }
            else if (chance < 0.7) {
                witherSkeleton.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            }
            // SWORD
            chance = Math.random();
            if (chance < 0.2) {
                witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                baseDamage.setBaseValue(baseDamage.getBaseValue() + 1);
            }
            else if (chance < 0.5) {
                witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                baseDamage.setBaseValue(baseDamage.getBaseValue() + 2);
            }
            else if (chance < 0.55) {
                witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
                baseDamage.setBaseValue(baseDamage.getBaseValue() + 3);
            }
            if (witherSkeleton.getEquipment().getItemInMainHand().getType() == Material.NETHERITE_SWORD)
            {
                witherSkeleton.getEquipment().setItemInMainHandDropChance(0);
            }
            else if (witherSkeleton.getEquipment().getItemInMainHand().getType() == Material.DIAMOND_SWORD) {
                witherSkeleton.getEquipment().setItemInMainHandDropChance(0.02f);
            }
        }

    }

    @EventHandler
    public void onGhastTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Ghast && event.getTarget() instanceof Player) {
            Ghast ghast = (Ghast) event.getEntity();
            Player player = (Player) event.getTarget();

            startFasterShooting(ghast);
        }
    }
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
