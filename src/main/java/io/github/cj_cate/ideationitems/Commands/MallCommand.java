package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
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

import static io.github.cj_cate.ideationitems.Utils.GuiUtil.createInventory;

public class MallCommand implements Listener, CommandExecutor
{
    /**
     * This command was the first big GUI implemented. It is used to cheat in items in an interactive way.
     */
    private static final String mallName = "Mall";
//    public String getMallName() { return mallName; }
    private final int slots_per_page = 45;
    private int page = 0;
    private final int page_max = (int) Math.ceil((double) ItemMaps.getBlueprints().size() / slots_per_page);
    private final ItemStack air = new ItemStack(Material.AIR);
    private final Blueprint[][] book = new Blueprint[page_max][slots_per_page];
    private final Blueprint bair = new Blueprint(ItemUtil.makeItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ""), "air", Categories.SECRET, null);


    public MallCommand()
    {
        // Initialise the contents of book
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

        GuiUtil.addGlassMinimal(mallInv);
        GuiUtil.addArrows(mallInv);
        refreshMall(mallInv);
        p.openInventory(mallInv);
        return true;
    }

    @EventHandler
    public void onMallInventoryClickPageTurn(InventoryClickEvent e)
    {
        //TODO: put a check for handle here
        if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
        // turn right
        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Turn Right")) {
            page += (page == page_max - 1) ? 0 : 1;
            refreshMall(e.getInventory());
            return;
        }
        // turn left
        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Turn Left")) {
            page += (page == 0) ? 0 : -1;
            refreshMall(e.getInventory());
        }
    }

    private void refreshMall(Inventory inv) {
        for (int i = 0; i < slots_per_page; i++) {
            inv.setItem(i, book[page][i].item());
        }
    }

    @EventHandler
    public void closecustominv(InventoryCloseEvent e)
    {
        //shutdown logic?
        //page = 0;
    }
}
