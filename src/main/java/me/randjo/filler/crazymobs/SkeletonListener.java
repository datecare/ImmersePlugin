package me.randjo.filler.crazymobs;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.randjo.MainPlugin;
import me.randjo.Settings;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkeletonListener implements Listener {
    private final Set<Integer> invisibleArcherIds = new HashSet<>();

    private final MainPlugin plugin;

    public SkeletonListener(MainPlugin plugin) {

        this.plugin = plugin;

    }

    // Turns spawned skeleton invisible by sending a packet where mainhand is empty
    public void setupPacket(ProtocolManager protocolManager) {
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                int entityId = event.getPacket().getIntegers().read(0);
                if (invisibleArcherIds.contains(entityId)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PacketContainer equipmentPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
                            equipmentPacket.getIntegers().write(0, entityId); // Entity ID

                            List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = Collections.singletonList(
                                    new Pair<>(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.AIR))
                            );

                            equipmentPacket.getSlotStackPairLists().write(0, pairs);

                            protocolManager.sendServerPacket(event.getPlayer(), equipmentPacket);

                            // System.out.println("The protocol should be sent");
                        }
                    }.runTaskLater(plugin, 2l);

                }
            }
        });
    }

    // Skeletons have a chance to spawn invisible
    @EventHandler
    public void onSkeletonSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Skeleton skeleton) {
            if (Math.random() < Settings.SKELETON_INVISIBLE_CHANCE) {
                PotionEffect invisible = new PotionEffect(
                        PotionEffectType.INVISIBILITY,
                        100000,
                        25,
                        false,  // Ambient: false
                        false   // Particles: false (Crucial for a clean invisible effect)
                );
                skeleton.addPotionEffect(invisible);
                invisibleArcherIds.add(skeleton.getEntityId());
            }
        }
    }
}