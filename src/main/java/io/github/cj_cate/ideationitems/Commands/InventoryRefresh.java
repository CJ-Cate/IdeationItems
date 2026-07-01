package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.Events.DisableDurabilityEvents;
import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
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
        ArrayList<String> items_updated_array = new ArrayList<>();
        ArrayList<String> items_old_array = new ArrayList<>();
        for(ItemStack item : p.getInventory().getContents() )
        {
            if(item == null) continue;

            // Check to see if the item in question has a custom value and that it is not tagged disabled
            if(TagUtil.hasCustomValue(item) && !TagUtil.isDisabled(item))
            {
                // So now we can check to see if the item we have stored internally is the same as the one they have
                Blueprint bloo = ItemMaps.getBlueprint(TagUtil.getCustomValue(item));
                if(bloo == null) {
                    Main.log("IdeationItems/InventoryRefresh: Item '" + TagUtil.getCustomValue(item) + "' not found in your version of the plugin on player " + p.getName() + ". Skipping.");
                    continue;
                }
                ItemStack updated_item = bloo.item();
                if(!(updated_item.isSimilar(item))) // We use .isSimilar() instead of .equals() to account for stack size
                {
                    // Now we have confirmed that the item is a custom item but not the same as the one that we have stored,
                    //  therefore we can update it with these methods

                    ItemMeta new_meta;
                    if(DisableDurabilityEvents.durabilityIsOn == false ||
                            !(updated_item instanceof Damageable) ||
                            !(item instanceof Damageable)) {
                        new_meta = updated_item.getItemMeta();
                    } else {
                        // only accessible if both items are damageable and durability is toggled on
                        Damageable damageable_updated_item_meta = (Damageable) updated_item.getItemMeta();
                        damageable_updated_item_meta.setDamage(((Damageable) item.getItemMeta()).getDamage());
                        new_meta = damageable_updated_item_meta;
                    }

                    // Carry over any registered per-instance state (e.g. a dyed color) from the player's
                    // old item onto the freshly-templated meta, before we overwrite everything else.
                    if(bloo.instanceData() != null) {
                        bloo.instanceData().carryOver(item.getItemMeta(), new_meta);
                    }

                    // The carry-over above may have already accounted for the whole difference (e.g. a dyed
                    // color surviving the refresh) - only report and apply the change if the item the player
                    // would actually end up with still differs from what they have.
                    ItemStack candidate = updated_item.clone();
                    candidate.setItemMeta(new_meta);
                    if(candidate.isSimilar(item)) continue;

                    // For loggin to the player
                    items_updated_array.add(updated_item.getItemMeta().getDisplayName());
                    items_old_array.add(item.getItemMeta().getDisplayName());

                    item.setType(updated_item.getType());
                    item.setItemMeta(new_meta);
                }
            }
        }

        if(!items_updated_array.isEmpty())
        {
            p.sendMessage(ChatColor.AQUA + "IdeationItems: These items have been updated:");
            for (int i = 0; i < items_updated_array.size(); i++) {
                p.sendMessage(ChatColor.AQUA + "$ " + items_old_array.get(i) + " -> " + items_updated_array.get(i));
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
        if(DisableDurabilityEvents.durabilityIsOn == false) {
            return;
        }

        for(ItemStack i : p.getInventory().getContents())
        {
            if(i == null) continue;

            if(i.getItemMeta() instanceof Damageable d && DisableDurabilityEvents.durabilityIsOn_and_MaterialInList(i.getType()))
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
