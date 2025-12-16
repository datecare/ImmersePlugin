package me.randjo;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.randjo.commands.*;
import me.randjo.economy.EconomyPlugin;
import me.randjo.economy.PlayerGainMoney;
import me.randjo.filler.RandomListener;
import me.randjo.filler.crazymobs.*;
import me.randjo.jobs.JobsListener;
import me.randjo.jobs.JobsPlugin;
import me.randjo.restriction.RestrictionListener;
import me.randjo.restriction.RestrictionPlugin;
import me.randjo.statistics.Achievements;
import me.randjo.statistics.StatReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPlugin extends JavaPlugin implements Listener {
    static public EconomyPlugin economyPlugin;
    static public RestrictionPlugin restrictionPlugin;
    static public JobsPlugin jobsPlugin;
    static public StatReader statReader;
    static public Essentials essentials;
    static public ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        Bukkit.broadcastMessage("ImmersePlugin reloaded! " + Math.random());
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        economyPlugin = new EconomyPlugin(this);
        restrictionPlugin = new RestrictionPlugin(this);
        jobsPlugin = new JobsPlugin(this);
        statReader = new StatReader(this);

        Plugin pl = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if (pl == null || !pl.isEnabled()) {
            getLogger().severe("ProtocolLib not found or failed to enable! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();
        if (protocolManager == null) {
            getLogger().severe("ProtocolManager is null! ProtocolLib failed to initialize properly.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        economyPlugin.saveBalances();
        restrictionPlugin.saveEnch();
        restrictionPlugin.saveSaleData();
        jobsPlugin.saveToYaml();
        jobsPlugin.saveSilk();
        jobsPlugin.saveSelectedJobs();
    }

    public void registerCommands() {
                     // DEFAULT COMMANDS //
        MainCommandExecutor mainExecutor = new MainCommandExecutor(economyPlugin);
        getCommand("msg").setExecutor(mainExecutor);
        getCommand("help").setExecutor(mainExecutor);
        getCommand("spawn").setExecutor(mainExecutor);
        getCommand("changelog").setExecutor(mainExecutor);
        getCommand("addhome").setExecutor(mainExecutor);
        // ECONOMY COMMANDS //
        EconomyCommandExecutor economyExecutor = new EconomyCommandExecutor(economyPlugin);
        getCommand("balance").setExecutor(economyExecutor);
        getCommand("pay").setExecutor(economyExecutor);
        getCommand("givemoney").setExecutor(economyExecutor);
        getCommand("baltop").setExecutor(economyExecutor);
                     // RESTRICTION COMMANDS //
        RestrictionCommandExecutor restrictionExecutor = new RestrictionCommandExecutor(restrictionPlugin, economyPlugin);
        getCommand("enchantingtable").setExecutor(restrictionExecutor);
        getCommand("shop").setExecutor(restrictionExecutor);
                    // ACHIEVEMENT COMMANDS //
        AchievementCommandExecutor achievementExecutor = new AchievementCommandExecutor();
        getCommand("achievements").setExecutor(achievementExecutor);
                    // JOB COMMANDS //
        JobCommandExecutor jobExecutor = new JobCommandExecutor(jobsPlugin);
        getCommand("jobs").setExecutor(jobExecutor);
        getCommand("setjob").setExecutor(jobExecutor);
        getCommand("addjob").setExecutor(jobExecutor);
        getCommand("removejob").setExecutor(jobExecutor);
        getCommand("jobshop").setExecutor(jobExecutor);
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerGainMoney(economyPlugin), this);
        getServer().getPluginManager().registerEvents(new RestrictionListener(restrictionPlugin, economyPlugin, jobsPlugin), this);
        getServer().getPluginManager().registerEvents(new RandomListener(this, economyPlugin), this);
        getServer().getPluginManager().registerEvents(new JobsListener(this, jobsPlugin), this);
        getServer().getPluginManager().registerEvents(new Achievements(), this);

        // Register Listeners for Special Mob Events
        getServer().getPluginManager().registerEvents(new CreeperListener(this), this);
        getServer().getPluginManager().registerEvents(new GhastListener(this), this);
        getServer().getPluginManager().registerEvents(new PigListener(this), this);
        getServer().getPluginManager().registerEvents(new WitherSkeletonListener(this), this);
        getServer().getPluginManager().registerEvents(new ZombieListener(this), this);
        getServer().getPluginManager().registerEvents(new SlimeListener(this), this);

        SkeletonListener skeletonListener = new SkeletonListener(this);
        getServer().getPluginManager().registerEvents(skeletonListener, this);
        skeletonListener.setupPacket(protocolManager);
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPlayedBefore()) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "Dobro došli " + ChatColor.GOLD + p.getName() + ChatColor.GREEN + "!");
        }
        if (economyPlugin.getBalance(event.getPlayer().getUniqueId()) == -1) {
            economyPlugin.setBalance(event.getPlayer().getUniqueId(), 100); // Default starting balance
        }
    }

    public static User getUser(Player player) {
        return essentials == null ? null : essentials.getUser(player);
    }

    public static boolean addHomeSlot(Player player, int additionalHomes) {
        User user = MainPlugin.getUser(player);
        if (user == null) return false;

        int currentLimit = user.getHomes().size();
        int newLimit = currentLimit + additionalHomes;

        // Use permissions or internal settings to adjust home limits
        player.addAttachment(Bukkit.getPluginManager().getPlugin("ImmersePlugin"), "essentials.sethome.multiple." + newLimit, true);

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ImmersePlugin"), () -> {
            user.reloadConfig();
            player.recalculatePermissions(); // Ensure Bukkit permissions are also updated
        }, 1L);

        player.sendMessage("Your home limit has been increased to " + newLimit + " homes!");
        return true;
    }
}
