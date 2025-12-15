package me.randjo.filler.crazymobs;

import me.randjo.MainPlugin;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class WitherSkeletonListener implements Listener {

    private final MainPlugin plugin;

    public WitherSkeletonListener(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherSkeletonSpawn(CreatureSpawnEvent event) {
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
}