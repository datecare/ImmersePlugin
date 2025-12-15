package me.randjo.statistics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.randjo.commands.MainCommandExecutor.createHelp;
import static me.randjo.statistics.StatReader.checkPlayerStats;

public class Achievements implements Listener  {
    public static AchievementHolder[] achievementHolders = new AchievementHolder[] {
            /*
            new AchievementHolder("Botaničar", "Sruši po jedan cvet od svih vrsta", 0),
            new AchievementHolder("Ledeno doba", "Iskopaj 64 ice blokova", 1),
            new AchievementHolder("Nepažljiv", "Umri 5 puta",2 ),
            new AchievementHolder("Pecaroš", "Upecaj 128 riba", 3),
        */
            new AchievementHolder("", "", 0),
            new AchievementHolder("", "", 1),
            new AchievementHolder("", "", 2),
            new AchievementHolder("", "", 3),
            new AchievementHolder("", "", 4),
            new AchievementHolder("", "", 5),
            new AchievementHolder("", "", 6),
            new AchievementHolder("", "", 7),
            new AchievementHolder("", "", 8),
            new AchievementHolder("", "", 9),
            new AchievementHolder("", "", 10),
            new AchievementHolder("", "", 11),
            new AchievementHolder("", "", 12),
            new AchievementHolder("", "", 13),
            new AchievementHolder("", "", 14),
            new AchievementHolder("", "", 15),
            new AchievementHolder("", "", 16),
            new AchievementHolder("", "", 17),
            new AchievementHolder("", "", 18),
            new AchievementHolder("", "", 19),
            new AchievementHolder("", "", 20),
            new AchievementHolder("", "", 21),
            new AchievementHolder("", "", 22),
            new AchievementHolder("", "", 23),
            new AchievementHolder("", "", 24),
            new AchievementHolder("", "", 25),
            new AchievementHolder("", "", 26),
            new AchievementHolder("", "", 27),
            new AchievementHolder("", "", 28),
            new AchievementHolder("", "", 29),
            new AchievementHolder("", "", 30),
            new AchievementHolder("", "", 31),
            new AchievementHolder("", "", 32),
            new AchievementHolder("", "", 33),
            new AchievementHolder("", "", 34),
            new AchievementHolder("", "", 35),
            new AchievementHolder("", "", 36),
            new AchievementHolder("", "", 37),
            new AchievementHolder("", "", 38),
            new AchievementHolder("", "", 39),
            new AchievementHolder("", "", 40),
            new AchievementHolder("", "", 41),
            new AchievementHolder("", "", 42),
            new AchievementHolder("", "", 43),
            new AchievementHolder("", "", 44),
            new AchievementHolder("", "", 45),
            new AchievementHolder("", "", 46),
            new AchievementHolder("", "", 47),
            new AchievementHolder("", "", 48),
            new AchievementHolder("", "", 49),
            new AchievementHolder("", "", 50),
            new AchievementHolder("", "", 51),
            new AchievementHolder("", "", 52),
            new AchievementHolder("", "", 53),
            new AchievementHolder("", "", 54),
            new AchievementHolder("", "", 55),
            new AchievementHolder("", "", 56),
            new AchievementHolder("", "", 57),
            new AchievementHolder("", "", 58),
            new AchievementHolder("", "", 59),
            new AchievementHolder("", "", 60),
            new AchievementHolder("", "", 61),
            new AchievementHolder("", "", 62),
            new AchievementHolder("", "", 63),
            new AchievementHolder("", "", 64),
            new AchievementHolder("", "", 65),
            new AchievementHolder("", "", 66),
            new AchievementHolder("", "", 67),
            new AchievementHolder("", "", 68),
            new AchievementHolder("", "", 69),
            new AchievementHolder("", "", 70),
            new AchievementHolder("", "", 71),
            new AchievementHolder("", "", 72),
            new AchievementHolder("", "", 73),
            new AchievementHolder("", "", 74),
            new AchievementHolder("", "", 75),
            new AchievementHolder("", "", 76),
            new AchievementHolder("", "", 77),
            new AchievementHolder("", "", 78),
            new AchievementHolder("", "", 79),
            new AchievementHolder("", "", 80),
            new AchievementHolder("", "", 81),
            new AchievementHolder("", "", 82),
            new AchievementHolder("", "", 83),
            new AchievementHolder("", "", 84),
            new AchievementHolder("", "", 85),
            new AchievementHolder("", "", 86),
            new AchievementHolder("", "", 87),
            new AchievementHolder("", "", 88),
            new AchievementHolder("", "", 89),
            new AchievementHolder("", "", 90),
            new AchievementHolder("", "", 91),
            new AchievementHolder("", "", 92),
            new AchievementHolder("", "", 93),
            new AchievementHolder("", "", 94),
            new AchievementHolder("", "", 95),
            new AchievementHolder("", "", 96),
            new AchievementHolder("", "", 97),
            new AchievementHolder("", "", 98),
            new AchievementHolder("", "", 99),
            new AchievementHolder("", "", 100),
            new AchievementHolder("", "", 101),
            new AchievementHolder("", "", 102),
            new AchievementHolder("", "", 103),
            new AchievementHolder("", "", 104),
            new AchievementHolder("", "", 105),
            new AchievementHolder("", "", 106),
            new AchievementHolder("", "", 107),
            new AchievementHolder("", "", 108),
            new AchievementHolder("", "", 109),
            new AchievementHolder("", "", 110),
            new AchievementHolder("", "", 111),
            new AchievementHolder("", "", 112),
            new AchievementHolder("", "", 113),
            new AchievementHolder("Ipak živ", "", 114),
            new AchievementHolder("", "", 115),
            new AchievementHolder("", "", 116),
            new AchievementHolder("", "", 117),
            new AchievementHolder("", "", 118),
            new AchievementHolder("", "", 119),
            new AchievementHolder("", "", 120),
            new AchievementHolder("", "", 121),
            new AchievementHolder("", "", 122),
            new AchievementHolder("", "", 123),
            new AchievementHolder("", "", 124),
            new AchievementHolder("", "", 125),
            new AchievementHolder("", "", 126),
            new AchievementHolder("", "", 127),
            new AchievementHolder("", "", 128),
            new AchievementHolder("", "", 129),
            new AchievementHolder("", "", 130),
            new AchievementHolder("", "", 131),
            new AchievementHolder("", "", 132),
            new AchievementHolder("", "", 133),
            new AchievementHolder("", "", 134)



    };
    private static class AchievementHolder {
        public String name;
        public String description;
        public int slot;
        AchievementHolder(String name, String description, int slot) {
            this.name = name;
            this.description = description;
            this.slot = slot;
        }

