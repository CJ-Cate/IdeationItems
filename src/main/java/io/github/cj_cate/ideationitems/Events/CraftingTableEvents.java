package io.github.cj_cate.ideationitems.Events;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.cj_cate.ideationitems.Events.ModifyVanillaEvents.durableItems;

/**
 * Events related to the custom crafting system. Modify with caution, or probably not at all.
 */
public class CraftingTableEvents implements Listener {

    // This stops you crafting vanilla items with custom items. You can still craft Categories.VANILLA items because
    //   those items are not fully vanilla until they are brought into the game
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

    // When a recipe is crafted in the crafting table (or similar) if it is a VANILLA category blueprint item, then
    //   when it is clicked out of the thing overwrite it with a true vanilla item
    @EventHandler
    public void changeCustomVanillaCraftingOutput(PrepareItemCraftEvent e) {
        ItemStack original = e.getRecipe().getResult();

        if(!TagUtil.hasVanillaCategory(original)) {
            return;
        }

        ItemStack modified = new ItemStack(original.getType());
        modified.setAmount(original.getAmount());

        e.getInventory().setResult(modified);
    }

    // needed for crafting vanilla stuffs
    @EventHandler
    public void playerCraftBreakableItem(CraftItemEvent e) {
        if(durableItems.contains(e.getCurrentItem().getType())) {
            ItemMeta m = e.getCurrentItem().getItemMeta();
            m.setUnbreakable(true);
            m.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            e.getCurrentItem().setItemMeta(m);
        }
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

}
