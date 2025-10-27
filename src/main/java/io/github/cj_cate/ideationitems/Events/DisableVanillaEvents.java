package io.github.cj_cate.ideationitems.Events;

import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

//problem: minecraft has poor mechanics. solution: i fix it by turning them all offss

public class DisableVanillaEvents implements Listener {
    /**
     * This plugin changes many core things about the game. It is not intended to be mixed with various other plugins.
     */

    // This plugin is not built to handle when things break, because durability changes the metadata. This was an early
    // and intentional design choice. This should not be changed while this plugin is being used.
    // The things that are commented are things that have ignored with purpose and intent.
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

    // Anvils are used for renaming, repairing, and applying book enchants. None of those things are applicable.
    @EventHandler
    public void disableAnvils(PlayerInteractEvent e) {
        if(e.getClickedBlock() != null && e.getClickedBlock().getType().name().contains("anvil"))
        {
            e.setCancelled(true);
            e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
            e.getPlayer().sendMessage(ChatColor.AQUA + "Sorry for the inconvenience, this block is disabled.");
        }
    }

    // Regardless, if someone is in an anvil GUI they should be warned.
    @EventHandler
    public void anvilWarning(PrepareAnvilEvent e)
    {
        for(HumanEntity h : e.getViewers())
        {
            if(h instanceof Player p)
            {
                p.sendMessage(ChatColor.RED + "Warning: certain anvil functions do not work or may be reverted after use");
            }
        }
    }

    // Disable elytras. This function can be restored if you wish.
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

    // Used to handle custom crafting; The entire custom-crafting system is a work-in-progress, but leave this.
    @EventHandler
    public void disbableCraftingWithCustomItems(PrepareItemCraftEvent e) {
        // very small chance need to check for repair item recipe. i will choose to ignore this because I dont want to test it. check the docs.
        if(e.getRecipe() == null || e.getRecipe().getResult() == null) return;

        // if the result has no custom value AND there are custom things in the grid then cancel it (by setting result air)
        if(!TagUtil.hasCustomValue(e.getRecipe().getResult())) {
            for(ItemStack item : e.getInventory().getStorageContents()) {
                if(TagUtil.hasCustomValue(item)) {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            }
        }
    }

    // Stop players from placing items that are represented as blocks
    @EventHandler
    public void placeItem(BlockPlaceEvent e)
    {
        // This one doesn't need a null check, not exactly sure why
        if(e.getItemInHand().getItemMeta() != null)
        {
            if(e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getMain(), "unplaceable"), PersistentDataType.STRING))
            {
                e.setCancelled(true);
            }
        }
    }


    // TODO: This event likely needs to be changed, but what do I know is ATM it just removed all items when hotbar slot changes but like idk
    @EventHandler
    public void removeDisabledFromOffHandOnChange(PlayerItemHeldEvent e)
    {
        if(e.getPlayer().getInventory().getItemInOffHand().getItemMeta() != null && TagUtil.isDisabled(e.getPlayer().getInventory().getItemInOffHand()))
        {
            e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    // Any item tagged "disabled" should not be able to be picked up
    @EventHandler
    public void removeDisabledOnPickup_1(InventoryPickupItemEvent e) // Hoppers & Minecart-Hoppers picking up items
    {
        if(TagUtil.isDisabled(e.getItem().getItemStack())) e.setCancelled(true);
    }

    @EventHandler
    public void removeDisabledOnPickup_2(EntityPickupItemEvent e) // For players and other enteties
    {
        if(TagUtil.isDisabled(e.getItem().getItemStack())) e.setCancelled(true);
    }

    // Disabled Items Logic
    @EventHandler
    public void stopInventoryMovingDisabledItems(InventoryClickEvent e)
    {
        if(e.getCurrentItem() == null) return;
        if(TagUtil.isDisabled(e.getCurrentItem()))
        {
            e.setResult(Event.Result.DENY);
            //e.getCursor().setType(Material.AIR);
        }
    }

    @EventHandler
    public void stopDroppingDisabledItems(PlayerDropItemEvent e)
    {
        if(TagUtil.isDisabled(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
            e.getItemDrop().setItemStack(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void stopPlacingUnplaceableItems(BlockPlaceEvent e) {
        if(TagUtil.getCustomValue(e.getItemInHand(), "unplaceable").equals("true")) { e.setCancelled(true); }
    }

}
