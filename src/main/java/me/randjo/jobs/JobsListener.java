package me.randjo.jobs;

import me.randjo.MainPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Ageable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static me.randjo.commands.MainCommandExecutor.createHelp;
import static me.randjo.economy.PlayerGainMoney.getLocationKey;
import static me.randjo.economy.PlayerGainMoney.getPlacedBlocks;
import static me.randjo.restriction.RestrictionListener.createJobMenu;
import static org.bukkit.Material.*;

public class JobsListener implements Listener {

    private final Map<Player, BossBar> playerBossBars = new HashMap<>();
    private final Map<Player, BukkitTask> bossBarTasks = new HashMap<>();

    private final JobsPlugin jobsPlugin;
    private static MainPlugin plugin;
    public final Map<EntityType, Integer> hunterXP = new EnumMap<>(EntityType.class);
    public final Map<Material, Double> minerXP = new EnumMap<>(Material.class);
    public final Map<Material, Double> cropXP = new EnumMap<>(Material.class);
    public final Map<EntityType, Double> breedXP = new EnumMap<>(EntityType.class);
    private static NamespacedKey spawnEggKey;
    private static NamespacedKey spawnerKey;


    public JobsListener(MainPlugin mainPlugin, JobsPlugin jobsPlugin) {
        this.jobsPlugin = jobsPlugin;
        this.plugin = mainPlugin;
        jobsPlugin.loadListener(this);
        spawnEggKey = new NamespacedKey(plugin, "spawn_egg_villager");
        spawnerKey = new NamespacedKey(plugin, "spawner");
    }

    public String slotToName(int slot) {
        switch(slot) {
            case 11:
                return "hunter";
            case 12:
                return "miner";
            case 14:
                return "farmer";
            case 15:
                return "adventurer";
        }
        return null;
    }

    public boolean writeNeededStat (List lore, Player player, Statistic statType, int statRequirement, String bonusInfo) {
        Integer statAmount = player.getStatistic(statType);
        if (statAmount == null) statAmount = 0;
        if (statAmount >= statRequirement) {
            lore.add(ChatColor.GREEN + statAmount.toString() + "/" + statRequirement + " " + bonusInfo);
            return true;
        }
        else {
            lore.add(ChatColor.RED + statAmount.toString() + "/" + statRequirement + " " + bonusInfo);
            return false;
        }

    }
    public boolean writeNeededStat (List lore, Player player, Statistic statType, EntityType statName, int statRequirement, String bonusInfo) {
        Integer statAmount = player.getStatistic(statType, statName);
        if (statAmount == null) statAmount = 0;
        if (statAmount >= statRequirement) {
            lore.add(ChatColor.GREEN + statAmount.toString() + "/" + statRequirement + " " + bonusInfo);
            return true;
        }
        else {
            lore.add(ChatColor.RED + statAmount.toString() + "/" + statRequirement + " " + bonusInfo);
            return false;
        }

    }

    public boolean writeNeededStat (List lore, Player player, Statistic statType, Material statName, int statRequirement, String bonusInfo) {
        return writeNeededStat(lore, player, statType, statName, statRequirement, bonusInfo, 0);
    }

    public boolean writeNeededStat (List lore, Player player, Statistic statType, Material statName, int statRequirement, String bonusInfo, Integer invalid) {
        Integer statAmount = player.getStatistic(statType, statName);
        if (statAmount == null) statAmount = 0;
        statAmount -= invalid;
        if (statAmount >= statRequirement) {
            lore.add(ChatColor.GREEN + statAmount.toString() + "/" + statRequirement + " " + bonusInfo);
            return true;
        }
        else {
            lore.add(ChatColor.RED + statAmount.toString() + "/" + statRequirement + " " + bonusInfo);
            return false;
        }
    }

    public boolean writeNeededMoney (List lore, Player player, int moneyRequirement, String bonusInfo) {
        Integer ownedMoney = MainPlugin.economyPlugin.getBalance(player.getUniqueId());
        if (ownedMoney == null) ownedMoney = 0;
        if (ownedMoney >= moneyRequirement) {
            lore.add(ChatColor.GREEN + ownedMoney.toString() + "/" + moneyRequirement + " " + bonusInfo);
            return true;
        }
        else {
            lore.add(ChatColor.RED + ownedMoney.toString() + "/" + moneyRequirement + " " + bonusInfo);
            return false;
        }
    }

    public boolean writeNeededCustom (List lore, Player player, Integer has, int requirement, String bonusInfo) {
        if (has >= requirement) {
            lore.add(ChatColor.GREEN + has.toString() + "/" + requirement + " " + bonusInfo);
            return true;
        }
        else {
            lore.add(ChatColor.RED + has.toString() + "/" + requirement + " " + bonusInfo);
            return false;
        }
    }


