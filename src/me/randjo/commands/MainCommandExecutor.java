package me.randjo.commands;

import me.randjo.MainPlugin;
import me.randjo.economy.EconomyPlugin;
import me.randjo.statistics.StatReader;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MainCommandExecutor implements CommandExecutor {
    private final EconomyPlugin economyPlugin;
    public MainCommandExecutor(EconomyPlugin plugin) {
        economyPlugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "msg":
                return handleMSG(sender, args);
            case "help":
                return handleHELP(sender, args);
            case "spawn":
                return handleSPAWN(sender, args);
            case "changelog":
                return handleCHANGELOG(sender, args);
            case "addhome":
                return handleADDHOME(sender, args);
            default:
                return false;
        }
    }

    private boolean handleADDHOME(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            Bukkit.broadcastMessage("Added home?");
            MainPlugin.addHomeSlot(player, 1);
            return true;
        }
        return false;
    }

    private boolean handleCHANGELOG(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            /*
            player.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Changelog:");
            player.sendMessage(ChatColor.UNDERLINE + "28.12.2024.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "Enchanting temporarily disabled");
            player.sendMessage(ChatColor.GREEN + "Added custom pig event");
            player.sendMessage(ChatColor.GREEN + "Added a grass economy event");
            player.sendMessage(ChatColor.GREEN + "Players now lose money on death");
            player.sendMessage(ChatColor.GREEN + "Added a chance for charged creepers to spawn");
           */
            player.sendMessage(ChatColor.UNDERLINE + "29.12.2024.");
            player.sendMessage(ChatColor.GREEN + "Enchantment system reworked and back in game");
            player.sendMessage(ChatColor.GREEN + "Chances of diamond drops modified");
            player.sendMessage(ChatColor.RED + "Trading with librarians is temporarily disabled");
            player.sendMessage(ChatColor.UNDERLINE + "30.12.2024.");
            player.sendMessage(ChatColor.GREEN + "/help command added alongside Mladen Štrbac");
            player.sendMessage(ChatColor.UNDERLINE + "31.12.2024.");
            player.sendMessage(ChatColor.GREEN + "Implemented weekly shop");
            player.sendMessage(ChatColor.UNDERLINE + "1.01.2025.");
            player.sendMessage(ChatColor.GREEN + "Ghasts and Wither Skeletons have been made stronger");
            // player.sendMessage(ChatColor.UNDERLINE + "3.01.2025.");
            // player.sendMessage(ChatColor.GREEN + "Implemented weekly shop");
            return true;
        }
        return false;
    }

    private boolean handleSPAWN(CommandSender sender, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = Bukkit.getWorld("world");
            if (world != null) {
                Location spawnLocation = world.getSpawnLocation();

                double spawnX = spawnLocation.getX();
                double spawnZ = spawnLocation.getZ();

                double offsetX = 0.5;
                double offsetZ = 0.5;
                spawnLocation.setX(spawnX + offsetX);
                spawnLocation.setZ(spawnZ + offsetZ);

                int y = spawnLocation.getBlockY();
                while (y > 0 && world.getBlockAt(spawnLocation.getBlockX(), y, spawnLocation.getBlockZ()).getType().isAir()) {
                    y--;
                }
                spawnLocation.setY(y + 1);

                player.teleport(spawnLocation);

                return true;
            } else {
                player.sendMessage("Svet ne postoji");
            }
        } else {
            sender.sendMessage("Samo igrači mogu poslati ovu komandu");
        }
        return false;
    }



    private boolean handleMSG(CommandSender sender, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage("§cNepravilan format! Iskoristi: /msg <igrač> <poruka>");
                return true;
            }

            Player target = player.getServer().getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                String message = String.join(" ", args).substring(args[0].length()).trim();
                target.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + player.getName() + ChatColor.GOLD + " -> " + ChatColor.RED + "ja" + ChatColor.GOLD + "] §f" + message);
                player.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "ja" + ChatColor.GOLD + " -> " + ChatColor.RED + target.getName() + ChatColor.GOLD + "] §f" + message);
            } else {
                player.sendMessage("§cIgrač nije pronađen na serveru.");
                return false;
            }
        } else {
            sender.sendMessage("Samo igrači mogu da koriste ovu komandu!");
            return false;
        }
        return true;
    }

    public static void createHelp(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "HELP MENU");

        ItemStack filler1 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler1.getItemMeta();
        meta.setDisplayName(" ");
        filler1.setItemMeta(meta);

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, filler1);
        }
        ItemStack filler2 = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        meta = filler2.getItemMeta();
        meta.setDisplayName(" ");
        filler2.setItemMeta(meta);
        inventory.setItem(0, filler2);
        inventory.setItem(1, filler2);
        inventory.setItem(9, filler2);
        inventory.setItem(44, filler2);
        inventory.setItem(52, filler2);
        inventory.setItem(53, filler2);

        ItemStack end = new ItemStack(Material.ENDER_EYE);
        meta = end.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Želiš da odeš u end svet?");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.RED + "Nažalost end svet je trenutno isključen.");
        lore.add(ChatColor.RED + "Planira se custom end.");
        meta.setLore(lore);
        end.setItemMeta(meta);
        inventory.setItem(7, end);

        ItemStack dragon = new ItemStack(Material.DRAGON_HEAD);
        meta = dragon.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Želiš da stvoriš zmaja?");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.RED + "" + ChatColor.UNDERLINE + "TI TRENUTNO NEMAŠ TOKEN...");
        lore.add("");
        lore.add(ChatColor.RED + "End svet trenutno nije aktivan.");
        lore.add(ChatColor.RED + "Planira se custom end.");
        meta.setLore(lore);
        dragon.setItemMeta(meta);
        inventory.setItem(8, dragon);

        ItemStack shop = new ItemStack(Material.CHEST);
        meta = shop.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Daily Shop");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Prodaj resurse za novac u Daily Shop-u");
        meta.setLore(lore);
        shop.setItemMeta(meta);
        inventory.setItem(21, shop);

        ItemStack auction = new ItemStack(Material.LECTERN);
        meta = auction.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Aukcija");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Kupi i prodaj stvari ostalim igračima...");
        lore.add("");
        lore.add(ChatColor.RED + "Još nije implementirano");
        meta.setLore(lore);
        auction.setItemMeta(meta);
        inventory.setItem(22, auction);

        ItemStack achievements = new ItemStack(Material.SKULL_BANNER_PATTERN);
        meta = achievements.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Achievements");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Ovde možeš proveriti sva svoja dostignuća...");
        //lore.add()
        lore.add("");
        lore.add(ChatColor.RED + "Sistem završen, spremaju se konkretni achievementi");
        meta.setLore(lore);
        achievements.setItemMeta(meta);
        inventory.setItem(23, achievements);

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);  // Set the player's head
            playerHead.setItemMeta(skullMeta);
        }
        meta = playerHead.getItemMeta();
        meta.setDisplayName(ChatColor.RED+ "Sve informacije o tvom nalogu...");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Novac: " + ChatColor.YELLOW + MainPlugin.economyPlugin.getBalance(player.getUniqueId()) + " RSD");
        lore.add(ChatColor.GRAY + "Achievement Poeni: " + ChatColor.YELLOW + "0 ⛁");
        lore.add("");
        String totalTime = "";
        Integer time = player.getStatistic(Statistic.TOTAL_WORLD_TIME);
        if (time != null) {
            time /= 20;
            long secs = time % 60;
            time/=60;
            long mins = time % 60;
            time/=60;
            long hours = time % 24;
            time/=24;
            long days = time;
            if (days > 0) {
                totalTime += (days + " dan");
                if (days > 1) totalTime += "a";
                totalTime += " ";
            }
            if (hours > 0) {
                totalTime += (hours + " sat");
                if (hours > 1) totalTime += "i";
                totalTime += " ";
            }
            if (mins >= 0) {
                totalTime += (mins + " minut");
                if (mins > 1) totalTime += "a";
                totalTime += " ";
            }
            lore.add(ChatColor.GRAY + "Vreme: " +  ChatColor.YELLOW + totalTime);
        }
        meta.setLore(lore);
        playerHead.setItemMeta(meta);
        inventory.setItem(31, playerHead);

        ItemStack jobs = new ItemStack(Material.GOLDEN_SWORD);
        meta = jobs.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Jobs");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Ovde možeš dobiti informacije o poslovima...");
        lore.add(ChatColor.RED + "" +ChatColor.BOLD + "Bitno!" + " nakon izbora, posao ne možete promeniti!");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attack_speed", 0, AttributeModifier.Operation.ADD_NUMBER));
        jobs.setItemMeta(meta);
        inventory.setItem(30, jobs);

        ItemStack upgrade = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        meta = upgrade.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Upgrades");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "U zamenu za neke resure možeš trajno napredovati...");
        lore.add("");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        upgrade.setItemMeta(meta);


        inventory.setItem(32, upgrade);

        player.openInventory(inventory);
    }
    private boolean handleHELP(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            createHelp(player);
            return true;
        }
        return false;
    }
}