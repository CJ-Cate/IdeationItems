package io.github.cj_cate.ideationitems.Events;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class DisabledItemEvents implements Listener {

    // Stop players from placing items that are represented as blocks, or anything tagged "unplaceable"
    @EventHandler
    public void placeItem(BlockPlaceEvent e)
    {
        if(TagUtil.hasCustomValue(e.getItemInHand(), TagUtil.Tag.UNPLACEABLE.getTag()))
        {
            e.setCancelled(true);
        }
    }

    // TODO: This event likely needs to be changed, but what do I know is ATM it just removed all items when hotbar slot changes but like idk
    @EventHandler
    public void removeDisabledFromOffHandOnChange(PlayerItemHeldEvent e)
    {
        if(TagUtil.isDisabled(e.getPlayer().getInventory().getItemInOffHand()))
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

}
