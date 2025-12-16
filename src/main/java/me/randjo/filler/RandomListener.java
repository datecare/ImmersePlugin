package me.randjo.filler;

import me.randjo.MainPlugin;
import me.randjo.economy.EconomyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public class RandomListener implements Listener {

    private final MainPlugin plugin;
    private final EconomyPlugin economyPlugin;
    private final Random r = new Random();

    public RandomListener(MainPlugin plugin, EconomyPlugin economyPlugin) {
        this.plugin = plugin;
        this.economyPlugin = economyPlugin;
    }


    // Easter egg
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

    // Player loses money on death
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
}