    public void openInfo(Player player, int slot) {
        Inventory newInventory = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "Info");
        String jobName = slotToName(slot);
        Long lvl = jobsPlugin.getJobLevel(player.getName(), jobName);
        ItemStack upgrade = new ItemStack(Material.GRAY_STAINED_GLASS);
        ItemStack unlocked = new ItemStack(Material.YELLOW_STAINED_GLASS);
        if (jobName != "adventurer") {
            if (jobsPlugin.getSelectedJobs(player.getName()).contains(jobName)) {
                if (lvl < 1) newInventory.setItem(11, upgrade);
                else newInventory.setItem(11, unlocked);
                if (lvl < 5) newInventory.setItem(12, upgrade);
                else newInventory.setItem(12, unlocked);
                if (lvl < 10) newInventory.setItem(13, upgrade);
                else newInventory.setItem(13, unlocked);
                if (lvl < 15) newInventory.setItem(14, upgrade);
                else newInventory.setItem(14, unlocked);
                if (lvl < 20) newInventory.setItem(15, upgrade);
                else newInventory.setItem(15, unlocked);
            }
            else
            {
                newInventory.setItem(11, upgrade);
                newInventory.setItem(12, upgrade);
                newInventory.setItem(13, upgrade);
                newInventory.setItem(14, upgrade);
                newInventory.setItem(15, upgrade);
            }

        }
        else {
            if (!jobsPlugin.getSelectedJobs(player.getName()).contains(jobName)) newInventory.setItem(13, upgrade);
            else newInventory.setItem(13, unlocked);
        }

