package me.randjo.economy;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Material.*;

public class PlayerGainMoney implements Listener {
    private final EconomyPlugin plugin;
    private final Map<Class<? extends Entity>, Integer> rewards;
    private final Map<Material, Integer> rewardsBlock;
    private File placedBlocksFile;
    private FileConfiguration placedBlocksConfig;
    private static Set<String> placedBlocks = new HashSet<>();

    public PlayerGainMoney(EconomyPlugin plugin) {
        this.plugin = plugin;
        // Initialize the rewards map
        rewards = new HashMap<>();
        rewards.put(Allay.class, 50);
        rewards.put(Armadillo.class, 2);
        rewards.put(Axolotl.class, 1);
        rewards.put(Bat.class, 2);
        rewards.put(Camel.class, 2);
        rewards.put(Cat.class, 1);
        rewards.put(Chicken.class, 1);
        rewards.put(Cod.class, 1);
        rewards.put(Donkey.class, 1);
        rewards.put(Frog.class, 1);
        rewards.put(GlowSquid.class, 2);
        rewards.put(Horse.class, 1);
        rewards.put(MushroomCow.class, 2);
        rewards.put(Mule.class, 2);
        rewards.put(Ocelot.class, 1);
        rewards.put(Parrot.class, 2);
        rewards.put(PufferFish.class, 3);
        rewards.put(Rabbit.class, 1);
        rewards.put(Salmon.class, 1);
        rewards.put(SkeletonHorse.class, 50);
        rewards.put(Sniffer.class, 20);
        rewards.put(Snowman.class, 1);
        rewards.put(Squid.class, 1);
        rewards.put(Strider.class, 5);
        rewards.put(Tadpole.class, 1);
        rewards.put(TropicalFish.class, 5);
        rewards.put(Turtle.class, 3);
        rewards.put(Villager.class, -10);
        rewards.put(WanderingTrader.class, 20);
        rewards.put(Bee.class, 1);
        rewards.put(CaveSpider.class, 3);
        rewards.put(Dolphin.class, 5);
        rewards.put(Drowned.class, 3);
        rewards.put(Goat.class, 1);
        rewards.put(IronGolem.class, 10);
        rewards.put(Llama.class, 2);
        rewards.put(Panda.class, 10);
        rewards.put(Fox.class, 1);
        rewards.put(Piglin.class, 5);
        rewards.put(PolarBear.class, 5);
        rewards.put(Spider.class, 2);
        rewards.put(TraderLlama.class, 1);
        rewards.put(Wolf.class, -15);
        rewards.put(PigZombie.class, 3);
        rewards.put(Blaze.class, 10);
        rewards.put(Bogged.class, 3);
        rewards.put(Breeze.class, 5);
        rewards.put(ElderGuardian.class, 50);
        rewards.put(Endermite.class, 5);
        rewards.put(Evoker.class, 10);
        rewards.put(Ghast.class, 15);
        rewards.put(Guardian.class, 3);
        rewards.put(Hoglin.class, 5);
        rewards.put(Husk.class, 4);
        rewards.put(MagmaCube.class, 4);
        rewards.put(Phantom.class, 8);
        rewards.put(PiglinBrute.class, 7);
        rewards.put(Pillager.class, 5);
        rewards.put(Ravager.class, 15);
        rewards.put(Shulker.class, 10);
        rewards.put(Silverfish.class, 1);
        rewards.put(Slime.class, 3);
        rewards.put(Stray.class, 4);
        rewards.put(Vex.class, 1);
        rewards.put(Vindicator.class, 5);
        rewards.put(Warden.class, 200);
        rewards.put(Witch.class, 20);
        rewards.put(WitherSkeleton.class, 7);
        rewards.put(Zoglin.class, 5);
        rewards.put(ZombieVillager.class, 5);
        rewards.put(EnderDragon.class, 1000);
        rewards.put(Wither.class, 1000);
        rewards.put(Zombie.class, 3);
        rewards.put(Skeleton.class, 3);
        rewards.put(Creeper.class, 8);
        rewards.put(Enderman.class, 15);

        rewardsBlock = new HashMap<>();

        rewardsBlock.put(NETHER_GOLD_ORE, 1);
        rewardsBlock.put(COAL_ORE, 1);
        rewardsBlock.put(DEEPSLATE_IRON_ORE, 2);
        rewardsBlock.put(COPPER_ORE, 1);
        rewardsBlock.put(DEEPSLATE_COPPER_ORE, 1);
        rewardsBlock.put(IRON_ORE, 2);
        rewardsBlock.put(NETHER_QUARTZ_ORE, 2);
        rewardsBlock.put(DEEPSLATE_REDSTONE_ORE, 2);
        rewardsBlock.put(REDSTONE_ORE, 2);
        rewardsBlock.put(GOLD_ORE, 3);
        rewardsBlock.put(DEEPSLATE_GOLD_ORE, 3);
        rewardsBlock.put(LAPIS_ORE, 2);
        rewardsBlock.put(DEEPSLATE_LAPIS_ORE, 2);
        rewardsBlock.put(DEEPSLATE_DIAMOND_ORE, 5);
        rewardsBlock.put(EMERALD_ORE, 10);
        rewardsBlock.put(DEEPSLATE_EMERALD_ORE, 20);
        rewardsBlock.put(DEEPSLATE_COAL_ORE, 30);
        rewardsBlock.put(ANCIENT_DEBRIS, 10);
        rewardsBlock.put(DIAMOND_ORE, 10);
        rewardsBlock.put(GOLD_BLOCK, 20);


        rewardsBlock.put(SPAWNER, 100);

        placedBlocksFile = new File("plugins/ImmensePlugin", "placed_blocks.yml");
        if (!placedBlocksFile.exists()) {
            placedBlocksFile.getParentFile().mkdirs();
        }
        placedBlocksConfig = YamlConfiguration.loadConfiguration(placedBlocksFile);

        // Load placed blocks data from the YAML
        loadPlacedBlocks();
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Get block location
        Location location = event.getBlock().getLocation();

        // Store the location of the placed block in the set
        String locKey = getLocationKey(location);
        placedBlocks.add(locKey);

        // Store the block location in the YAML file
        placedBlocksConfig.set("placed_blocks", new java.util.ArrayList<>(placedBlocks));
        savePlacedBlocks();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        String locKey = getLocationKey(location);


        // Check if the block was placed by a player
        if (!placedBlocks.contains(locKey)) {
            Player miner = event.getPlayer();
            int moneyGained = getRewardForBlock(event.getBlock());

            if (moneyGained == 0) {
                return;
            }
            String actionBarMessage;
            if (moneyGained < 0) {
                actionBarMessage = ChatColor.YELLOW + "" + moneyGained + " RSD";
            } else {
                actionBarMessage = ChatColor.YELLOW + "+" + moneyGained + " RSD";
            }
            miner.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarMessage));
            plugin.addBalance(miner.getUniqueId(), moneyGained);

        }
    }

    private void loadPlacedBlocks() {
        List<String> blockLocations = placedBlocksConfig.getStringList("placed_blocks");

        // Add all loaded locations to the set
        placedBlocks.addAll(blockLocations);
    }

    // Helper method to save placed blocks data to the YAML file
    private void savePlacedBlocks() {
        try {
            placedBlocksConfig.save(placedBlocksFile);
        } catch (IOException e) {
            getLogger().warning("Failed to save placed blocks data to file");
        }
    }

    public static Set<String> getPlacedBlocks() {
        return placedBlocks;
    }

    public static String getLocationKey(Location location) {
        return location.getWorld().getName() + ", " +
                location.getBlockX() + ", " +
                location.getBlockY() + ", " +
                location.getBlockZ();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        Player killer = e.getEntity().getKiller();

        // Dynamically find the reward for the entity type
        Integer moneyGained = getRewardForEntity(entity);
        if (moneyGained == null) {
            moneyGained = 1;
        }
        if (entity instanceof Creeper creeper) {
            if (creeper.isPowered()) moneyGained *= 2;
        }
        if (entity instanceof WitherSkeleton witherSkeleton) {
            if (witherSkeleton.getEquipment().getItemInMainHand().getType() == Material.NETHERITE_SWORD) {
                moneyGained += 5;
            } else if (witherSkeleton.getEquipment().getItemInMainHand().getType() == Material.DIAMOND_SWORD) {
                moneyGained += 3;
            } else if (witherSkeleton.getEquipment().getItemInMainHand().getType() == Material.IRON_SWORD) {
                moneyGained += 1;
            }
            if (witherSkeleton.getEquipment().getHelmet() != null) moneyGained += 1;
            if (witherSkeleton.getEquipment().getChestplate() != null) moneyGained += 1;
            if (witherSkeleton.getEquipment().getLeggings() != null) moneyGained += 1;
            if (witherSkeleton.getEquipment().getBoots() != null) moneyGained += 1;
        }
        if (entity instanceof Ghast ghast) {
            moneyGained += (int) Math.round(10 - 10 * ghast.getAttribute(Attribute.SCALE).getBaseValue());
        }
        if (killer != null) {
            // Send Action Bar message
            String actionBarMessage = "";
            if (moneyGained < 0) {
                actionBarMessage = ChatColor.YELLOW + "" + moneyGained + " RSD";
            } else {
                actionBarMessage = ChatColor.YELLOW + "+" + moneyGained + " RSD";
            }
            killer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarMessage));
            plugin.addBalance(killer.getUniqueId(), moneyGained);
        }
    }


    private Integer getRewardForEntity(Entity entity) {
        // Iterate through the map and check assignability
        for (Map.Entry<Class<? extends Entity>, Integer> entry : rewards.entrySet()) {
            if (entry.getKey().isAssignableFrom(entity.getClass())) {
                return entry.getValue();
            }
        }
        return null; // No reward found
    }

    private Integer getRewardForBlock(Block block) {
        Integer reward = rewardsBlock.get(block.getType());
        return reward != null ? reward : 0;
    }
}
