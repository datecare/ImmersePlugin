package me.randjo.restriction;

import me.randjo.economy.EconomyPlugin;
import me.randjo.jobs.JobsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.randjo.commands.MainCommandExecutor.createHelp;

public class RestrictionListener implements Listener {

    private final RestrictionPlugin plugin;
    private final EconomyPlugin economyPlugin;
    private final JobsPlugin jobsPlugin;
    private final LinkedHashMap<Material, Integer> cene;

    private final float lvl1BonusClassMoneyMultiplier = 1.1f;
    private final float lvl2BonusClassMoneyMultiplier = 1.2f;
    private final float lvl3BonusClassMoneyMultiplier = 1.3f;
    private final float lvl4BonusClassMoneyMultiplier = 1.4f;
    private final float lvl5BonusClassMoneyMultiplier = 1.5f;

    // Constructor to pass the plugin instance
    public RestrictionListener(RestrictionPlugin plugin, EconomyPlugin economyPlugin, JobsPlugin jobsPlugin) {
        this.plugin = plugin;
        this.economyPlugin = economyPlugin;
        this.jobsPlugin = jobsPlugin;

        cene = new LinkedHashMap<>();
        // MINERALI
        cene.put(Material.CHARCOAL, 70);
        cene.put(Material.COAL, 100);
        cene.put(Material.IRON_INGOT, 250);
        cene.put(Material.GOLD_INGOT, 250);
        cene.put(Material.COPPER_INGOT, 100);
        cene.put(Material.REDSTONE, 130);
        cene.put(Material.LAPIS_LAZULI, 130);
        cene.put(Material.GLOWSTONE, 150);
        cene.put(Material.QUARTZ, 120);
        cene.put(Material.AMETHYST_SHARD, 350);
        // PLODOVI
        cene.put(Material.POTATO, 80);
        cene.put(Material.CARROT, 80);
        cene.put(Material.BEETROOT, 80);
        cene.put(Material.SWEET_BERRIES, 80);
        cene.put(Material.GLOW_BERRIES, 110);
        cene.put(Material.MELON_SEEDS, 100);
        cene.put(Material.PUMPKIN_SEEDS, 100);
        cene.put(Material.COCOA_BEANS, 150);
        cene.put(Material.SUGAR_CANE, 70);
        cene.put(Material.CACTUS, 90);
        cene.put(Material.APPLE, 220);
        cene.put(Material.WHEAT, 100);
        cene.put(Material.BAMBOO, 30);
        cene.put(Material.DRIED_KELP, 60);
        cene.put(Material.CHORUS_FRUIT, 200);
        // OSTACI
        cene.put(Material.ROTTEN_FLESH, 80);
        cene.put(Material.BONE, 100);
        cene.put(Material.STRING, 90);
        cene.put(Material.SPIDER_EYE, 90);
        cene.put(Material.GUNPOWDER, 120);
        cene.put(Material.ENDER_EYE, 350);
        cene.put(Material.SLIME_BALL, 225);
        cene.put(Material.MAGMA_CREAM, 120);
        cene.put(Material.BLAZE_ROD, 130);
        cene.put(Material.PRISMARINE_SHARD, 250);
        cene.put(Material.LEATHER, 100);
        cene.put(Material.RABBIT_HIDE, 120);
        cene.put(Material.FEATHER, 90);
        cene.put(Material.GHAST_TEAR, 500);
        cene.put(Material.PHANTOM_MEMBRANE, 225);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if the block is diamond ore or deepslate diamond ore
        if (event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.DEEPSLATE_DIAMOND_ORE) {

            ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
            if (!tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                if (tool.getType() == Material.DIAMOND_PICKAXE || tool.getType() == Material.IRON_PICKAXE || tool.getType() == Material.NETHERITE_PICKAXE) {
                    int fortuneLevel = tool.getEnchantmentLevel(Enchantment.FORTUNE);

                    if (fortuneLevel == 0) {
                        if (Math.random() < 0.25) {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND, 1));
                        }
                    }
                    else
                    {
                        if (fortuneLevel == 1) {
                            double m = Math.random();
                            if  (m < 0.4) {
                                int numDiamonds = 1;
                                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND, numDiamonds));
                            }
                        }
                        if (fortuneLevel == 2) {
                            if  (Math.random() > 0.3) {
                                int numDiamonds = 1;
                                if (Math.random() < 0.2) {
                                    numDiamonds = 2;
                                }
                                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND, numDiamonds));
                            }
                        }
                        if (fortuneLevel == 3) {
                            if  (Math.random() < 0.7) {
                                double k = Math.random();
                                int numDiamonds = 1;
                                if (k >= 0.2 && k <= 0.8) {
                                    numDiamonds = 2;
                                }
                                if (k > 0.8) {
                                    numDiamonds = 3;
                                }
                                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND, numDiamonds));
                            }
                        }
                    }
                }
                event.setDropItems(false);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (inventory.getType().equals(InventoryType.ENCHANTING)) {
            if (!playerUnlockedEnchTable(player)) {
                event.setCancelled(true); // Cancel the enchanting table opening
                player.sendMessage(ChatColor.RED + "Prvo moraš da otključaš Enchanting Table.");
            }
        }
    }

    public boolean playerUnlockedEnchTable(Player player) {
        if (plugin.getPlayerNames().contains(player.getName())) {
            return true;
        }
        return false;
    }

    public static void createShop(Player player) {
        Inventory newInventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "" + ChatColor.BOLD + "Daily Shop");
        ItemStack minerali = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = minerali.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Minerali ⛏");
        minerali.setItemMeta(meta);
        newInventory.setItem(3, minerali);
        ItemStack plodovi = new ItemStack(Material.CARROT);
        meta = plodovi.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Plodovi \uD83C\uDF3F");
        plodovi.setItemMeta(meta);
        newInventory.setItem(4, plodovi);
        ItemStack ostaci = new ItemStack(Material.ROTTEN_FLESH);
        meta = ostaci.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Ostaci ⚔");
        ostaci.setItemMeta(meta);
        newInventory.setItem(5, ostaci);

        ItemStack back = new ItemStack(Material.BARRIER);
        meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
        back.setItemMeta(meta);
        newInventory.setItem(0, back);
        player.openInventory(newInventory);
    }

    public static void createJobMenu(Player player) {
        Inventory newInventory = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "JOBS");
        ItemStack filler = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        for (int i = 0; i < 27; i++)  newInventory.setItem(i, filler);

        ItemStack hunter = new ItemStack(Material.DIAMOND_SWORD);
        meta = hunter.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD+ "HUNTER");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attack_speed", 0, AttributeModifier.Operation.ADD_NUMBER));
        List lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.WHITE + "LEVEL: " + JobsPlugin.getJobLevel(player.getName(), "hunter"));
        lore.add(ChatColor.WHITE + "XP: " + String.format("%.2f", JobsPlugin.getJobXP(player.getName(), "hunter")) + "/" + JobsPlugin.getXpNeeded()[Math.toIntExact(JobsPlugin.getJobLevel(player.getName(), "hunter")) - 1]);
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "da bi video informacije u vezi posla");
        lore.add(ChatColor.YELLOW + "[SHIFT + DESNI KLIK] " + ChatColor.AQUA + "da bi uzeo ovaj posao");
        meta.setLore(lore);
        if (JobsPlugin.getSelectedJobs(player.getName()).contains("hunter")) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        hunter.setItemMeta(meta);
        newInventory.setItem(11, hunter);

        ItemStack miner = new ItemStack(Material.GOLDEN_PICKAXE);
        meta = miner.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "MINER");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attack_speed", 0, AttributeModifier.Operation.ADD_NUMBER));
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.WHITE + "LEVEL: " + JobsPlugin.getJobLevel(player.getName(), "miner"));
        lore.add(ChatColor.WHITE + "XP: " + String.format("%.2f",JobsPlugin.getJobXP(player.getName(), "miner")) + "/" + JobsPlugin.getXpNeeded()[Math.toIntExact(JobsPlugin.getJobLevel(player.getName(), "miner")) - 1]);
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "da bi video informacije u vezi posla");
        lore.add(ChatColor.YELLOW + "[SHIFT + DESNI KLIK] " + ChatColor.AQUA + "da bi uzeo ovaj posao");
        meta.setLore(lore);
        if (JobsPlugin.getSelectedJobs(player.getName()).contains("miner")) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        miner.setItemMeta(meta);
        newInventory.setItem(12, miner);

        ItemStack farmer = new ItemStack(Material.NETHERITE_HOE);
        meta = farmer.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "FARMER");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attack_speed", 0, AttributeModifier.Operation.ADD_NUMBER));
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.WHITE + "LEVEL: " + JobsPlugin.getJobLevel(player.getName(), "farmer"));
        lore.add(ChatColor.WHITE + "XP: " + String.format("%.2f",JobsPlugin.getJobXP(player.getName(), "farmer")) + "/" + JobsPlugin.getXpNeeded()[Math.toIntExact(JobsPlugin.getJobLevel(player.getName(), "farmer")) - 1]);
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "da bi video informacije u vezi posla");
        lore.add(ChatColor.YELLOW + "[SHIFT + DESNI KLIK] " + ChatColor.AQUA + "da bi uzeo ovaj posao");
        meta.setLore(lore);
        if (JobsPlugin.getSelectedJobs(player.getName()).contains("farmer")) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        farmer.setItemMeta(meta);
        newInventory.setItem(14, farmer);

        /*
        ItemStack lumberjack = new ItemStack(Material.IRON_AXE);
        meta = lumberjack.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "§x§8§b§4§5§1§3§lLUMBERJACK");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attack_speed", 0, AttributeModifier.Operation.ADD_NUMBER));
        lore = new ArrayList();
        lore.add("");
        lore.add("§lLEVEL: " + JobsPlugin.getJobLevel(player.getName(), "lumberjack"));
        lore.add("§lXP: " + JobsPlugin.getJobXP(player.getName(), "lumberjack"));
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "da bi uzeo ovaj posao");
        lore.add(ChatColor.YELLOW + "[DESNI KLIK] " + ChatColor.AQUA + "da bi napustio ovaj posao");
        meta.setLore(lore);
        if (JobsPlugin.getSelectedJobs(player.getName()).contains("lumberjack")) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        lumberjack.setItemMeta(meta);
        newInventory.setItem(14, lumberjack);
         */

        ItemStack adventurer = new ItemStack(Material.COMPASS);
        meta = adventurer.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "ADVENTURER");
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.RED + "Da bi otključao ovaj posao:");
        lore.add(ChatColor.RED + "moraš biti level 10 u svim ostalim poslovima");
        lore.add(ChatColor.RED + "moraš platiti 25k RSD");
        meta.setLore(lore);
        if (JobsPlugin.getSelectedJobs(player.getName()).contains("adventurer")) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        adventurer.setItemMeta(meta);
        newInventory.setItem(15, adventurer);

        /*
        ItemStack info = new ItemStack(Material.OAK_SIGN);
        meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Hunter Info");
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "za više informacija o poslu");
        meta.setLore(lore);
        info.setItemMeta(meta);
        newInventory.setItem(19, info);

        info = new ItemStack(Material.DARK_OAK_SIGN);
        meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Miner Info");
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "za više informacija o poslu");
        meta.setLore(lore);
        info.setItemMeta(meta);
        newInventory.setItem(21, info);

        info = new ItemStack(Material.BIRCH_SIGN);
        meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Farmer Info");
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "za više informacija o poslu");
        meta.setLore(lore);
        info.setItemMeta(meta);
        newInventory.setItem(23, info);


        info = new ItemStack(Material.JUNGLE_SIGN);
        meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "§x§8§b§4§5§1§3§lLumberjack Info");
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "za više informacija o poslu");
        meta.setLore(lore);
        info.setItemMeta(meta);
        newInventory.setItem(23, info);


        info = new ItemStack(Material.WARPED_SIGN);
        meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Adventurer Info");
        lore = new ArrayList();
        lore.add("");
        lore.add(ChatColor.YELLOW + "[LEVI KLIK] " + ChatColor.AQUA + "za više informacija o poslu");
        meta.setLore(lore);
        info.setItemMeta(meta);
        newInventory.setItem(25, info);
        */
        ItemStack info = new ItemStack(Material.BOOK);
        meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "                        INFO");
        lore = new ArrayList();
        //lore.add("");
        lore.add("§eXP za posao se dobija akcijama vezanim za posao");
        lore.add("");
        lore.add("§aNa levelima 4, 9, 14 i 19 kada dođete do max XP");
        lore.add ("§amorate da ispunite uslove koji će se otkriti u");
        lore.add("§ainfo delu tog posla, na odgovarajućem levelu.");
        lore.add("");
        lore.add("§bNa 15-om levelu posla dobijate opciju da izaberete");
        lore.add("§bnovi posao besplatno.");
        lore.add("");
        lore.add("§6Posao adventurer je posao koji omogućava");
        lore.add("§6dobijanje tokena potrebnih za odlazak u end");
        meta.setLore(lore);
        info.setItemMeta(meta);
        newInventory.setItem(13, info);


        ItemStack back = new ItemStack(Material.BARRIER);
        meta = back.getItemMeta();
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

        ///////////////////////////////////// HELP MENU ///////////////////////////////////////////////////

        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "HELP MENU")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null) return;
                else if (clickedItem.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                    Inventory newInventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "" + ChatColor.BOLD + "Upgrades");
                    ItemStack enchtab = new ItemStack(Material.ENCHANTING_TABLE);
                    ItemMeta meta = enchtab.getItemMeta();
                    if (playerUnlockedEnchTable(player)) {
                        meta.setDisplayName(ChatColor.GREEN + "Otključali ste enchantovanje");
                        List lore = new ArrayList();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Možete kupiti još enchanting table-ova ovde");
                        lore.add(ChatColor.GRAY + "Potrebno: " + ChatColor.YELLOW + "1000 RSD");
                        meta.setLore(lore);
                        meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    else {
                        meta.setDisplayName(ChatColor.DARK_PURPLE + "Otključaj enchanting table");
                        List lore = new ArrayList();
                        lore.add("");
                        lore.add(ChatColor.GRAY + "Ova opcija ti omogućava korišćenje enchanting table-a");
                        lore.add(ChatColor.GRAY + "Nakon otključavanja dobićete jedan enchanting table");
                        lore.add(ChatColor.GRAY + "Potrebno: " + ChatColor.YELLOW + "5000 RSD" + ChatColor.GRAY + " i " + ChatColor.GREEN + "50 EXP levela");
                        meta.setLore(lore);
                    }
                    enchtab.setItemMeta(meta);
                    newInventory.setItem(4, enchtab);

                    ItemStack back = new ItemStack(Material.BARRIER);
                    meta = back.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
                    back.setItemMeta(meta);
                    newInventory.setItem(0, back);
                    player.openInventory(newInventory);
                }
                else if (clickedItem.getType() == Material.CHEST) {
                    createShop(player);
                }
                else if (clickedItem.getType() == Material.GOLDEN_SWORD) {
                      createJobMenu(player);
                }
            }
        }

        //////////////////////////////////////////////// SHOP STUFF ////////////////////////////////////////////////////


        else if (event.getView().getTitle().equals(ChatColor.BLUE + "" + ChatColor.BOLD + "Daily Shop")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.GOLD_INGOT) {
                    boolean isMiner = JobsPlugin.getSelectedJobs(player.getName()).contains("miner");
                    Inventory newInventory = Bukkit.createInventory(null, 36, ChatColor.YELLOW + "" + ChatColor.BOLD + "MINERALI ⛏");
                    Iterator<Map.Entry<Material, Integer>> iterator = cene.entrySet().iterator();
                    ItemStack filler = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                    ItemMeta fmeta = filler.getItemMeta();
                    fmeta.setDisplayName(" ");
                    filler.setItemMeta(fmeta);
                    for (int j = 0; j <= 35; j++) newInventory.setItem(j, filler);
                    int i = 0;
                    while (iterator.hasNext()) {
                        Map.Entry<Material, Integer> entry = iterator.next();
                        Material material = entry.getKey();
                        Integer price = entry.getValue();
                        if (i < 10) {
                            ItemStack item1 = new ItemStack(material);
                            ItemMeta meta = item1.getItemMeta();

                            meta.setDisplayName(ChatColor.GRAY + "Želiš da prodaš " + ChatColor.YELLOW + "" + ChatColor.BOLD + "x64 " + ChatColor.BOLD + material.toString() + ChatColor.GRAY + "?");
                            List lore = new ArrayList();
                            lore.add("");
                            int broj = plugin.getRemainingSales(player, item1.getType().toString(), "miner");
                            if (broj != 1) {
                                lore.add(ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " puta");
                            } else {
                                lore.add(ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " put");
                            }
                            int priceDiff = price;
                            if (isMiner) price = Math.round(price*JobsPlugin.getBonusMultiplier(player, "miner"));
                            lore.add(ChatColor.AQUA + "Cena po steku iznosi " + ChatColor.GREEN + price + " RSD");
                            if (priceDiff != price) lore.add(ChatColor.YELLOW + "(+" + (price - priceDiff) + "RSD od miner posla)");
                            meta.setLore(lore);
                            item1.setItemMeta(meta);
                            int offset = 0;
                            if (i >= 5) offset = 4;
                            newInventory.setItem(11 + i + offset, item1);
                        }
                        i++;
                    }
                    ItemStack back = new ItemStack(Material.BARRIER);
                    ItemMeta meta = back.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
                    back.setItemMeta(meta);
                    newInventory.setItem(31, back);
                    player.openInventory(newInventory);
                }
                if (clickedItem != null && clickedItem.getType() == Material.CARROT) {
                    boolean isFarmer = JobsPlugin.getSelectedJobs(player.getName()).contains("farmer");
                    Inventory newInventory = Bukkit.createInventory(null, 45, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "PLODOVI \uD83C\uDF3F");
                    Iterator<Map.Entry<Material, Integer>> iterator = cene.entrySet().iterator();
                    ItemStack filler = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    ItemMeta fmeta = filler.getItemMeta();
                    fmeta.setDisplayName(" ");
                    filler.setItemMeta(fmeta);
                    for (int j = 0; j <= 44; j++) newInventory.setItem(j, filler);
                    int i = 0;
                    while (iterator.hasNext()) {
                        Map.Entry<Material, Integer> entry = iterator.next();
                        Material material = entry.getKey();
                        Integer price = entry.getValue();
                        if (i >= 10 && i < 25) {
                            ItemStack item1 = new ItemStack(material);
                            ItemMeta meta = item1.getItemMeta();

                            meta.setDisplayName(ChatColor.GRAY + "Želiš da prodaš " + ChatColor.YELLOW + "" + ChatColor.BOLD + "x64 " + ChatColor.BOLD + material.toString() + ChatColor.GRAY + "?");
                            List lore = new ArrayList();
                            lore.add("");
                            int broj = plugin.getRemainingSales(player, item1.getType().toString(), "farmer");
                            if (broj != 1) {
                                lore.add(ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " puta");
                            } else {
                                lore.add(ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " put");
                            }
                            int priceDiff = price;
                            if (isFarmer) price = Math.round(price*JobsPlugin.getBonusMultiplier(player, "farmer"));
                            lore.add(ChatColor.AQUA + "Cena po steku iznosi " + ChatColor.GREEN + price + " RSD");
                            if (priceDiff != price) lore.add(ChatColor.YELLOW + "(+" + (price - priceDiff) + "RSD od farmer posla)");
                            meta.setLore(lore);
                            item1.setItemMeta(meta);
                            int offset = 0;
                            if (i >= 15) offset = 4;
                            if (i >= 20) offset = 8;
                            newInventory.setItem(11 - 10 + i + offset, item1);
                        }
                        i++;
                    }
                    ItemStack back = new ItemStack(Material.BARRIER);
                    ItemMeta meta = back.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
                    back.setItemMeta(meta);
                    newInventory.setItem(40, back);
                    player.openInventory(newInventory);
                }

                if (clickedItem != null && clickedItem.getType() == Material.ROTTEN_FLESH) {
                    boolean isHunter = JobsPlugin.getSelectedJobs(player.getName()).contains("hunter");
                    Inventory newInventory = Bukkit.createInventory(null, 45, ChatColor.RED + "" + ChatColor.BOLD + "OSTACI ⚔");
                    Iterator<Map.Entry<Material, Integer>> iterator = cene.entrySet().iterator();
                    ItemStack filler = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    ItemMeta fmeta = filler.getItemMeta();
                    fmeta.setDisplayName(" ");
                    filler.setItemMeta(fmeta);
                    for (int j = 0; j <= 44; j++) newInventory.setItem(j, filler);

                    int i = 0;
                    while (iterator.hasNext()) {
                        Map.Entry<Material, Integer> entry = iterator.next();
                        Material material = entry.getKey();
                        Integer price = entry.getValue();
                        if (i >= 25 && i < 40) {
                            ItemStack item1 = new ItemStack(material);
                            ItemMeta meta = item1.getItemMeta();

                            meta.setDisplayName(ChatColor.GRAY + "Želiš da prodaš " + ChatColor.YELLOW + "" + ChatColor.BOLD + "x64 " + ChatColor.BOLD + material.toString() + ChatColor.GRAY + "?");
                            List lore = new ArrayList();
                            lore.add("");
                            int broj = plugin.getRemainingSales(player, item1.getType().toString(), "hunter");
                            if (broj != 1) {
                                lore.add(ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " puta");
                            } else {
                                lore.add(ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " put");
                            }
                            int priceDiff = price;
                            if (isHunter) price = Math.round(price*JobsPlugin.getBonusMultiplier(player, "hunter"));
                            lore.add(ChatColor.AQUA + "Cena po steku iznosi " + ChatColor.GREEN + price + " RSD");
                            if (priceDiff != price) lore.add(ChatColor.YELLOW + "(+" + (price - priceDiff) + "RSD od hunter posla)");                            meta.setLore(lore);
                            item1.setItemMeta(meta);
                            int offset = 0;
                            if (i >= 30) offset = 4;
                            if (i >= 35) offset = 8;
                            newInventory.setItem(11 - 25 + i + offset, item1);
                        }
                        i++;
                    }
                    ItemStack back = new ItemStack(Material.BARRIER);
                    ItemMeta meta = back.getItemMeta();
                    meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Nazad");
                    back.setItemMeta(meta);
                    newInventory.setItem(40, back);
                    player.openInventory(newInventory);
                }

                if (event.getSlot() == 0) createHelp(player);
            }
        }
        else if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "PLODOVI \uD83C\uDF3F")
                || event.getView().getTitle().equals(ChatColor.YELLOW + "" + ChatColor.BOLD + "MINERALI ⛏")
                || event.getView().getTitle().equals(ChatColor.RED + "" + ChatColor.BOLD + "OSTACI ⚔")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.BARRIER) {
                createShop(player);
            } else if (clickedItem != null && clickedItem.getType() != Material.YELLOW_STAINED_GLASS_PANE && clickedItem.getType() != Material.RED_STAINED_GLASS_PANE && clickedItem.getType() != Material.LIME_STAINED_GLASS_PANE) {
                String job = "farmer";
                if (event.getView().getTitle().equals(ChatColor.YELLOW + "" + ChatColor.BOLD + "MINERALI ⛏")) job = "miner";
                if (event.getView().getTitle().equals(ChatColor.RED + "" + ChatColor.BOLD + "OSTACI ⚔")) job = "hunter";
                if (plugin.getRemainingSales(player, clickedItem.getType().toString(), job) > 0) {
                    if (player.getInventory().containsAtLeast(new ItemStack(clickedItem.getType()), 64)) {
                        ItemStack itemToRemove = new ItemStack(clickedItem.getType(), 64);
                        player.getInventory().removeItem(itemToRemove);
                        int price = Math.round(cene.get(clickedItem.getType()));
                        if (event.getView().getTitle().equals(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "PLODOVI \uD83C\uDF3F")
                         && JobsPlugin.getSelectedJobs(player.getName()).contains("farmer")) {
                            price *= JobsPlugin.getBonusMultiplier(player, "farmer");
                        }
                        else if (event.getView().getTitle().equals(ChatColor.YELLOW + "" + ChatColor.BOLD + "MINERALI ⛏")
                            && JobsPlugin.getSelectedJobs(player.getName()).contains("miner")) {
                            price *= JobsPlugin.getBonusMultiplier(player, "miner");
                        }
                        else if (event.getView().getTitle().equals(ChatColor.RED + "" + ChatColor.BOLD + "OSTACI ⚔")
                            && JobsPlugin.getSelectedJobs(player.getName()).contains("hunter")) {
                            price *= JobsPlugin.getBonusMultiplier(player, "hunter");
                        }
                        economyPlugin.addBalance(player.getUniqueId(), price);
                        plugin.addSaleTime(player, clickedItem.getType().toString(), job);
                        ItemMeta meta = clickedItem.getItemMeta();
                        List lore = meta.getLore();
                        int broj = plugin.getRemainingSales(player, clickedItem.getType().toString(), job);
                        if (broj != 1) {
                            lore.set(1, ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " puta");
                        } else {
                            lore.set(1, ChatColor.AQUA + "Ovaj item možeš prodati još " + ChatColor.YELLOW + broj + ChatColor.AQUA + " put");
                        }
                        meta.setLore(lore);
                        clickedItem.setItemMeta(meta);
                    }
                }
            }
        }

        //////////////////////////////////// UPGRADE STUFF ///////////////////////////////////////////////////

        else if (event.getView().getTitle().equals(ChatColor.BLUE + "" + ChatColor.BOLD + "Upgrades")) {
            if (event.getClickedInventory() == topInventory) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() == Material.ENCHANTING_TABLE) {
                    player.performCommand("enchantingtable");
                    player.closeInventory();
                }
                if (event.getSlot() == 0) createHelp(player);
            }

        }
    }
}


