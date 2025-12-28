package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.GuiUtil;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static io.github.cj_cate.ideationitems.Utils.GuiUtil.createInventory;

/**
 * The mall command serves two functions:
 *   1. Give the playerbase the ability to view all of the recipes that you have implemented
 *   2. Let admins cheat in items
 *
 *  If there is an item with no recipe, it
 */

public class MallCommand implements Listener, CommandExecutor
{
    private static final String mallName = "Mall";
//    public String getMallName() { return mallName; }
    private final int slots_per_page = 45;
    private final int page_max = (int) Math.ceil((double) ItemMaps.getBlueprints().size() / slots_per_page);
    private final ItemStack air = new ItemStack(Material.AIR);
    private final Blueprint[][] book = new Blueprint[page_max][slots_per_page];
    // It gets kinda angry if we just put air in it, this is the workaround. We reset it to air later.
    private final Blueprint bair = new Blueprint(ItemUtil.makeItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ""),
                                            "air", Categories.SECRET, null);

    // This is a little bit extra but works flawlessly
    private final HashMap<Player, Integer> page = new HashMap<>();
    private Integer getPage(Player player) {
        return page.getOrDefault(player, 0);
    }
    private void setPage(Player player, Integer page) {
        this.page.put(player, page);
    }

    public MallCommand()
    {
        // Initialize the contents of book
        // each page (i) should get up to slots_per_page (45) elements
        // every time we get to the 45th element, increment i and reset j
        int i = 0; // page
        int j = 0; // slot on page
        int index = 0;
        while(index < ItemMaps.getBlueprints().size())
        {
            book[i][j] = ItemMaps.getBlueprints().get(index);
            j++;
            if(j >= slots_per_page) {
                j = 0;
                i++;
            }
            index = slots_per_page*i + j;
        }

        // Fill the last page with bair. We only have to check the last page because every other page is full.
        for (int k = 0; k < slots_per_page; k++) {
            if(book[page_max - 1][k] == null) {
                book[page_max - 1][k] = bair;
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!command.getName().equalsIgnoreCase("mall")) return true;
        if(!(sender instanceof Player p)) return true;
        if(!p.hasPermission("ideationitems.mall")) return true;

        final Inventory mallInv = createInventory(54, "Mall");

        if(args.length != 0) {
            try {
                int new_page = Integer.valueOf(args[0]);

                if(new_page > page_max - 1) {
                    new_page = page_max - 1;
                } else if(new_page < 0) {
                    new_page = 0;
                }
                setPage(p, new_page);

            } catch (NumberFormatException e) { }
        }

        GuiUtil.addGlassMinimal(mallInv);
        GuiUtil.addArrows(mallInv);
        refreshMall(mallInv, p);
        p.openInventory(mallInv);
        return true;
    }

    // Handles page turning in the mall
    @EventHandler
    public void onMallInventoryClickPageTurn(InventoryClickEvent e)
    {
        if(!e.getView().getTitle().equalsIgnoreCase(mallName)) return;
        if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
        Player p = (Player) e.getWhoClicked();
        // turn right
        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Turn Right")) {
            if(getPage(p) != page_max - 1) {
                setPage(p, getPage(p) + 1);
            }
            refreshMall(e.getInventory(), p);
            return;
        }
        // turn left
        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Turn Left")) {
            if(getPage(p) != 0) {
                setPage(p, getPage(p) - 1);
            }
            refreshMall(e.getInventory(), p);
        }
    }

    // Handles when an actual item is clicked.
    // If left click -> see the recipe
    // If shift+left click + perms -> get the item
    // If middle click + perms -> get a stack of the item
    @EventHandler
    public void onMallInventoryClickItem(InventoryClickEvent e)
    {
        if(!e.getView().getTitle().equalsIgnoreCase(mallName)) return;
        if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        // 44 is last slot of the 5th row in a big chest, e.g. the last slot before the 'info row'
        if(e.getSlot() > 44) return;

        e.setCancelled(true);

        switch(e.getClick()) {
            case LEFT -> {
                GetRecipeCommand.openRecipeMenu((Player) e.getWhoClicked(), e.getCurrentItem());
            }
            case SHIFT_LEFT -> {
                ItemStack item = e.getCurrentItem();
                if(e.getCurrentItem().getAmount() != item.getAmount()) {
                    Main.debug("Different amounts, changing! Report this");
                    Main.debug("Commands/MallCommand/onMallInventoryClickItem");
                    item.setAmount(e.getCurrentItem().getAmount()); // could be a non-one amount
                }
                e.getWhoClicked().sendMessage("IdeationItems/Admin: Created item: " + item.getItemMeta().getDisplayName() + " x" + item.getAmount());
                e.getWhoClicked().getInventory().addItem(item);
            }
            case MIDDLE ->  {
                ItemStack item = e.getCurrentItem();
                item.setAmount(item.getMaxStackSize());
                e.getWhoClicked().sendMessage("IdeationItems/Admin: Created item: " + item.getItemMeta().getDisplayName() + " x" + item.getAmount());
                e.getWhoClicked().getInventory().addItem(item);
            }

        }

    }


    private void refreshMall(Inventory inv, Player player) {
        int player_page = getPage(player);
        for (int i = 0; i < slots_per_page; i++) {
            if(book[player_page][i].equals(bair)) {
                inv.setItem(i, air);
            } else {
                inv.setItem(i, book[player_page][i].item());
            }
        }
    }

//    @EventHandler
//    public void closecustominv(InventoryCloseEvent e)
//    {
//        //shutdown logic?
//        //page = 0;
//    }
}
