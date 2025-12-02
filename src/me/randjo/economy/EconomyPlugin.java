package me.randjo.economy;

import me.randjo.MainPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyPlugin {
    private final MainPlugin mainPlugin;

    private final HashMap<UUID, Integer> playerBalances = new HashMap<>();
    private File balanceFile;
    private YamlConfiguration balanceConfig;

    public EconomyPlugin(MainPlugin mainPlugin) {
        this.mainPlugin = mainPlugin;
        setupBalanceFile();
        loadBalances();
    }

    private void setupBalanceFile() {
        balanceFile = new File(mainPlugin.getDataFolder(), "balances.yml");
        if (!balanceFile.exists()) {
            try {
                mainPlugin.getDataFolder().mkdirs();
                balanceFile.createNewFile();
            } catch (IOException e) {
                mainPlugin.getLogger().severe("Could not create balances.yml file!");
                e.printStackTrace();
            }
        }
        balanceConfig = YamlConfiguration.loadConfiguration(balanceFile);
    }

    public int getBalance(UUID playerUUID) {
        return playerBalances.getOrDefault(playerUUID, -1);
    }

    public void setBalance(UUID playerUUID, int amount) {
        playerBalances.put(playerUUID, amount);
        saveBalances();
    }

    public void addBalance(UUID playerUUID, int amount) {
        setBalance(playerUUID, Math.max(0, getBalance(playerUUID) + amount));
        saveBalances();
    }

    public void subtractBalance(UUID playerUUID, int amount) {
        setBalance(playerUUID, Math.max(0, getBalance(playerUUID) - amount));
        saveBalances();
    }

    public void saveBalances() {
        for (UUID uuid : playerBalances.keySet()) {
            balanceConfig.set(uuid.toString(), playerBalances.get(uuid));
        }

        try {
            balanceConfig.save(balanceFile);
        } catch (IOException e) {
            mainPlugin.getLogger().severe("Could not save balances to balances.yml!");
            e.printStackTrace();
        }
    }

    public void loadBalances() {
        if (!balanceFile.exists()) return;

        for (String key : balanceConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int balance = balanceConfig.getInt(key);
                playerBalances.put(uuid, balance);
            } catch (IllegalArgumentException e) {
                mainPlugin.getLogger().warning("Invalid UUID found in balances.yml: " + key);
            }
        }
    }

    public Map<UUID, Integer> getPlayerBalances() {
        return new HashMap<>(playerBalances);
    }


}
