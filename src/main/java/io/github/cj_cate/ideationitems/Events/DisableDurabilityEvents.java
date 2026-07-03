package io.github.cj_cate.ideationitems.Events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class DisableDurabilityEvents implements Listener {

    public static final boolean durabilityIsOn = true; // True = durability is ON

    // This plugin is not built to handle when things break, because durability changes the metadata. This was an early
    // and intentional design choice. This sounds controversial, but has actually been a beloved change by every
    // person that has used it.
    private static final ArrayList<Material> durableItems = new ArrayList<>(Arrays.asList(
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

    public static boolean durabilityIsOn_and_MaterialInList(Material material) {
        return durabilityIsOn && durableItems.contains(material);
    }

    public static void setUnbreakable(ItemStack item) {
        ItemMeta m = item.getItemMeta();
        m.setUnbreakable(true);
        m.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(m);
    }

    // If someone's item does break, then it should be repaired.
    @EventHandler
    public void fixBrokenItems(PlayerItemBreakEvent e)
    {
        // check if the item that breaks isn't in the list
        if(!DisableDurabilityEvents.durabilityIsOn_and_MaterialInList(e.getBrokenItem().getType())) return;

//        e.getPlayer().sendMessage(ChatColor.RED + "Report this with a screenshot and what item broke\n" + e.getBrokenItem().getType());
        e.getBrokenItem().setAmount(e.getBrokenItem().getAmount() + 1);
        ItemMeta m = e.getBrokenItem().getItemMeta();
        m.setUnbreakable(true);
        m.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        e.getBrokenItem().setItemMeta(m);
    }

    @EventHandler
    public void craftItemsUnbreakabley(PrepareItemCraftEvent e) {
        if(e.getRecipe() == null) return;
        ItemStack result = e.getRecipe().getResult();

        if(durabilityIsOn_and_MaterialInList(result.getType())) {
            setUnbreakable(result);
            e.getInventory().setResult(result);
        }

    }
    
}
