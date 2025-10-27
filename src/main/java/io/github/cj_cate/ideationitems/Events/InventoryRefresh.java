package io.github.cj_cate.ideationitems.Events;

import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.ReimplentationItems;
import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Class used to refresh the players inventory with the server-side version
public class InventoryRefresh implements CommandExecutor, Listener
{

    private static final List<NamespacedKey> everyRecipeKeyList = new ArrayList<>();
    public static void addToEveryRecipeList(NamespacedKey key) {
        everyRecipeKeyList.add(key);
    }


    public InventoryRefresh() {

        Iterator<Recipe> recipeIterator = Main.getMain().getServer().recipeIterator();
        Recipe recipe;
        if (recipeIterator.hasNext()) {
            do {
                recipe = recipeIterator.next();
                if (recipe != null && ReimplentationItems.markToRemove.contains(recipe.getResult().getType())) {
                    recipeIterator.remove();
                } else if (!TagUtil.hasCustomValue(recipe.getResult())) {
                    addToEveryRecipeList(((Keyed) recipe).getKey());
                }
            } while (recipeIterator.hasNext());
        }
    }

    private void refreshInventory(Player p) {
        ArrayList<ItemStack> items_updated_array = new ArrayList<>();
        for(ItemStack item : p.getInventory().getContents() )
        {
            if(item == null) continue;

            // Check to see if the item in question has a custom value and that it is not disabled
            if(TagUtil.hasCustomValue(item) && !TagUtil.isDisabled(item))
            {
                // So now we can check to see if the item we have stored internally is the same as the one they have
                ItemStack updated_item = ItemMaps.getBlueprint(TagUtil.getCustomValue(item)).item();
                if(!(updated_item.isSimilar(item))) // We use .isSimilar() instead of .equals() to account for stack size
                {
                    // Now we have confirmed that the item is a custom item but not the same as the one that we have stored,
                    //  therefore we can update it with these methods
                    item.setItemMeta(updated_item.getItemMeta());
                    item.setType(updated_item.getType());
//                    item.setData(updated_item.getData()); // this line caused an error

                    // Note to future self: If there are issues, then you may have to manually refresh the players inventory here
                    items_updated_array.add(item);
                }
            }
        }

        if(!items_updated_array.isEmpty())
        {
            p.sendMessage(ChatColor.AQUA + "These items have been updated:");
            for(ItemStack i : items_updated_array)
            {
                p.sendMessage(ChatColor.AQUA + "- " + i.getItemMeta().getDisplayName());
            }
        } else p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "No items updated; your inventory is funky fresh!");

    }

    private void addRecipes(Player p) {

        int n = p.discoverRecipes(everyRecipeKeyList);

        if(n != 0) {
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + n + " recipes newly unlocked, check your recipe book");
        } else {
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "No new recipes discovered; your knowledge is funky fresh!");
        }
    }

    private void repairDurability(Player p) {
        // this could be merged with refreshInventory(), but I am writing it separate for readability
        for(ItemStack i : p.getInventory().getContents())
        {
            if(i == null) continue;

            if(i.getItemMeta() instanceof Damageable d && DisableVanillaEvents.durableItems.contains(i.getType()))
            {
                if(d.getDamage() != i.getType().getMaxDurability())
                {
                    d.setDamage(0);
                    ItemMeta meta = i.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    i.setItemMeta(meta);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p) || !(command.getName().equalsIgnoreCase("invr"))) { return true; }
        refreshInventory(p);
        addRecipes(p);
        repairDurability(p);
        return true;
    }

    @EventHandler
    public void onJoinRefresh(PlayerJoinEvent e)
    {
        refreshInventory(e.getPlayer());
        addRecipes(e.getPlayer());
        repairDurability(e.getPlayer());
    }
}
