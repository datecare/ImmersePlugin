package me.randjo.commands;

import me.randjo.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EconomyCommandExecutor implements CommandExecutor {

    private final EconomyPlugin plugin;
    public EconomyCommandExecutor(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        switch (command.getName().toLowerCase()) {
            case "balance":
                return handleBALANCE(sender, args);
            case "pay":
                return handlePAY(sender, args);
            case "givemoney":
                return handleGIVE(sender,args);
            case "baltop":
                return handleBALTOP(sender, args);
            default:
                return false;
        }
    }

    private boolean handleGIVE(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 2) {
                player.sendMessage("Nepravilan format. Format komande je: /givemoney <igrač> <količina>");
                return true;
            }
            UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[0]).getBytes(StandardCharsets.UTF_8));
            if (plugin.getBalance(offlineUUID) == -1.0) {
                player.sendMessage("Igrač kome ste pokušali poslati novac ne postoji.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[1]);
                plugin.addBalance(offlineUUID, amount);
                player.sendMessage("Poslali ste igraču " + args[0] + " " + amount + " RSD.");
                Player otherPlayer = Bukkit.getPlayer(offlineUUID);
                if (otherPlayer != null) {
                    otherPlayer.sendMessage("Server vam je dao " + amount + " RSD.");
                }
                return true;
            } catch (NumberFormatException e) {
                try {
                    double v = Double.parseDouble(args[1]);
                    player.sendMessage("Pogle današnju inflaciju, zar misliš da deo dinara vredi kurcu...");
                    return true;
                }
                catch (NumberFormatException e2) {
                    player.sendMessage("Niste uneli važeću količinu novca.");
                    return true;
                }
            }
        }
        return false;
    }


    private boolean handleBALANCE(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            player.sendMessage(ChatColor.GREEN + "Tvoj balans je: " + ChatColor.YELLOW + plugin.getBalance(player.getUniqueId()) + ChatColor.GOLD + " RSD");
            return true;
        }
        return false;
    }

    private boolean handlePAY(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 2) {
                player.sendMessage("Nepravilan format. Format komande je: /pay <igrač> <količina>");
                return true;
            }
            UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[0]).getBytes(StandardCharsets.UTF_8));
            if (plugin.getBalance(offlineUUID) == -1.0) {
                player.sendMessage("Igrač kome ste pokušali poslati novac ne postoji.");
                return true;
            }
            if (offlineUUID.toString().equals(player.getUniqueId().toString())) {
                player.sendMessage("Ne možete poslati novac sami sebi.");
                return true;
            }
            try {
                int amount = Integer.parseInt(args[1]);
                if (amount > plugin.getBalance(player.getUniqueId())) {
                    player.sendMessage("Nemate dovoljno novca.");
                    return true;
                }
                if (amount < 0) {
                    player.sendMessage("Niste uneli važeću količinu novca.");
                    return true;
                }
                plugin.subtractBalance(player.getUniqueId(), amount);
                plugin.addBalance(offlineUUID, amount);
                player.sendMessage("Poslali ste igraču " + args[0] + " " + amount + " RSD.");
                Player otherPlayer = Bukkit.getPlayer(offlineUUID);
                if (otherPlayer != null) {
                    otherPlayer.sendMessage("Igrač " + player.getName() + " vam je poslao " + amount + " RSD.");
                }
                return true;
            } catch (NumberFormatException e) {
                try {
                    double v = Double.parseDouble(args[1]);
                    player.sendMessage("Pogle današnju inflaciju, zar misliš da deo dinara vredi kurcu...");
                    return true;
                }
                catch (NumberFormatException e2) {
                    player.sendMessage("Niste uneli važeću količinu novca.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleBALTOP(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            // Sort the balances in descending order
            List<Map.Entry<UUID, Integer>> sortedBalances = plugin.getPlayerBalances()
                    .entrySet()
                    .stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .toList();

            // Limit to top 10 players
            int limit = Math.min(sortedBalances.size(), 10);

            player.sendMessage(ChatColor.GOLD + "========[ Top 10 ]========");

            for (int i = 0; i < limit; i++) {
                Map.Entry<UUID, Integer> entry = sortedBalances.get(i);
                String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                player.sendMessage(ChatColor.YELLOW + Integer.toString((i + 1)) + ". " + ChatColor.GREEN + (playerName != null ? playerName : "Unknown Player") + ChatColor.WHITE + " - " + ChatColor.GOLD +  entry.getValue().toString() + " RSD");
            }

            return true;
        }
        return false;
    }


}

