package io.github.cj_cate.ideationitems.Events;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

//problem: minecraft has poor mechanics. solution: i fix it by turning them all offss

public class ModifyVanillaEvents implements Listener {

    // Disable elytras
//    @EventHandler
//    public void disableGliding(PlayerMoveEvent e)
//    {
//        if (e.getPlayer().isGliding()) {
//            e.getPlayer().setGliding(false);
//        }
//    }

    // Disable villagers. Their main functionality is enchanted books, which is boring.
//    @EventHandler
//    public void disableVillagers(PlayerInteractEntityEvent e) {
//        if (e.getRightClicked().getType() == EntityType.VILLAGER) {
//            e.setCancelled(true);
//            e.getPlayer().playSound(e.getPlayer(), Sound.ENTITY_VILLAGER_HURT, 0.8f, 1);
//        }
//    }

    // Keep withers to the nether.
//    @EventHandler
//    public void placeWitherSkullOutsideOfNether(BlockPlaceEvent e) {
//        if(e.getBlockPlaced().getType().equals(Material.WITHER_SKELETON_SKULL) || e.getBlockPlaced().getType().equals(Material.WITHER_SKELETON_WALL_SKULL)) {
//            if(e.getPlayer().getWorld().getBiome(e.getPlayer().getLocation()) != Biome.SOUL_SAND_VALLEY) {
//                e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "There aren't enough souls here for that...");
//                e.setCancelled(true);
//            }
//        }
//    }

    // When a spawner spawns a mob, tag it so that it can be handled later.
    @EventHandler
    public void tagSpawnerMobs(CreatureSpawnEvent e) {
        if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            TagUtil.setSpawnerMob(e.getEntity());
        }
    }



}
