package me.randjo.restriction;

import me.randjo.MainPlugin;
import me.randjo.jobs.JobsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RestrictionPlugin {
    private final int numberOfSales = 3;
    private MainPlugin plugin;
    private HashSet<String> playerNames = new HashSet<>();
    private File enchFile;
    private YamlConfiguration enchConfig;

    private Map<String, Map<String, Map<String, List<Long>>>> playerSaleData = new HashMap<>();
    private File saleDataFile;
    private YamlConfiguration saleDataConfig;


    public RestrictionPlugin(MainPlugin plugin) {
        this.plugin = plugin;
        // Set up the files
        setupEnchFile();
        setupSaleDataFile();

        // Load player names and sales data
        loadEnch();
        loadSaleData();

    }

    private void setupEnchFile() {
        enchFile = new File(plugin.getDataFolder(), "ench.yml");
        if (!enchFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                enchFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create ench.yml file!");
                e.printStackTrace();
            }
        }
        enchConfig = YamlConfiguration.loadConfiguration(enchFile);
    }

    private void setupSaleDataFile() {
        saleDataFile = new File(plugin.getDataFolder(), "sales_data.yml");
        if (!saleDataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                saleDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create sales_data.yml file!");
                e.printStackTrace();
            }
        }
        saleDataConfig = YamlConfiguration.loadConfiguration(saleDataFile);
    }

    public void loadEnch() {
        // Clear current playerNames before loading new data
        playerNames.clear();

        // Check if file exists and contains data
        if (enchFile.exists()) {
            for (String player : enchConfig.getKeys(false)) {
                playerNames.add(player);
            }
            plugin.getLogger().info("Loaded player names from ench.yml.");
        }
    }

    public void saveEnch() {
        // Clear previous data and re-add player names from HashSet to YML
        for (String playerName : playerNames) {
            enchConfig.set(playerName, true);  // Set player name with a value (can be customized)
        }

        try {
            enchConfig.save(enchFile);
            plugin.getLogger().info("Saved player names to ench.yml.");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save ench.yml!");
            e.printStackTrace();
        }
    }

    public void loadSaleData() {
        playerSaleData.clear();  // Clear existing data

        if (saleDataFile.exists()) {
            for (String playerName : saleDataConfig.getKeys(false)) {
                // Create a map for each player's items and their sales data
                Map<String, Map<String, List<Long>>> items = new HashMap<>();

                for (String itemName : saleDataConfig.getConfigurationSection(playerName).getKeys(false)) {
                    // Create a map for basic and bonus sales
                    Map<String, List<Long>> saleTypes = new HashMap<>();
                    List<Long> basicSales = new ArrayList<>();
                    List<Long> bonusSales = new ArrayList<>();

                    // Load total sold amount
                    long totalSold = saleDataConfig.getLong(playerName + "." + itemName + ".totalSold", 0);

                    // Load basic sales if available
                    if (saleDataConfig.contains(playerName + "." + itemName + ".basicSales")) {
                        for (String saleKey : saleDataConfig.getConfigurationSection(playerName + "." + itemName + ".basicSales").getKeys(false)) {
                            long saleTime = saleDataConfig.getLong(playerName + "." + itemName + ".basicSales." + saleKey);
                            basicSales.add(saleTime);
                        }
                        saleTypes.put("basicSales", basicSales);
                    }

                    // Load bonus sales if available
                    if (saleDataConfig.contains(playerName + "." + itemName + ".bonusSales")) {
                        for (String saleKey : saleDataConfig.getConfigurationSection(playerName + "." + itemName + ".bonusSales").getKeys(false)) {
                            long saleTime = saleDataConfig.getLong(playerName + "." + itemName + ".bonusSales." + saleKey);
                            bonusSales.add(saleTime);
                        }
                        saleTypes.put("bonusSales", bonusSales);
                    }

                    // Store total sold amount and sales data
                    saleTypes.put("totalSold", Collections.singletonList(totalSold));
                    items.put(itemName, saleTypes);
                }

                playerSaleData.put(playerName, items);
            }

            plugin.getLogger().info("Loaded player sales data from sales_data.yml.");
        }
    }

    public void addSaleTime(Player player, String itemName, String jobName) {
        String playerName = player.getName();
        int bonusSales = JobsPlugin.getBonusNumberOfSales(player, jobName);

        // Get the current sale data for the player
        Map<String, Map<String, List<Long>>> playerItems = playerSaleData.get(playerName);
        if (playerItems == null) {
            playerItems = new HashMap<>();
            playerSaleData.put(playerName, playerItems);
        }

        // Get the sale times for the specified item
        Map<String, List<Long>> itemSales = playerItems.get(itemName);
        if (itemSales == null) {
            itemSales = new HashMap<>();
            playerItems.put(itemName, itemSales);
        }

        // Get the basic sales and bonus sales lists for the item
        List<Long> basicSales = itemSales.getOrDefault("basicSales", new ArrayList<>());
        List<Long> bonusSalesList = itemSales.getOrDefault("bonusSales", new ArrayList<>());

        // Get the current total sold amount
        long totalSold = itemSales.getOrDefault("totalSold", Collections.singletonList(0L)).get(0);

        // Get the current time in seconds
        long currentTime = System.currentTimeMillis() / 1000L;

        // Determine sale type (basic or bonus) based on available slots
        if (basicSales.size() < 3) {
            basicSales.add(currentTime);
            itemSales.put("basicSales", basicSales);
        } else if (bonusSalesList.size() < bonusSales) {
            bonusSalesList.add(currentTime);
            itemSales.put("bonusSales", bonusSalesList);
        } else {
            plugin.getLogger().info("No available sales slots for " + playerName + " selling " + itemName);
            return;
        }

        // Update the total sold amount
        totalSold += 64; // Increment by the stack size
        itemSales.put("totalSold", Collections.singletonList(totalSold));

        // Save the updated sales data to the file
        saveSaleData();
        plugin.getLogger().info("Added sale time for " + playerName + " selling " + itemName + " at " + currentTime);
    }

    public void saveSaleData() {
        // Save player sale data to the YML file
        for (Map.Entry<String, Map<String, Map<String, List<Long>>>> playerEntry : playerSaleData.entrySet()) {
            String playerName = playerEntry.getKey();
            Map<String, Map<String, List<Long>>> items = playerEntry.getValue();

            // Iterate over each item for the player
            for (Map.Entry<String, Map<String, List<Long>>> itemEntry : items.entrySet()) {
                String itemName = itemEntry.getKey();
                Map<String, List<Long>> saleTypes = itemEntry.getValue();

                // Split sales data into basic, bonus sales, and total sold
                List<Long> basicSales = saleTypes.getOrDefault("basicSales", new ArrayList<>());
                List<Long> bonusSales = saleTypes.getOrDefault("bonusSales", new ArrayList<>());
                long totalSold = saleTypes.getOrDefault("totalSold", Collections.singletonList(0L)).get(0);

                // Save basic sales
                for (int i = 0; i < basicSales.size(); i++) {
                    saleDataConfig.set(playerName + "." + itemName + ".basicSales.sale" + (i + 1), basicSales.get(i));
                }

                // Save bonus sales
                for (int i = 0; i < bonusSales.size(); i++) {
                    saleDataConfig.set(playerName + "." + itemName + ".bonusSales.sale" + (i + 1), bonusSales.get(i));
                }

                // Save total sold
                saleDataConfig.set(playerName + "." + itemName + ".totalSold", totalSold);
            }
        }

        try {
            saleDataConfig.save(saleDataFile);
            plugin.getLogger().info("Saved player sales data to sales_data.yml.");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save sales_data.yml!");
            e.printStackTrace();
        }
    }



    // You can add other methods to manipulate player names in the HashSet, e.g., add/remove player names
    public void addPlayerName(String playerName) {
        playerNames.add(playerName);
        saveEnch();  // Optionally save right after adding
    }

    public void removePlayerName(String playerName) {
        playerNames.remove(playerName);
        saveEnch();  // Optionally save right after removing
    }

    public Set<String> getPlayerNames() {
        return playerNames;
    }

    public int getRemainingSales(Player player, String itemName, String jobMenu) {
        String playerName = player.getName();

        // Get the number of bonus sales slots based on the player's job
        int bonusSales = JobsPlugin.getBonusNumberOfSales(player, jobMenu);
        int totalAllowedSales = 3 + bonusSales; // Basic 3 slots + bonus slots

        // Get the current sale data for the player and item
        Map<String, Map<String, List<Long>>> playerItems = playerSaleData.get(playerName);
        if (playerItems == null) {
            return totalAllowedSales; // No sales yet, so all slots are available
        }

        Map<String, List<Long>> itemSales = playerItems.get(itemName);
        if (itemSales == null) {
            return totalAllowedSales; // No sales for this item yet
        }

        List<Long> basicSales = itemSales.getOrDefault("basicSales", new ArrayList<>());
        List<Long> bonusSalesList = itemSales.getOrDefault("bonusSales", new ArrayList<>());

        // Get the current time in seconds
        long currentTime = System.currentTimeMillis() / 1000L;
        long dayInSeconds = 86400L; // 24 hours * 60 minutes * 60 seconds

        // Remove outdated sales
        basicSales.removeIf(saleTime -> currentTime - saleTime > dayInSeconds);
        bonusSalesList.removeIf(saleTime -> currentTime - saleTime > dayInSeconds);

        saveSaleData();

        // If the player has no job, return remaining basic slots
        if (bonusSales == 0) {
            return Math.max(0, 3 - basicSales.size());
        }

        // If the player has a job, return remaining total slots
        return Math.max(0, totalAllowedSales - (basicSales.size() + bonusSalesList.size()));
    }

    public Map<String, Map<String, Map<String, List<Long>>>> getPlayerSaleData() {
        return playerSaleData;
    }

    public long getTotalSales(String playerName, String itemName) {
        // Check if the player exists in the data
        if (!playerSaleData.containsKey(playerName)) {
            return 0; // Player doesn't exist
        }

        // Get the items sold by the player
        Map<String, Map<String, List<Long>>> items = playerSaleData.get(playerName);

        // Check if the specific item exists in the player's data
        if (!items.containsKey(itemName)) {
            return 0; // Item doesn't exist for this player
        }

        // Get the sales data for the item
        Map<String, List<Long>> itemSales = items.get(itemName);

        // Fetch the total sold value, defaulting to 0 if not present
        return itemSales.getOrDefault("totalSold", Collections.singletonList(0L)).get(0);
    }

}
