package me.randjo.commands;

import me.randjo.economy.EconomyPlugin;
import me.randjo.restriction.RestrictionPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.randjo.restriction.RestrictionListener.createShop;

public class RestrictionCommandExecutor implements CommandExecutor {

    private RestrictionPlugin plugin;
    private EconomyPlugin economyPlugin;
    public RestrictionCommandExecutor (RestrictionPlugin plugin, EconomyPlugin economyPlugin) {
        this.plugin = plugin;
        this.economyPlugin = economyPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "enchantingtable":
                return handleTABLE(sender, args);
            case "shop":
                return handleSHOP(sender, args);
            default:
                return false;
        }
    }

    private boolean handleSHOP(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            createShop(player);
            return true;
        }
        return false;
    }

    private boolean handleTABLE(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (!plugin.getPlayerNames().contains(player.getName())) {
                if (economyPlugin.getBalance(player.getUniqueId()) >= 5000 && player.getLevel() >= 50) {
                    plugin.getPlayerNames().add(player.getName());
                    economyPlugin.subtractBalance(player.getUniqueId(), 5000);
                    player.setLevel(player.getLevel() - 50);
                    player.sendMessage(ChatColor.GREEN + "Čestitam, otključali ste enchantovanje.");
                    player.getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE, 1));
                }
                else {
                    player.sendMessage(ChatColor.WHITE + "Da bi otključao enchanting table potrebno ti je " + ChatColor.YELLOW + "5000 RSD " + ChatColor.YELLOW + "i " + ChatColor.GREEN + "50 EXP.");
                }
            }
            else {
                if (economyPlugin.getBalance(player.getUniqueId()) >= 1000) {
                    economyPlugin.subtractBalance(player.getUniqueId(), 1000);
                    player.sendMessage(ChatColor.GREEN + "Kupili ste 1 enchanting table");
                    player.getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE, 1));
                } else {
                    player.sendMessage(ChatColor.RED + "Nemaš dovoljno novca, potrebno je" + ChatColor.YELLOW + " 1000 RSD ");

                }
            }
            return true;
        }
        return false;
    }

}