        static String slotToName(int slot, int type) {
            return achievementHolders[slot -= type * 45].name;
        }
    }

    public static void createBasicMenu(Player player) {
        int[] stats = checkPlayerStats(player);
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Basic " + ChatColor.RESET + "Achievements");
        ItemStack notFulfilled = new ItemStack(Material.GRAY_STAINED_GLASS);
        ItemMeta meta = notFulfilled.getItemMeta();
        ItemStack notTaken = new ItemStack(Material.YELLOW_STAINED_GLASS);
        ItemMeta metaTaken = notTaken.getItemMeta();
        ItemStack done = new ItemStack(Material.LIME_STAINED_GLASS);
        ItemMeta metaDone = notTaken.getItemMeta();



        /////////////////////////////////////// ACHS //////////////////////////////////////////////////

        for (int i = 0; i <= 44; i++) {
            AchievementHolder holder = achievementHolders[i];
            List lore = new ArrayList();
            if (stats[holder.slot] == 0) {
                meta.setDisplayName(ChatColor.DARK_GRAY + "[" + holder.name + "]");
                lore.add(ChatColor.GRAY + holder.description);
                meta.setLore(lore);
                notFulfilled.setItemMeta(meta);
                inventory.setItem(holder.slot, notFulfilled);
            }
            else if (stats[holder.slot] == 1) {
                metaTaken.setDisplayName(ChatColor.YELLOW + "[Otkrili ste novi achievement]");
                lore.add(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da preuzmeš dostignuće...");
                metaTaken.setLore(lore);
                notTaken.setItemMeta(metaTaken);
                inventory.setItem(holder.slot, notTaken);
            }
            else if (stats[holder.slot] == 2) {
                metaDone.setDisplayName(ChatColor.AQUA + "[" + holder.name + "]");
                lore.add(ChatColor.GRAY + holder.description);
                lore.add("");
                lore.add(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da podeliš sa ostalima...");
                metaDone.setLore(lore);
                done.setItemMeta(metaDone);
                inventory.setItem(holder.slot, done);
            }
        }

        ////////////////////////////////////// POSLEDNJI RED ////////////////////////////////////////////////
        ItemStack current = new ItemStack(Material.AXOLOTL_BUCKET);
        meta = current.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Ti si trenutno na " + ChatColor.DARK_GREEN + "BASIC " + ChatColor.RESET + "achievementima");
        current.setItemMeta(meta);
        inventory.setItem(48, current);
        ItemStack other = new ItemStack(Material.WATER_BUCKET);
        meta = other.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "[Levi klik] " + ChatColor.RESET + "za " + ChatColor.GOLD + "ADVANCED " + ChatColor.RESET + "achievemente");
        other.setItemMeta(meta);
        inventory.setItem(49, other);
        meta.setDisplayName(ChatColor.YELLOW + "[Levi klik] " + ChatColor.RESET + "za " + ChatColor.DARK_PURPLE + "EXPERT " + ChatColor.RESET + "achievemente");
        other.setItemMeta(meta);
        inventory.setItem(50, other);

        ItemStack back = new ItemStack(Material.BARRIER);
        meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
        back.setItemMeta(meta);
        inventory.setItem(45, back);

        player.openInventory(inventory);
    }

    public void createAdvancedMenu(Player player) {
        int[] stats = checkPlayerStats(player);
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Advanced " + ChatColor.RESET + "Achievements");
        ItemStack notFulfilled = new ItemStack(Material.GRAY_STAINED_GLASS);
        ItemMeta meta = notFulfilled.getItemMeta();
        ItemStack notTaken = new ItemStack(Material.YELLOW_STAINED_GLASS);
        ItemMeta metaTaken = notTaken.getItemMeta();
        ItemStack done = new ItemStack(Material.ORANGE_STAINED_GLASS);
        ItemMeta metaDone = notTaken.getItemMeta();

        for (int i = 45; i <= 89; i++) {
            AchievementHolder holder = achievementHolders[i];
            List lore = new ArrayList();
            if (stats[holder.slot] == 0) {
                meta.setDisplayName(ChatColor.DARK_GRAY + "[" + holder.name + "]");
                lore.add(ChatColor.GRAY + holder.description);
                meta.setLore(lore);
                notFulfilled.setItemMeta(meta);
                inventory.setItem(holder.slot - 45, notFulfilled);
            }
            else if (stats[holder.slot] == 1) {
                metaTaken.setDisplayName(ChatColor.YELLOW + "[Otkrili ste novi achievement]");
                lore.add(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da preuzmeš dostignuće...");
                metaTaken.setLore(lore);
                notTaken.setItemMeta(metaTaken);
                inventory.setItem(holder.slot - 45, notTaken);
            }
            else if (stats[holder.slot] == 2) {
                metaDone.setDisplayName(ChatColor.AQUA + "[" + holder.name + "]");
                lore.add(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da podeliš sa ostalima...");
                metaDone.setLore(lore);
                done.setItemMeta(metaDone);
                inventory.setItem(holder.slot - 45, done);
            }
        }
        ItemStack current = new ItemStack(Material.AXOLOTL_BUCKET);
        meta = current.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Ti si trenutno na " + ChatColor.GOLD + "ADVANCED " + ChatColor.RESET + "achievementima");
        current.setItemMeta(meta);
        inventory.setItem(49, current);
        ItemStack other = new ItemStack(Material.WATER_BUCKET);
        meta = other.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "[Levi klik] " + ChatColor.RESET + "za " + ChatColor.DARK_GREEN + "BASIC " + ChatColor.RESET + "achievemente");
        other.setItemMeta(meta);
        inventory.setItem(48, other);
        meta.setDisplayName(ChatColor.YELLOW + "[Levi klik] " + ChatColor.RESET + "za " + ChatColor.DARK_PURPLE + "EXPERT " + ChatColor.RESET + "achievemente");
        other.setItemMeta(meta);
        inventory.setItem(50, other);

        ItemStack back = new ItemStack(Material.BARRIER);
        meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
        back.setItemMeta(meta);
        inventory.setItem(45, back);

        player.openInventory(inventory);
    }

    public void createExpertMenu(Player player) {
        int[] stats = checkPlayerStats(player);
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "Expert " + ChatColor.RESET + "Achievements");
        ItemStack notTaken = new ItemStack(Material.YELLOW_STAINED_GLASS);
        ItemMeta metaTaken = notTaken.getItemMeta();
        ItemStack done = new ItemStack(Material.PURPLE_STAINED_GLASS);
        ItemMeta metaDone = notTaken.getItemMeta();

        for (int i = 90; i <= 134; i++) {
            AchievementHolder holder = achievementHolders[i];
            List lore = new ArrayList();
            if (stats[holder.slot] == 1) {
                metaTaken.setDisplayName(ChatColor.YELLOW + "[Otkrili ste novi achievement]");
                lore.add(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da preuzmeš dostignuće...");
                metaTaken.setLore(lore);
                notTaken.setItemMeta(metaTaken);
                inventory.setItem(holder.slot - 90, notTaken);
            }
            else if (stats[holder.slot] == 2) {
                metaDone.setDisplayName(ChatColor.AQUA + "[" + holder.name + "]");
                lore.add(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da podeliš sa ostalima...");
                metaDone.setLore(lore);
                done.setItemMeta(metaDone);
                inventory.setItem(holder.slot - 90, done);
            }
        }

        ItemStack current = new ItemStack(Material.AXOLOTL_BUCKET);
        ItemMeta meta = current.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Ti si trenutno na " + ChatColor.DARK_PURPLE + "EXPERT " + ChatColor.RESET + "achievementima");
        current.setItemMeta(meta);
        inventory.setItem(50, current);
        ItemStack other = new ItemStack(Material.WATER_BUCKET);
        meta = other.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "[Levi klik] " + ChatColor.RESET + "za " + ChatColor.GOLD + "ADVANCED " + ChatColor.RESET + "achievemente");
        other.setItemMeta(meta);
        inventory.setItem(49, other);
        meta.setDisplayName(ChatColor.YELLOW + "[Levi klik] " + ChatColor.RESET + "za " + ChatColor.DARK_GREEN + "BASIC " + ChatColor.RESET + "achievemente");
        other.setItemMeta(meta);
        inventory.setItem(48, other);

        ItemStack back = new ItemStack(Material.BARRIER);
        meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
        back.setItemMeta(meta);
        inventory.setItem(45, back);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "HELP MENU")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null) return;
                else if (clickedItem.getType() == Material.SKULL_BANNER_PATTERN) {
                    createBasicMenu(player);
                }
            }
        }

        else if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Basic " + ChatColor.RESET + "Achievements")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null) return;
                if (event.getSlot() == 49) createAdvancedMenu(player);
                if (event.getSlot() == 50) createExpertMenu(player);
                if (event.getSlot() == 45) createHelp(player);
                /*
                else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "[Otkrili ste novi achievement]"))
                {
                    StatReader.claimAchievement(player.getName(), event.getSlot());
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
                    createBasicMenu(player);
                }
                else if (clickedItem.getItemMeta().getLore().contains(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da podeliš sa ostalima...")) {
                    player.closeInventory();

                    String hoverText = "";
                    if (clickedItem.hasItemMeta()) {
                        if (clickedItem.getItemMeta().hasLore()) {
                            hoverText = clickedItem.getItemMeta().getLore().getFirst();
                        }
                    }

                    BaseComponent[] hoverComponents = new BaseComponent[] { new TextComponent(hoverText) };
                    HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, hoverComponents);
                    TextComponent message = new TextComponent(ChatColor.AQUA + "(❢) " + ChatColor.GRAY + "Igrač " + player.getName() + " je odlučio da podeli svoje dostignuće — ");
                    TextComponent hover = new TextComponent(ChatColor.GREEN + "[" + achievementHolders[event.getSlot()].name + "]");
                    hover.setHoverEvent(hoverEvent);
                    message.addExtra(hover);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.spigot().sendMessage(message);
                    }
                }

                 */
            }
        }

        else if (event.getView().getTitle().equals(ChatColor.GOLD + "Advanced " + ChatColor.RESET + "Achievements")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null) return;
                if (event.getSlot() == 48) createBasicMenu(player);
                if (event.getSlot() == 50) createExpertMenu(player);
                if (event.getSlot() == 45) createHelp(player);
                /*
                else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "[Otkrili ste novi achievement]"))
                {
                    StatReader.claimAchievement(player.getName(), event.getSlot() + 45);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
                    createAdvancedMenu(player);
                }
                else if (clickedItem.getItemMeta().getLore().contains(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da podeliš sa ostalima...")) {
                    player.closeInventory();
                    Bukkit.broadcastMessage(ChatColor.AQUA + "(❢) " + ChatColor.GRAY + "Igrač " + player.getName() + " je odlučio da podeli svoje dostignuće — " + ChatColor.GOLD + "[" + achievementHolders[event.getSlot() + 45].name + "]");
                }

                 */
            }
        }

        else if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Expert " + ChatColor.RESET + "Achievements")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null) return;
                if (event.getSlot() == 48) createBasicMenu(player);
                if (event.getSlot() == 49) createAdvancedMenu(player);
                if (event.getSlot() == 45) createHelp(player);
                /*
                else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "[Otkrili ste novi achievement]"))
                {
                    StatReader.claimAchievement(player.getName(), event.getSlot() + 90);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
                    createExpertMenu(player);
                }
                else if (clickedItem.getItemMeta().getLore().contains(ChatColor.YELLOW + "[Levi klik]" + ChatColor.GRAY + " da podeliš sa ostalima...")) {
                    player.closeInventory();
                    Bukkit.broadcastMessage(ChatColor.AQUA + "(❢) " + ChatColor.GRAY + "Igrač " + player.getName() + " je odlučio da podeli svoje dostignuće — " + ChatColor.LIGHT_PURPLE + "[" + achievementHolders[event.getSlot() + 90].name + "]");
                }

                 */
            }
        }
    }
}
