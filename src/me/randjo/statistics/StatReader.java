package me.randjo.statistics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.randjo.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatReader {
    private static  File file;
    private static MainPlugin plugin;
    private static Map<String, List<Integer>> achievements;

    public StatReader(MainPlugin plugin) {
        this.plugin = plugin;
        setupAchFile();
    }
    public static int check(int stat, int req) {
        if (stat >= req) return 1;
        return 0;
    }

    public static void setupAchFile() {
        file = new File(plugin.getDataFolder(), "achievements.yml");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create achievements.yml file!");
                e.printStackTrace();
            }
        }
    }

    public static int[] checkPlayerStats(Player player) {
        loadAchievements();
        int[] ach = new int[135];
        // BASIC ACHIEVEMENTS
        //////////////////////////////// 1 ////////////////////////////////////
        /*
        for (int i = 0; i < 135; i++) if (hasClaimedAchievement(player.getName(), i)) ach[i] = 2;
        if (ach [0] != 2) {
            int brokenDMD = player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);
            ach[0] = check(brokenDMD, 10);
        }
        if (ach [114] != 2) {
            int usedTokens = player.getStatistic(Statistic.USE_ITEM, Material.TOTEM_OF_UNDYING);
            ach[114] = check(usedTokens, 5);
        }


         */
        return ach;
    }

    // Method to load player achievements from YAML file
    public static void loadAchievements() {
        Yaml yaml = new Yaml();
        achievements = new HashMap<>(); // Initialize to avoid null

        try (FileInputStream inputStream = new FileInputStream(file)) {
            Object data = yaml.load(inputStream);
            if (data instanceof Map) {
                //noinspection unchecked
                achievements = (Map<String, List<Integer>>) data;
            } else {
                plugin.getLogger().warning("Achievements file is empty or invalid; starting with a fresh map.");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load achievements.yml file!");
            e.printStackTrace();
        }
    }


    // Method to save player achievements to YAML file
    public static void saveAchievements(Map<String, List<Integer>> achievements) {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        Yaml yaml = new Yaml(options);

        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(achievements, writer);
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions as needed
        }
    }

    // Method to add a claimed achievement for a player
    public static void claimAchievement(String player, int achievementNumber) {
        List<Integer> playerAchievements = achievements.getOrDefault(player, new ArrayList<>());
        if (!playerAchievements.contains(achievementNumber)) {
            playerAchievements.add(achievementNumber);
        }

        achievements.put(player, playerAchievements);
        saveAchievements(achievements);
    }

    // Method to check if a player has claimed a specific achievement
    public static boolean hasClaimedAchievement(String player, int achievementNumber) {
        List<Integer> playerAchievements = achievements.get(player);
        return playerAchievements != null && playerAchievements.contains(achievementNumber);
    }
}