        if (jobName != "adventurer") {
            ItemStack level = newInventory.getItem(11);
            ItemMeta meta = level.getItemMeta();
            List lore = new ArrayList();
            lore.add("");
            lore.add("§dBenefiti:");
            switch (slot) {
                case 11:
                    lore.add("§d10% više novca od prodaje ostataka");
                    lore.add("§d+1 put dnevno može da proda ostatke");
                    lore.add("§d5% više EXP od ubijanja mobova");
                    break;
                case 12:
                    lore.add("§d10% više novca od prodaje ruda");
                    lore.add("§d+1 put dnevno može da proda rude");
                    lore.add("§d10% više EXP od kopanja i topljenja ruda");
                    break;
                case 14:
                    lore.add("§d10% više novca od prodaje plodova");
                    lore.add("§d+1 put dnevno može da proda plodove");
                    lore.add("§dDobija EXP kada prodaje plodove u shopu");
                    lore.add("§dAutomatsko postavljanje useva nakon branja");
                    break;
            }
            meta.setLore(lore);

            if (level.getType() == Material.GRAY_STAINED_GLASS) {
                meta.setDisplayName(ChatColor.GRAY + "Level 1");
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Level 1");
            }
            level.setItemMeta(meta);

            // Level 2
            level = newInventory.getItem(12);
            meta = level.getItemMeta();
            lore = new ArrayList();
            lore.add("");
            lore.add("§dBenefiti:");
            switch (slot) {
                case 11:
                    lore.add("§d20% više novca od prodaje ostataka");
                    lore.add("§d+2 puta dnevno može da proda ostatke");
                    lore.add("§d10% više EXP od ubijanja mobova");
                    if (lvl == 4 && jobsPlugin.getJobXP(player.getName(), "hunter") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.ZOMBIE, 200, "ubijenih zombija");
                        boolean check2 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.SKELETON, 200, "ubijenih skeletona");
                        boolean check3 = writeNeededMoney(lore, player, 1000, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 12:
                    lore.add("§d20% više novca od prodaje ruda");
                    lore.add("§d+2 puta dnevno može da proda rude");
                    lore.add("§d20% više EXP od kopanja i topljenja ruda");
                    if (lvl == 4 && jobsPlugin.getJobXP(player.getName(), "miner") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.COAL_ORE, 200, "iskopanog Coal Ore", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("coal_ore", 0));
                        boolean check2 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.IRON_ORE, 200, "iskopanog Iron Ore", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("iron_ore", 0));
                        boolean check3 = writeNeededMoney(lore, player, 1000, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 14:
                    lore.add("§d20% više novca od prodaje plodova");
                    lore.add("§d+2 puta dnevno može da proda plodove");
                    lore.add("§d20% veći EXP kada prodaje plodove u shopu od levela 1");
                    lore.add("§dAutomatsko postavljanje useva nakon branja");
                    if (lvl == 4 && jobsPlugin.getJobXP(player.getName(), "farmer") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        long wheatSales = plugin.restrictionPlugin.getTotalSales(player.getName(), "WHEAT");
                        long carrotSales = plugin.restrictionPlugin.getTotalSales(player.getName(), "CARROT");
                        long potatoSales = plugin.restrictionPlugin.getTotalSales(player.getName(), "POTATO");
                        long beetrootSales = plugin.restrictionPlugin.getTotalSales(player.getName(), "BEETROOT");
                        long total = wheatSales + carrotSales + potatoSales + beetrootSales;
                        boolean check1 = writeNeededCustom(lore, player, Math.toIntExact(total), 1280, "prodate pšenice, šargarepe, krompira i cvekle");
                        boolean check2 = writeNeededMoney(lore, player, 1000, "RSD");
                        if (check1 && check2)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
            }
            meta.setLore(lore);
            if (level.getType() == Material.GRAY_STAINED_GLASS) {
                meta.setDisplayName(ChatColor.GRAY + "Level 5");
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Level 5");
            }
            level.setItemMeta(meta);

            // Level 3
            level = newInventory.getItem(13);
            meta = level.getItemMeta();
            lore = new ArrayList();
            lore.add("");
            lore.add("§dBenefiti:");
            switch (slot) {
                case 11:
                    lore.add("§d30% više novca od prodaje ostataka");
                    lore.add("§d+3 puta dnevno može da proda ostatke");
                    lore.add("§d15% više EXP od ubijanja mobova");
                    if (lvl == 9 && jobsPlugin.getJobXP(player.getName(), "hunter") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.SPIDER, 500, "ubijenih paukova");
                        boolean check2 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.CREEPER, 250, "ubijenih creepera");
                        boolean check3 = writeNeededMoney(lore, player, 2100, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 12:
                    lore.add("§d30% više novca od prodaje ruda");
                    lore.add("§d+3 puta dnevno može da proda rude");
                    lore.add("§d30% više EXP od kopanja i topljenja ruda");
                    if (lvl == 9 && jobsPlugin.getJobXP(player.getName(), "miner") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.GOLD_ORE, 500, "iskopanog Gold Ore", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("gold_ore", 0));
                        boolean check2 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.NETHER_QUARTZ_ORE, 750, "iskopanog Quartz Ore", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("nether_quartz_ore", 0));
                        boolean check3 = writeNeededMoney(lore, player, 2100, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 14:
                    lore.add("§d30% više novca od prodaje plodova");
                    lore.add("§d+3 puta dnevno može da proda plodove");
                    lore.add("§d30% veći EXP kada prodaje plodove u shopu od levela 1");
                    lore.add("§dAutomatsko postavljanje useva nakon branja");
                    if (lvl == 9 && jobsPlugin.getJobXP(player.getName(), "farmer") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.ANIMALS_BRED, 250, "životinja razmnoženih");
                        boolean check2 = writeNeededStat(lore, player, Statistic.CRAFT_ITEM, Material.GOLDEN_CARROT, 256, "napravljenih zlatnih šargarepa");
                        boolean check3 = writeNeededMoney(lore, player, 2100, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
            }
            meta.setLore(lore);
            if (level.getType() == Material.GRAY_STAINED_GLASS) {
                meta.setDisplayName(ChatColor.GRAY + "Level 10");
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Level 10");
            }
            level.setItemMeta(meta);

            // Level 4
            level = newInventory.getItem(14);
            meta = level.getItemMeta();
            lore = new ArrayList();
            lore.add("");
            lore.add("§dBenefiti:");
            switch (slot) {
                case 11:
                    lore.add("§d40% više novca od prodaje ostataka");
                    lore.add("§d+4 puta dnevno može da proda ostatke");
                    lore.add("§d20% više EXP od ubijanja mobova");
                    if (lvl == 14 && jobsPlugin.getJobXP(player.getName(), "hunter") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.VINDICATOR, 100, "ubijenih vindikatora");
                        boolean check2 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.ELDER_GUARDIAN, 5, "ubijenih elder guardiana");
                        boolean check3 = writeNeededMoney(lore, player, 4200, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 12:
                    lore.add("§d40% više novca od prodaje ruda");
                    lore.add("§d+4 puta dnevno može da proda rude");
                    lore.add("§d40% više EXP od kopanja i topljenja ruda");
                    if (lvl == 14 && jobsPlugin.getJobXP(player.getName(), "miner") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.BUDDING_AMETHYST, 100, "iskopanog Budding Amethyst", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("budding_amethyst", 0));
                        boolean check2 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.LAPIS_ORE, 500, "iskopanog Lapis Ore", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("lapis_ore", 0));
                        boolean check3 = writeNeededMoney(lore, player, 4200, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 14:
                    lore.add("§d40% više novca od prodaje plodova");
                    lore.add("§d+4 puta dnevno može da proda plodove");
                    lore.add("§d40% veći EXP kada prodaje plodove u shopu od levela 1");
                    lore.add("§dAutomatsko postavljanje useva nakon branja");
                    if (lvl == 14 && jobsPlugin.getJobXP(player.getName(), "farmer") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        long pumpSales = plugin.restrictionPlugin.getTotalSales(player.getName(), "PUMPKIN_SEEDS");
                        long melonSales = plugin.restrictionPlugin.getTotalSales(player.getName(), "MELON_SEEDS");
                        long cocoaSales = plugin.restrictionPlugin.getTotalSales(player.getName(), "COCOA_BEANS");
                        long total = pumpSales + melonSales + cocoaSales;
                        boolean check1 = writeNeededCustom(lore, player, Math.toIntExact(total), 1280, "prodatih semena bundeve, lubenice i kakao zrna");
                        boolean check2 = writeNeededStat(lore, player, Statistic.FISH_CAUGHT, 256, "upecanih riba");
                        boolean check3 = writeNeededMoney(lore, player, 4200, "RSD");
                        if (check1 && check2 && check3) lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
            }
            meta.setLore(lore);
            if (level.getType() == Material.GRAY_STAINED_GLASS) {
                meta.setDisplayName(ChatColor.GRAY + "Level 15");
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Level 15");
            }
            level.setItemMeta(meta);

            // Level 5
            level = newInventory.getItem(15);
            meta = level.getItemMeta();
            lore = new ArrayList();
            lore.add("");
            lore.add("§dBenefiti:");
            switch (slot) {
                case 11:
                    lore.add("§d50% više novca od prodaje ostataka");
                    lore.add("§d+5 puta dnevno može da proda ostatke");
                    lore.add("§d25% više EXP od ubijanja mobova");
                    lore.add("§4??????????????????????????");
                    if (lvl == 19 && jobsPlugin.getJobXP(player.getName(), "hunter") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.WITHER, 5, "ubijenih withera");
                        boolean check2 = writeNeededStat(lore, player, Statistic.KILL_ENTITY, EntityType.ENDER_DRAGON, 1, "ubijenih zmajeva");
                        boolean check3 = writeNeededMoney(lore, player, 6900, "RSD");
                        if (check1 && check2 && check3)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 12:
                    lore.add("§d50% više novca od prodaje ruda");
                    lore.add("§d+5 puta dnevno može da proda rude");
                    lore.add("§d50% više EXP od kopanja i topljenja ruda");
                    lore.add("§3??????????????????????????");
                    if (lvl == 19 && jobsPlugin.getJobXP(player.getName(), "miner") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.EMERALD_ORE, 100, "iskopanog Emerald Ore", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("emerald_ore", 0));
                        boolean check2 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.DEEPSLATE_COAL_ORE, 100, "iskopanog Deepslate Coal Ore", jobsPlugin.getSilkData().getOrDefault(player.getName(), new HashMap<String, Integer>()).getOrDefault("deepslate_coal_ore", 0));
                        boolean check3 = writeNeededStat(lore, player, Statistic.MINE_BLOCK, Material.STONE, 50000, "iskopanog kamena");
                        boolean check4 = writeNeededMoney(lore, player, 6900, "RSD");
                        if (check1 && check2 && check3 && check4)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
                case 14:
                    lore.add("§d50% više novca od prodaje plodova");
                    lore.add("§d+5 puta dnevno može da proda plodove");
                    lore.add("§d50% veći EXP kada prodaje plodove u shopu od levela 1");
                    lore.add("§dAutomatsko postavljanje useva nakon branja");
                    lore.add("§2??????????????????????????");
                    if (lvl == 19 && jobsPlugin.getJobXP(player.getName(), "farmer") == jobsPlugin.getXpNeeded()[Math.toIntExact(lvl)-1]) {
                        lore.add("");
                        boolean check1 = writeNeededStat(lore, player, Statistic.CRAFT_ITEM, Material.CAKE, 50, "napravljenih torti");
                        boolean check2 = writeNeededStat(lore, player, Statistic.CRAFT_ITEM, Material.GOLDEN_APPLE, 64, "napravljenih zlatnih jabuka");
                        boolean check3 = writeNeededStat(lore, player, Statistic.USE_ITEM, Material.BONE_MEAL, 1000, "iskorišćenog bone meal-a");
                        boolean check4 = writeNeededStat(lore, player, Statistic.BREAK_ITEM, Material.SHEARS, 5, "polomi pet makaza");
                        boolean check5 = writeNeededMoney(lore, player, 6900, "RSD");
                        if (check1 && check2 && check3 && check4 && check5)  lore.add(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš");
                    }
                    break;
            }
            meta.setLore(lore);
            if (level.getType() == Material.GRAY_STAINED_GLASS) {
                meta.setDisplayName(ChatColor.GRAY + "Level 20");
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Level 20");
            }
            level.setItemMeta(meta);
        }
        else {
            ItemStack level = newInventory.getItem(13);
            ItemMeta meta = level.getItemMeta();
            List lore = new ArrayList();
            lore.add("");
            lore.add("§dBenefiti:");
            lore.add("§dOtključava tokene potrebne za put u end");
            meta.setLore(lore);
            if (level.getType() == Material.GRAY_STAINED_GLASS) {
                meta.setDisplayName(ChatColor.GRAY + "Level 1");
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Level 1");
            }
            level.setItemMeta(meta);
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
        back.setItemMeta(meta);
        newInventory.setItem(22, back);
        player.openInventory(newInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        if (event.getView().getTitle().equals(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "JOBS")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                int slot = event.getSlot();
                if (clickedItem == null) return;
                if (clickedItem.getType() == Material.BARRIER) {
                    createHelp(player);
                }
                    if (clickedItem.getType() == DIAMOND_SWORD || clickedItem.getType() == GOLDEN_PICKAXE || clickedItem.getType() == NETHERITE_HOE)
                    {
                        if (event.isRightClick() && event.isShiftClick()) {
                            if (jobsPlugin.getNumberOfSelectedJobs(player.getName()) < JobsPlugin.getPlayerMaxJobs(player)) {
                                if (!JobsPlugin.getSelectedJobs(player.getName()).contains(slotToName(slot))) {
                                    jobsPlugin.addSelectedJob(player.getName(), slotToName(slot));
                                    ItemMeta meta = clickedItem.getItemMeta();
                                    meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                    clickedItem.setItemMeta(meta);
                                    player.sendMessage("§aIzabrali ste posao " + "§e" + slotToName(slot));
                                }
                            }
                        }
                        else if (event.isLeftClick()) {
                            openInfo(player, slot);
                        }
                    }
                /*
                else if (event.isRightClick())
                {
                    if (slot == 10 || slot == 12 || slot == 14 || slot == 16) {
                        if (jobsPlugin.getSelectedJobs(player.getName()).contains(slotToName(slot))) {
                            jobsPlugin.removeSelectedJob(player.getName(), slotToName(slot));
                            ItemMeta meta = clickedItem.getItemMeta();
                            meta.removeEnchant(Enchantment.SHARPNESS);
                            clickedItem.setItemMeta(meta);
                            player.sendMessage("§cNapustili ste posao " + "§e" + slotToName(slot));
                        }
                    }
                }
                 */
            }
        }
        else if (event.getView().getTitle().equals(ChatColor.YELLOW + "Info")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null) {
                    if (clickedItem.getType() == Material.GRAY_STAINED_GLASS) {
                        ItemMeta meta = clickedItem.getItemMeta();
                        List lore = meta.getLore();
                        if (lore.contains(ChatColor.YELLOW + "[Levi klik] " + ChatColor.GRAY + "da otključaš")) {
                            ItemStack differential = topInventory.getItem(15);
                            ItemMeta metaDiff = differential.getItemMeta();
                            List loreDiff = metaDiff.getLore();
                            String job = "miner";
                            if (loreDiff.contains("§4??????????????????????????")) job = "hunter";
                            if (loreDiff.contains("§2??????????????????????????")) job = "farmer";
                            int slot = event.getSlot();
                            Integer neededMoney = 6900;
                            if (slot == 14) neededMoney = 4200;
                            if (slot == 13) neededMoney = 2100;
                            if (slot == 12) neededMoney = 1000;
                            if (plugin.economyPlugin.getBalance(player.getUniqueId()) > neededMoney) {
                                plugin.economyPlugin.subtractBalance(player.getUniqueId(), neededMoney);
                                JobsPlugin.levelUP(player.getName(), job, jobsPlugin.getJobLevel(player.getName(), job), 0);
                                if (job == "hunter") openInfo(player, 11);
                                if (job == "miner") openInfo(player, 12);
                                if (job == "farmer") openInfo(player, 14);
                                int level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), job));
                                String title = ChatColor.GREEN + "Level up!";
                                int fadeIn = 20; // in ticks
                                int stay = 60;   // in ticks
                                int fadeOut = 20; // in ticks
                                player.sendTitle(title,"", fadeIn, stay, fadeOut);
                            }
                        }
                    }
                    if (clickedItem.getType() == Material.BARRIER) createJobMenu(player);
                }
            }
        }
    }

    ///////////////////////////////////////// SPECIFIC JOB THINGS ///////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final int[] levelUpMoney = {20, 45, 100, 0, 150, 200, 250, 300, 0, 350, 400, 450, 500, 0, 600, 750, 900, 1000};
    //////////////////////////////////////////////// HUNTER /////////////////////////////////////////////////////////////



    private float getRewardForEntity(Entity entity) {
        return hunterXP.getOrDefault(entity.getType(), 0);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            boolean isHunter = jobsPlugin.getSelectedJobs(killer.getName()).contains("hunter");
            if (isHunter) {
                event.setDroppedExp(Math.round(event.getDroppedExp()* JobsPlugin.getBonusMultiplier(killer, "hunter")));
                double expGained = getRewardForEntity(entity);
                if (expGained > 0) {

                    int level = Math.toIntExact(jobsPlugin.getJobLevel(killer.getName(), "hunter"));
                    double currentXP = jobsPlugin.getJobXP(killer.getName(), "hunter");
                    int neededXP = jobsPlugin.getXpNeeded()[level - 1];

                    currentXP += expGained;
                    if (currentXP > neededXP) {
                        if (level == 4 || level == 9 || level == 14 || level == 19) {
                            currentXP = neededXP;
                            jobsPlugin.setJobXP(killer.getName(), "hunter", currentXP);
                        }
                        else {
                            currentXP -= neededXP;
                            jobsPlugin.levelUP(killer.getName(), "hunter", level, currentXP);
                            level = Math.toIntExact(jobsPlugin.getJobLevel(killer.getName(), "hunter"));
                            String title = ChatColor.GREEN + "Level up!";
                            String subtitle = "Dobili ste " + ChatColor.YELLOW + levelUpMoney[level-2] + " RSD";
                            killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            int fadeIn = 20; // in ticks
                            int stay = 60;   // in ticks
                            int fadeOut = 20; // in ticks
                            killer.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                            MainPlugin.economyPlugin.addBalance(killer.getUniqueId(), levelUpMoney[level-2]);
                        }
                    }
                    else {
                        jobsPlugin.setJobXP(killer.getName(), "hunter", currentXP);
                    }

                    double progress = currentXP / neededXP;

                    BossBar bossBar = playerBossBars.computeIfAbsent(killer, k -> Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID));
                    bossBar.setTitle("Lvl " + level + " Hunter: " + (int) currentXP + "/" + neededXP + " XP");
                    bossBar.setProgress(progress);
                    bossBar.addPlayer(killer);

                    if (bossBarTasks.containsKey(killer)) {
                        bossBarTasks.get(killer).cancel();
                    }

                    BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        bossBar.removePlayer(killer);
                        playerBossBars.remove(killer);
                        bossBarTasks.remove(killer);
                    }, 100L);

                    bossBarTasks.put(killer, task);
                }
            }
        }
    }

    public static ItemStack createSpawner(String type) {
        // Create a new spawner item
        ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawnerItem.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(spawnerKey, PersistentDataType.STRING, type);
            spawnerItem.setItemMeta(meta);
        }
        return spawnerItem;
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() == Material.SPAWNER) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (container.has(spawnerKey, PersistentDataType.STRING)) {
                    String spawnerType = item.getItemMeta().getPersistentDataContainer().get(spawnerKey, PersistentDataType.STRING);
                    Block block = event.getBlockPlaced();
                    if (block.getType() == Material.SPAWNER) {
                        BlockState state = block.getState();
                        if (state instanceof CreatureSpawner spawner) {
                            if ("minecraft:zombie".equals(spawnerType)) {
                                spawner.setSpawnedType(EntityType.ZOMBIE);
                                PersistentDataContainer blockContainer = spawner.getPersistentDataContainer();
                                blockContainer.set(spawnerKey, PersistentDataType.STRING, "minecraft:zombie");
                            }
                            spawner.update();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        CreatureSpawner spawner = event.getSpawner();
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        if (!container.has(spawnerKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }


    //////////////////////////////////////////////// MINER /////////////////////////////////////////////////////////////

    @EventHandler
    public void onOreBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        boolean isMiner = jobsPlugin.getSelectedJobs(player.getName()).contains("miner");
        if (isMiner) {
            event.setExpToDrop(Math.round(event.getExpToDrop() * 2 * JobsPlugin.getBonusMultiplier(player, "miner")));
            Double expGained = getXPforBlock(block);
            Location location = event.getBlock().getLocation();
            String locKey = getLocationKey(location);
            if (expGained != 0 && !getPlacedBlocks().contains(locKey)) {
                int level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "miner"));
                Double currentXP = jobsPlugin.getJobXP(player.getName(), "miner");
                double neededXP = jobsPlugin.getXpNeeded()[level - 1];

                currentXP += expGained;
                if (currentXP > neededXP) {
                    if (level == 4 || level == 9 || level == 14 || level == 19) {
                        currentXP = neededXP;
                        jobsPlugin.setJobXP(player.getName(), "miner", currentXP);
                    }
                    else {
                        currentXP -= neededXP;
                        jobsPlugin.levelUP(player.getName(), "miner", level, currentXP);
                        level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "miner"));
                        String title = ChatColor.GREEN + "Level up!";
                        String subtitle = "Dobili ste " + ChatColor.YELLOW + + levelUpMoney[level-2] + " RSD";
                        int fadeIn = 20; // in ticks
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        int stay = 60;   // in ticks
                        int fadeOut = 20; // in ticks
                        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                        MainPlugin.economyPlugin.addBalance(player.getUniqueId(), levelUpMoney[level-2]);
                    }
                }
                else {
                    jobsPlugin.setJobXP(player.getName(), "miner", currentXP);
                }
                double progress = currentXP / neededXP;

                BossBar bossBar = playerBossBars.computeIfAbsent(player, k -> Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID));
                bossBar.setTitle("Lvl " + level + " Miner: " +   String.format("%.2f", currentXP) + "/" + neededXP + " XP");
                bossBar.setProgress(progress);
                bossBar.addPlayer(player);

                if (bossBarTasks.containsKey(player)) {
                    bossBarTasks.get(player).cancel();
                }

                BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    bossBar.removePlayer(player);
                    playerBossBars.remove(player);
                    bossBarTasks.remove(player);
                }, 100L);

                bossBarTasks.put(player, task);
            }
        }
    }

    private Double getXPforBlock(Block block) {
        Double xp = minerXP.get(block.getType());
        return xp != null ? xp : 0;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH)) {
            Player player = event.getPlayer();
            Block block = event.getBlock();
            Material blockType = block.getType();

            if (isTrackedOre(blockType)) {
                String oreName = blockType.toString().toLowerCase();
                Map<String,Map<String, Integer>> silkData = jobsPlugin.getSilkData();
                Map<String, Integer> ores = silkData.get(player.getName());
                if (ores == null) ores = new HashMap<>();
                ores.put(oreName, ores.getOrDefault(oreName, 0) + 1);
                silkData.put(player.getName(), ores);
                jobsPlugin.saveSilk();
                plugin.getLogger().info("Ore broken with Silk Touch: " + oreName);
            }
        }
    }

    private boolean isTrackedOre(Material material) {
        // List of ores to track
        switch (material) {
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case NETHER_QUARTZ_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case DEEPSLATE_COAL_ORE:
            case COAL_ORE:
                return true;
            default:
                return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (JobsPlugin.getJobLevel(player.getName(), "miner") == 20) {
            applyHasteEffect(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (JobsPlugin.getJobLevel(player.getName(), "miner") == 20) {
            Bukkit.broadcastMessage("Did it!");
            Bukkit.getScheduler().runTask(plugin, () -> applyHasteEffect(player));        }
    }

    public void applyHasteEffect(Player player) {
        PotionEffect hasteEffect = new PotionEffect(PotionEffectType.HASTE, Integer.MAX_VALUE, 0, true, false);
        player.addPotionEffect(hasteEffect);
    }


    /////////////////////////////////////////////// FARMER /////////////////////////////////////////////////////////////

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        boolean isFarmer = jobsPlugin.getSelectedJobs(player.getName()).contains("farmer");

        Material seed = seedType(block.getType());

        if (isFarmer) {
            try {
                Ageable ageable = (Ageable) block.getBlockData();
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    if (seed != null) {
                        event.setCancelled(true);

                        List<ItemStack> drops = (List<ItemStack>) block.getDrops(event.getPlayer().getInventory().getItemInMainHand());

                        for (ItemStack drop : drops) {
                            if (drop.getType() == seed && drop.getAmount() > 0) {
                                drop.setAmount(drop.getAmount() - 1);
                                break;
                            }
                        }
                        for (ItemStack drop : drops) {
                            block.getWorld().dropItemNaturally(block.getLocation(), drop);
                        }
                        ageable.setAge(0);
                        block.setBlockData(ageable);
                    }
                    Double expGained = getXPforCrop(block);
                    if (expGained != 0) {
                        int level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "farmer"));
                        Double currentXP = jobsPlugin.getJobXP(player.getName(), "farmer");
                        double neededXP = jobsPlugin.getXpNeeded()[level - 1];

                        currentXP += expGained;
                        if (currentXP > neededXP) {
                            if (level == 4 || level == 9 || level == 14 || level == 19) {
                                currentXP = neededXP;
                                jobsPlugin.setJobXP(player.getName(), "farmer", currentXP);
                            }
                            else {
                                currentXP -= neededXP;
                                jobsPlugin.levelUP(player.getName(), "farmer", level, currentXP);
                                level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "farmer"));
                                String title = ChatColor.GREEN + "Level up!";
                                String subtitle = "Dobili ste " + ChatColor.YELLOW + + levelUpMoney[level-2] + " RSD";
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                                int fadeIn = 20; // in ticks
                                int stay = 60;   // in ticks
                                int fadeOut = 20; // in ticks
                                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                                MainPlugin.economyPlugin.addBalance(player.getUniqueId(), levelUpMoney[level-2]);
                            }
                        }
                        else {
                            jobsPlugin.setJobXP(player.getName(), "farmer", currentXP);
                        }
                        double progress = currentXP / neededXP;

                        BossBar bossBar = playerBossBars.computeIfAbsent(player, k -> Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID));
                        bossBar.setTitle("Lvl " + level + " Farmer: " +   String.format("%.2f", currentXP) + "/" + neededXP + " XP");
                        bossBar.setProgress(progress);
                        bossBar.addPlayer(player);

                        if (bossBarTasks.containsKey(player)) {
                            bossBarTasks.get(player).cancel();
                        }

                        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            bossBar.removePlayer(player);
                            playerBossBars.remove(player);
                            bossBarTasks.remove(player);
                        }, 100L);

                        bossBarTasks.put(player, task);
                    }
                }
            }
            catch (Exception e) {
                Double expGained = getXPforCrop(block);
                Location location = event.getBlock().getLocation();
                String locKey = getLocationKey(location);
                if (expGained != 0 && !getPlacedBlocks().contains(locKey)) {
                    int level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "farmer"));
                    Double currentXP = jobsPlugin.getJobXP(player.getName(), "farmer");
                    double neededXP = jobsPlugin.getXpNeeded()[level - 1];

                    currentXP += expGained;
                    if (currentXP > neededXP) {
                        if (level == 4 || level == 9 || level == 14 || level == 19) {
                            currentXP = neededXP;
                            jobsPlugin.setJobXP(player.getName(), "farmer", currentXP);
                        } else {
                            currentXP -= neededXP;
                            jobsPlugin.levelUP(player.getName(), "farmer", level, currentXP);
                            level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "farmer"));
                            String title = ChatColor.GREEN + "Level up!";
                            String subtitle = "Dobili ste " + ChatColor.YELLOW + +levelUpMoney[level - 2] + " RSD";
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            int fadeIn = 20; // in ticks
                            int stay = 60;   // in ticks
                            int fadeOut = 20; // in ticks
                            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                            MainPlugin.economyPlugin.addBalance(player.getUniqueId(), levelUpMoney[level - 2]);
                        }
                    } else {
                        jobsPlugin.setJobXP(player.getName(), "farmer", currentXP);
                    }
                    double progress = currentXP / neededXP;

                    BossBar bossBar = playerBossBars.computeIfAbsent(player, k -> Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID));
                    bossBar.setTitle("Lvl " + level + " Farmer: " + String.format("%.2f", currentXP) + "/" + neededXP + " XP");
                    bossBar.setProgress(progress);
                    bossBar.addPlayer(player);

                    if (bossBarTasks.containsKey(player)) {
                        bossBarTasks.get(player).cancel();
                    }

                    BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        bossBar.removePlayer(player);
                        playerBossBars.remove(player);
                        bossBarTasks.remove(player);
                    }, 100L);

                    bossBarTasks.put(player, task);

                }
            }
        }
    }

    private Double getXPforCrop(Block block) {
        Double xp = cropXP.get(block.getType());
        return xp != null ? xp : 0;
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        Entity entity = event.getEntity();
        if (event.getBreeder() instanceof Player player) {
            boolean isFarmer = jobsPlugin.getSelectedJobs(player.getName()).contains("farmer");
            if (isFarmer) {
                Double expGained = getRewardForBreeding(entity);
                if (expGained != 0) {
                    int level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "farmer"));
                    Double currentXP = jobsPlugin.getJobXP(player.getName(), "farmer");
                    double neededXP = jobsPlugin.getXpNeeded()[level - 1];

                    currentXP += expGained;
                    if (currentXP > neededXP) {
                        if (level == 4 || level == 9 || level == 14 || level == 19) {
                            currentXP = neededXP;
                            jobsPlugin.setJobXP(player.getName(), "farmer", currentXP);
                        }
                        else {
                            currentXP -= neededXP;
                            jobsPlugin.levelUP(player.getName(), "farmer", level, currentXP);
                            level = Math.toIntExact(jobsPlugin.getJobLevel(player.getName(), "farmer"));
                            String title = ChatColor.GREEN + "Level up!";
                            String subtitle = "Dobili ste " + ChatColor.YELLOW + + levelUpMoney[level-2] + " RSD";
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                            int fadeIn = 20; // in ticks
                            int stay = 60;   // in ticks
                            int fadeOut = 20; // in ticks
                            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                            MainPlugin.economyPlugin.addBalance(player.getUniqueId(), levelUpMoney[level-2]);
                        }
                    }
                    else {
                        jobsPlugin.setJobXP(player.getName(), "farmer", currentXP);
                    }
                    double progress = currentXP / neededXP;

                    BossBar bossBar = playerBossBars.computeIfAbsent(player, k -> Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID));
                    bossBar.setTitle("Lvl " + level + " Farmer: " +   String.format("%.2f", currentXP) + "/" + neededXP + " XP");
                    bossBar.setProgress(progress);
                    bossBar.addPlayer(player);

                    if (bossBarTasks.containsKey(player)) {
                        bossBarTasks.get(player).cancel();
                    }

                    BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        bossBar.removePlayer(player);
                        playerBossBars.remove(player);
                        bossBarTasks.remove(player);
                    }, 100L);

                    bossBarTasks.put(player, task);
                }
            }
        }

    }

    public Material seedType(Material crop) {
        if (crop == Material.WHEAT) return Material.WHEAT_SEEDS;
        if (crop == Material.CARROTS) return Material.CARROT;
        if (crop == Material.POTATOES) return Material.POTATO;
        if (crop == Material.BEETROOTS) return Material.BEETROOT_SEEDS;
        if (crop == Material.COCOA) return Material.COCOA_BEANS;
        return null;
    }

    private double getRewardForBreeding(Entity entity) {
        return breedXP.getOrDefault(entity.getType(), 0.0);
    }


    // Tag villagers spawned from spawn eggs
    @EventHandler
    public void onVillagerSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            Villager villager = (Villager) event.getEntity();
            PersistentDataContainer data = villager.getPersistentDataContainer();
            data.set(spawnEggKey, PersistentDataType.BYTE, (byte) 1); // Tag the villager
        }
    }

    //TODO
    /*
// Block interactions with non-spawn egg villagers
        @EventHandler
    public void onPlayerInteractWithVillager(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager villager) {
            PersistentDataContainer data = villager.getPersistentDataContainer();
            if (!data.has(spawnEggKey, PersistentDataType.BYTE)) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.sendMessage("§cOvaj villager je bogalj. Ne možeš interaktovati sa njim.");
            }
        }
    }

     */


    @EventHandler
    public void onVillagerBreed(EntityBreedEvent event) {
        // Ensure the breeding entities are villagers
        if (event.getEntityType() == EntityType.VILLAGER
                && event.getMother() instanceof Villager mother
                && event.getFather() instanceof Villager father) {

            // Check if both parents have the spawn egg metadata
            PersistentDataContainer motherData = mother.getPersistentDataContainer();
            PersistentDataContainer fatherData = father.getPersistentDataContainer();
            boolean motherHasTag = motherData.has(spawnEggKey, PersistentDataType.BYTE);
            boolean fatherHasTag = fatherData.has(spawnEggKey, PersistentDataType.BYTE);

            // Tag the offspring based on parent metadata
            Villager offspring = (Villager) event.getEntity();
            PersistentDataContainer offspringData = offspring.getPersistentDataContainer();

            if (motherHasTag && fatherHasTag) {
                // Both parents have the tag -> good villager
                offspringData.set(spawnEggKey, PersistentDataType.BYTE, (byte) 1);
                Bukkit.broadcastMessage("A good villager has been bred!");
            }
        }
    }

    //////////////////////////////////////////// JOB SHOP /////////////////////////////////////////////////////////////////

    public static void createJobShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLACK + "Special Shop");
        ItemStack spawnerShop = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawnerShop.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Spawner Shop");
        spawnerShop.setItemMeta(meta);
        ItemStack villager = new ItemStack(Material.VILLAGER_SPAWN_EGG);
        meta = villager.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Kupite roba - 5000 RSD i 3 Achievement Poena");
        villager.setItemMeta(meta);
        if (JobsPlugin.getJobLevel(player.getName(), "hunter") == 20) inventory.setItem(3, spawnerShop);
        if (JobsPlugin.getJobLevel(player.getName(), "farmer") == 20) inventory.setItem(5, villager);
        player.openInventory(inventory);
    }

    public static void createSpawnerShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.BLACK + "Spawner Shop");
        ItemStack spawner1 = createSpawner("minecraft:zombie");
        ItemStack spawner2 = createSpawner("minecraft:skeleton");
        ItemStack spawner3 = createSpawner("minecraft:spider");
        ItemStack spawner4 = createSpawner("minecraft:creeper");
        ItemStack spawner5 = createSpawner("minecraft:slime");
        ItemStack spawner6 = createSpawner("minecraft:witch");
    }

    @EventHandler
    public void onSpecialShopClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (event.getView().getTitle().equals(ChatColor.BLACK + "Special Shop")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null) {
                    if (clickedItem.getType() == Material.SPAWNER) {
                        createSpawnerShop(player);
                    }
                }
            }
        }
    }
}
