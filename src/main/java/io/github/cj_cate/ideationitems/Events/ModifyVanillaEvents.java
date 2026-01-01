package io.github.cj_cate.ideationitems.Events;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

//problem: minecraft has poor mechanics. solution: i fix it by turning them all offss

public class ModifyVanillaEvents implements Listener {

    // This plugin is not built to handle when things break, because durability changes the metadata. This was an early
    // and intentional design choice. This sounds controversial, but has actually been a beloved change by every
    // person that has used it.
    public static final ArrayList<Material> durableItems = new ArrayList<>(Arrays.asList(
            Material.WOODEN_SHOVEL, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_HOE, Material.WOODEN_SWORD,
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,

            Material.STONE_SHOVEL, Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_HOE, Material.STONE_SWORD,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,

            Material.IRON_SHOVEL, Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_HOE, Material.IRON_SWORD,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,

            /*Material.GOLDEN_SHOVEL, Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE,*/ Material.GOLDEN_HOE, Material.GOLDEN_SWORD,
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,

            Material.DIAMOND_SHOVEL, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE, Material.DIAMOND_SWORD,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,

            Material.NETHERITE_SHOVEL, Material.NETHERITE_AXE, Material.NETHERITE_PICKAXE, Material.NETHERITE_HOE, Material.NETHERITE_SWORD,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS,

            Material.TURTLE_HELMET,
            Material.SHIELD,
            Material.CROSSBOW,
            Material.BOW,
            Material.TRIDENT,
            Material.ELYTRA,
            Material.SHEARS,
            Material.FLINT_AND_STEEL,
//            Material.BRUSH,
            Material.FISHING_ROD

    ));

    // If someone's item does break, then it should be repaired.
    @EventHandler
    public void fixBrokenItems(PlayerItemBreakEvent e)
    {
        // check if the item that breaks isn't in the list
        if(!durableItems.contains(e.getBrokenItem().getType())) return;

//        e.getPlayer().sendMessage(ChatColor.RED + "Report this with a screenshot and what item broke\n" + e.getBrokenItem().getType());
        e.getBrokenItem().setAmount(e.getBrokenItem().getAmount() + 1);
        ItemMeta m = e.getBrokenItem().getItemMeta();
        m.setUnbreakable(true);
        m.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        e.getBrokenItem().setItemMeta(m);
    }

    @EventHandler
    public void craftItemsUnbreakabley(PrepareItemCraftEvent e) {
        ItemStack original = e.getRecipe().getResult();

        if(!durableItems.contains(original.getType())) {
            return;
        }

        ItemMeta meta = original.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        original.setItemMeta(meta);

        e.getInventory().setResult(original);
    }

    // Disable elytras
    @EventHandler
    public void disableGliding(PlayerMoveEvent e)
    {
        if (e.getPlayer().isGliding()) {
            e.getPlayer().setGliding(false);
        }
    }

    // Disable villagers. Their main functionality is enchanted books, which are not applicable.
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
