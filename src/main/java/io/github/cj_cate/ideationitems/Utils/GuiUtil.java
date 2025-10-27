package io.github.cj_cate.ideationitems.Utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class GuiUtil
{

    public static final ItemStack turnRightItem = makePageArrow(Color.TEAL, "Turn Right");
    public static final ItemStack turnLeftItem = makePageArrow(Color.YELLOW, "Turn Left");
    private static final ItemStack glass = TagUtil.tagDisabled(ItemUtil.makeItem(Material.LIME_STAINED_GLASS_PANE, " "), false);

//    private static final ItemStack pane = ItemUtil.makeItem(Material.CYAN_STAINED_GLASS_PANE, "");
//    private static final ItemStack deleter = ItemUtil.makeItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "DELETE ITEM");
//
//    public static ItemStack getPane() { return pane; }
//    public static ItemStack getNextPage() { return nextPage; }
//    public static ItemStack getPreviousPage() { return previousPage; }
//    public static ItemStack getDeleter() { return deleter; }


//    private static String mallCategoriesName = MallCommand.mallCategoriesName;
    public static Inventory createInventory(String title)
    {
        return Bukkit.createInventory(null, 54, title);
    }
    public static Inventory createInventory(int size_divisible_by_9, String title)
    {
        // If the size isnt divisible by 9 then EXPLODE ! ! ! ! ! ! ! !
        if(size_divisible_by_9 % 9 != 0) throw new IllegalArgumentException();
        return Bukkit.createInventory(null, size_divisible_by_9, title);
    }

    // domain: [0,27] ∈ Z
    public static int borderedIndex27(int i_0_27) {
        return 10 + i_0_27 + 2*(i_0_27 / 7);
    }

    // Kinda scuffed, because the 45 bordered index only has a bat on the bottom
//    public static int borderedIndex45(int i_0_45) {
//        return i_0_45 > 45 ? null : i_0_45;
//    }

    public static void addGlass(Inventory inv) {
        ItemStack glass = TagUtil.tagDisabled(ItemUtil.makeItem(Material.LIME_STAINED_GLASS_PANE, " "), false);

        // top and bottom rows
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glass);
            inv.setItem(i + 45, glass);
        }
        // sides
        for (int i = 1; i < 5; i++) {
            inv.setItem(i*9, glass);
            inv.setItem(i*9 + 8, glass);
        }

    }

    public static void addGlassMinimal(Inventory inv) {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i + 45, glass);
        }
    }

    public static void addArrows(Inventory inv) {
        inv.setItem(inv.getSize()-2, turnLeftItem);
        inv.setItem(inv.getSize()-1, turnRightItem);
    }

    private static ItemStack makePageArrow(Color color, String name) {
        ItemStack result = TagUtil.tagDisabled(
                            ItemUtil.makeItem(Material.TIPPED_ARROW, "name"), false);
        // name has to be re-set due to the nature of my sanity

        PotionMeta meta = (PotionMeta) result.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(ChatColor.GOLD + name);
        meta.setHideTooltip(false);

        result.setItemMeta(meta);

        return result;
    }
//    public static Inventory makeDefaultLayout()
//    {
//        Inventory inv = createInventory();
//        inv.setContents(getFillDefaultLayout(inv));
//
//        // Maybe logic to make sure inventory isnt null or empty idk
//        return inv;
//    }
//    public static Inventory makeDefaultLayout(ArrayList<ItemStack> inputList)
//    {
//        Inventory inv = createInventory();
//        inv.setContents(getFillDefaultLayout(inv));
//
//
//
//        // Maybe logic to make sure inventory isnt null or empty idk
//        return inv;
//    }
//
//    public static Inventory makeDefaultMallLayout()
//    {
//
//        Inventory inv = createInventory();
//        inv.setContents(getFillDefaultLayout(inv));
//
//        ArrayList<ItemStack> categoryItems = new ArrayList<>();
//        for(Categories c : Categories.values())
//        {
//            categoryItems.add(ItemUtil.makeItem(Material.CHICKEN_SPAWN_EGG, c.toString()));
//        }
//
//        int loopmax = Categories.values().length;
//        for (int i = 0; i < loopmax; i++) {
//            if(inv.getItem(i).getType() == Material.AIR)
//            {
//                // This section is inherently flawed, and needs to be changed.
//                // It needs to be changed to a function where every 27 items, it loops to a new page
//                // This can be done by taking the collection of categories and putting it into individual
//                // arraylists filled up in a fori loop that switches to a new arraylist every 27 entries
//                try {
//                    inv.setItem(i + 10, categoryItems.get(i));
//                } catch(ArrayIndexOutOfBoundsException e) { System.out.println("Too many Categories"); }
//
//            } else loopmax ++;
//        }
//
//        // Maybe logic to make sure inventory isnt null or empty idk probably not needed
//        return inv;
//    }
//
//    public static ItemStack[] getFillDefaultLayout(Inventory inv)
//    {
//        if(inv.getSize() <= 18 || inv.getSize() % 9 != 0) throw new IllegalStateException("Inventory size is wrong");
//        ItemStack[] itemStacks = new ItemStack[inv.getSize()];
//
////        for(ItemStack i : itemStacks) i.setType(Material.AIR);
//
//        // Set the top and bottom edges to PANE
//        for (int i = 1; i < 7; i++) {
//            itemStacks[i] = getPane();
//        }
//        for (int i = inv.getSize() - 8; i < inv.getSize() - 2; i++) {
//            itemStacks[i] = getPane();
//        }
//
//        // Set the left and right EDGES (& corners) to PANE
//        int position = 0;
//        while(position >= inv.getSize() /*true*/)
//        {
//            try {
//                itemStacks[position] = getPane();
//                itemStacks[position + 8] = pane;
//            } catch(Exception e) {
//                break;
//            }
//            position += 9;
//        }
//
//        // Set some of the special items that are needed
//        itemStacks[8] = getDeleter();
//        itemStacks[inv.getSize() - 1] = getNextPage();
//        itemStacks[inv.getSize() - 9] = getPreviousPage();
//
//        return itemStacks;
//    }



}
