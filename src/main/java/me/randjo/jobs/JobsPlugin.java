package me.randjo.jobs;

import me.randjo.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JobsPlugin {
    private static final int[] XpPerLevel = {10, 20, 50, 100,  200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 2000, 1000000};

    private static MainPlugin plugin;
    private static Map<String, Map<String, List<Object>>> playerJobData = new HashMap<>();
    private static Map<String, List<String>> selectedJobs = new HashMap<>();
    private static Map<String, Map<String, Integer>> silkData = new HashMap<>();
    private static File jobDataFile;
    private static FileConfiguration jobDataConfig;
    private static File selectedJobsFile;
    private static FileConfiguration selectedJobsConfig;
    private static File silkFile;
    private static FileConfiguration silkDataConfig;
    private JobsListener jobsListener;
    private File jobFile;
    private FileConfiguration jobConfig;

    public void loadListener(JobsListener listener) {
        jobsListener = listener;
        setupConfig();
        loadRewards();
        plugin.saveResource("jobSettings.yml", false);
    }

    private void setupConfig() {
        jobFile = new File(plugin.getDataFolder(), "jobSettings.yml");
        if (!jobFile.exists()) {
            plugin.saveResource("jobSettings.yml", false);
        }
        jobConfig = YamlConfiguration.loadConfiguration(jobFile);
    }

    private void loadRewards() {
        jobsListener.hunterXP.clear();
        if (jobConfig.contains("hunter-rewards")) {
            for (String key : jobConfig.getConfigurationSection("hunter-rewards").getKeys(false)) {
                try {
                    // Convert String to EntityType (e.g., "ZOMBIE" -> EntityType.ZOMBIE)
                    EntityType type = EntityType.valueOf(key.toUpperCase());
                    int xp = jobConfig.getInt("hunter-rewards." + key);

                    jobsListener.hunterXP.put(type, xp);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Skipping '" + key + "': Not a valid Entity (hunter)!");
                }
            }
        }
        plugin.getLogger().info("Successfully loaded " + jobsListener.hunterXP.size() + " mob rewards. (hunter)");

        jobsListener.minerXP.clear();
        if (jobConfig.contains("miner-rewards")) {
            for (String key : jobConfig.getConfigurationSection("miner-rewards").getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    double xp = jobConfig.getDouble("miner-rewards." + key);
                    jobsListener.minerXP.put(mat, xp);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Skipping '" + key + "': Not a valid Minecraft Material! (miner)");
                }
            }
        }
        plugin.getLogger().info("Successfully loaded " + jobsListener.minerXP.size() + " miner block rewards.");

        jobsListener.cropXP.clear();
        if (jobConfig.contains("crop-rewards")) {
            for (String key : jobConfig.getConfigurationSection("crop-rewards").getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    double xp = jobConfig.getDouble("crop-rewards." + key);
                    jobsListener.cropXP.put(mat, xp);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Skipping '" + key + "': Not a valid Minecraft Material! (crop)");
                }
            }
        }
        plugin.getLogger().info("Successfully loaded " + jobsListener.cropXP.size() + " farmer crop rewards.");

        jobsListener.breedXP.clear();
        if (jobConfig.contains("breed-rewards")) {
            for (String key : jobConfig.getConfigurationSection("breed-rewards").getKeys(false)) {
                try {
                    EntityType type = EntityType.valueOf(key.toUpperCase());
                    double xp = jobConfig.getDouble("breed-rewards." + key);
                    jobsListener.breedXP.put(type, xp);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Skipping '" + key + "': Not a valid Entity (breed)");
                }
            }
        }
        plugin.getLogger().info("Successfully loaded " + jobsListener.breedXP.size() + " farmer breed rewards.");
    }

    public JobsPlugin(MainPlugin plugin) {
        this.plugin = plugin;

        // Ensure the plugin's data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Initialize YAML files for job data and selected jobs
        jobDataFile = new File(plugin.getDataFolder(), "jobData.yml");
        selectedJobsFile = new File(plugin.getDataFolder(), "selectedJobs.yml");
        silkFile = new File(plugin.getDataFolder(), "silkData.yml");

        // Create the job data YAML file if it doesn't exist
        if (!jobDataFile.exists()) {
            try {
                jobDataFile.createNewFile();
                plugin.getLogger().info("jobData.yml created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create the selected jobs YAML file if it doesn't exist
        if (!selectedJobsFile.exists()) {
            try {
                selectedJobsFile.createNewFile();
                plugin.getLogger().info("selectedJobs.yml created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!silkFile.exists()) {
            try {
                silkFile.createNewFile();
                plugin.getLogger().info("silkFile.yml created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load the configurations
        jobDataConfig = YamlConfiguration.loadConfiguration(jobDataFile);
        selectedJobsConfig = YamlConfiguration.loadConfiguration(selectedJobsFile);
        silkDataConfig = YamlConfiguration.loadConfiguration(silkFile);

        // Load player job data and selected jobs
        loadFromYaml();
        loadSelectedJobs();
        loadSilk();

        plugin.getLogger().info("JobsPlugin has been enabled!");
    }

    public static void setJobData(String player, String job, long level, double xp) {
        playerJobData.putIfAbsent(player, new HashMap<>());
        Map<String, List<Object>> jobs = playerJobData.get(player);
        jobs.put(job, Arrays.asList(level, xp));

        saveToYaml();
    }

    public static Long getJobLevel(String player, String job) {
        if (!playerJobData.containsKey(player) || !playerJobData.get(player).containsKey(job)) {
            setJobData(player, job, 1L, 0.0); // Default level 1 and XP 0.0
        }
        return (Long) playerJobData.get(player).get(job).get(0);
    }

    public static Double getJobXP(String player, String job) {
        if (!playerJobData.containsKey(player) || !playerJobData.get(player).containsKey(job)) {
            setJobData(player, job, 1L, 0.0); // Default level 1 and XP 0.0
        }
        return (Double) playerJobData.get(player).get(job).get(1);
    }

    public static void setJobLevel(String player, String job, long level) {
        playerJobData.putIfAbsent(player, new HashMap<>());
        Map<String, List<Object>> jobs = playerJobData.get(player);

        // If the job doesn't exist for the player, initialize it with level and 0.0 XP
        if (!jobs.containsKey(job)) {
            jobs.put(job, Arrays.asList(level, 0.0));
        } else {
            jobs.get(job).set(0, level);
        }

        saveToYaml();
    }


    public static void levelUP(String player, String job, long level, double bonus_xp) {
        setJobData(player, job, level + 1,  bonus_xp);
    }

    public static void setJobXP(String player, String job, double xp) {
        playerJobData.putIfAbsent(player, new HashMap<>());
        Map<String, List<Object>> jobs = playerJobData.get(player);

        // If the job doesn't exist for the player, initialize it with level 1 and given XP
        if (!jobs.containsKey(job)) {
            jobs.put(job, Arrays.asList(1L, xp));
        } else {
            jobs.get(job).set(1, xp);
        }

        saveToYaml();
    }

    public static void saveToYaml() {
        for (String player : playerJobData.keySet()) {
            jobDataConfig.set(player, null);
        }

        for (Map.Entry<String, Map<String, List<Object>>> entry : playerJobData.entrySet()) {
            String player = entry.getKey();
            Map<String, List<Object>> jobs = entry.getValue();
            for (Map.Entry<String, List<Object>> jobEntry : jobs.entrySet()) {
                String job = jobEntry.getKey();
                List<Object> data = jobEntry.getValue();
                jobDataConfig.set(player + "." + job + ".level", data.get(0));
                jobDataConfig.set(player + "." + job + ".xp", data.get(1));
            }
        }

        try {
            jobDataConfig.save(jobDataFile);
            plugin.getLogger().info("Job data saved to jobData.yml.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromYaml() {
        for (String player : jobDataConfig.getKeys(false)) {
            Map<String, List<Object>> jobs = new HashMap<>();
            for (String job : jobDataConfig.getConfigurationSection(player).getKeys(false)) {
                long level = jobDataConfig.getLong(player + "." + job + ".level");
                double xp = jobDataConfig.getDouble(player + "." + job + ".xp");
                jobs.put(job, Arrays.asList(level, xp));
            }
            playerJobData.put(player, jobs);
        }

        plugin.getLogger().info("Job data loaded from jobData.yml.");
    }

    private void loadSilk() {
        for (String player : silkDataConfig.getKeys(false)) {
            Map<String, Integer> silkOres = new HashMap<>();
            for (String ore : silkDataConfig.getConfigurationSection(player).getKeys(false)) {
                int oreValue = silkDataConfig.getInt(player + "." + ore);
                silkOres.put(ore, oreValue);
            }
            silkData.put(player, silkOres);
        }
    }

    public void saveSilk() {
        plugin.getLogger().info("Save silk method called!");
        for (String player : silkData.keySet()) {
            silkDataConfig.set(player, null);
        }

        for (Map.Entry<String, Map<String, Integer>> entry : silkData.entrySet()) {
            plugin.getLogger().info("Player exists");
            String player = entry.getKey();
            Map<String, Integer> oreMap = entry.getValue();
            for (Map.Entry<String, Integer> ore : oreMap.entrySet()) {
                String oreName = ore.getKey();
                Integer numBroken = ore.getValue();
                silkDataConfig.set(player + "." + oreName, numBroken);
            }
        }

        try {
            silkDataConfig.save(silkFile);
            plugin.getLogger().info("Silk data saved to silkData.yml.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save selected jobs to selectedJobs.yml
    public static void saveSelectedJobs() {
        for (String player : selectedJobs.keySet()) {
            selectedJobsConfig.set(player, null);
        }

        for (Map.Entry<String, List<String>> entry : selectedJobs.entrySet()) {
            String player = entry.getKey();
            List<String> jobs = entry.getValue();
            selectedJobsConfig.set(player, jobs);
        }

        try {
            selectedJobsConfig.save(selectedJobsFile);
            plugin.getLogger().info("Selected jobs saved to selectedJobs.yml.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load selected jobs from selectedJobs.yml
    public static void loadSelectedJobs() {
        for (String player : selectedJobsConfig.getKeys(false)) {
            List<String> jobs = selectedJobsConfig.getStringList(player);
            selectedJobs.put(player, jobs);
        }

        plugin.getLogger().info("Selected jobs loaded from selectedJobs.yml.");
    }

    // Get the selected jobs for a player
    public static List<String> getSelectedJobs(String player) {
        return selectedJobs.getOrDefault(player, new ArrayList<>());
    }

    public static Map<String, Map<String, Integer>> getSilkData() {
        return silkData;
    }

    // Add a selected job for a player
    public static void addSelectedJob(String player, String job) {
        selectedJobs.putIfAbsent(player, new ArrayList<>());
        selectedJobs.get(player).add(job);

        saveSelectedJobs();
    }

    // Remove a selected job for a player
    public static void removeSelectedJob(String player, String job) {
        if (selectedJobs.containsKey(player)) {
            selectedJobs.get(player).remove(job);
            Bukkit.broadcastMessage("Job is in the select file");

            saveSelectedJobs();
        }
        if (playerJobData.get(player).containsKey(job))
        {
            Bukkit.broadcastMessage("Job is in the level file");
            playerJobData.get(player).remove(job);
            saveToYaml();
        }
    }

    public static int getNumberOfSelectedJobs(String player) {
        List<String> jobs = selectedJobs.getOrDefault(player, new ArrayList<>());
        return jobs.size();
    }

    public static int[] getXpNeeded() {
        return XpPerLevel;
    }

    public static float getBonusMultiplier(Player player, String job) {
        Long level = getJobLevel(player.getName(), job);
        if (level == 20) return 1.5f;
        if (level >= 15) return 1.4f;
        if (level >= 10) return 1.3f;
        if (level >= 5) return 1.2f;
        return 1.1f;
    }

    public static int getBonusNumberOfSales(Player player, String job) {
        if (!JobsPlugin.getSelectedJobs(player.getName()).contains(job)) return 0;
        Long level = getJobLevel(player.getName(), job);
        if (level == 20) return 5;
        if (level >= 15) return 4;
        if (level >= 10) return 3;
        if (level >= 5) return 2;
        return 1;
    }

    public static int getPlayerMaxJobs(Player player) {
        int maxJob = 1;
        if (JobsPlugin.getJobLevel(player.getName(), "hunter") >= 15) maxJob += 1;
        if (JobsPlugin.getJobLevel(player.getName(), "miner") >= 15) maxJob += 1;
        if (JobsPlugin.getJobLevel(player.getName(), "farmer") >= 15) maxJob += 1;
        return maxJob;
    }
}
