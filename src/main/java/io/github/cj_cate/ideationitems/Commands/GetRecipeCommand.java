package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Utils.GuiUtil;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class GetRecipeCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p) || !(command.getName().equals("getrecipe"))
                || p.getInventory().getItemInMainHand().equals(Material.AIR)) {
            return true;
        }
        // The version where the player manually calls it. In other calls, the item is passed in.
        openRecipeMenu(p, p.getInventory().getItemInMainHand());
        return true;
    }


    public static void openRecipeMenu(Player p, ItemStack item) {
        Blueprint bloo = ItemMaps.getBlueprint(TagUtil.getCustomValue(item));

        // Basic checks where the inventory check would fail and needs to be exited
        if(TagUtil.hasVanillaValue(p.getInventory().getItemInMainHand())) {
            p.sendMessage("Category vanilla item detected");
            ItemStack held_item = p.getInventory().getItemInMainHand();
            bloo = ItemMaps.getBlueprint(TagUtil.getCustomValue(held_item, TagUtil.Tag.VANILLA.getTag()));
        }

        if(bloo == null) {
            p.sendMessage("No bloo found, pure vanilla item detected, exiting");
            return;
        }

        if(bloo.recipe() == null) {
            p.sendMessage("No bloo.recipe detected, exiting");
            return;
        }

        RecipeHolder recipeHolder = bloo.recipe();
        ItemStack craftingIcon;
        int iconSlot = 23;
        int outputSlot = 25;
        Inventory inv = GuiUtil.createInventory(54, "Recipe Preview");

        // For each type of recipe, go through and change the format for the page
        switch(recipeHolder.getRecipeType()) {
            case SHAPED_RECIPE -> {
                String[] ra = recipeHolder.getRecipeArray();
                craftingIcon = new ItemStack(Material.CRAFTING_TABLE);
                addCraftingGlass(inv, Material.BROWN_STAINED_GLASS_PANE);

                ItemStack air = new ItemStack(Material.AIR);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        inv.setItem(i*9 + j + 10, air);
                    }
                }

                // make a map of char -> itemstack, which will be used to map the characters in the recipe grid to items
                HashMap<Character, ItemStack> map = new HashMap<>();
                for (MaterialCarrier mc : recipeHolder.getMaterialCarrierArrayList()) {
                    map.put(mc.getKey(), getItemFromMaterialCarrier(mc));
                }

                // Go through all the characters and get the thing they represent then put it in the grid
                char[] ca;
                for (int i = 0; i < 3; i++) {
                    ca = ra[i].toCharArray();
                    for (int j = 0; j < 3; j++) {
                        if(ca[j] != ' ') {
                            inv.setItem(i*9 + j + 10, TagUtil.tagDisabled(map.get(ca[j])));
                        }
                    }
                }
            }
            case SHAPELESS_RECIPE -> {
                craftingIcon = new ItemStack(Material.CRAFTING_TABLE);
                addCraftingGlass(inv, Material.BROWN_STAINED_GLASS_PANE);

                ItemStack air = new ItemStack(Material.AIR);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        inv.setItem(i*9 + j + 10, air);
                    }
                }

                ItemStack infosign = ItemUtil.makeItem(Material.OAK_SIGN, net.md_5.bungee.api.ChatColor.YELLOW + "Info",
                        List.of("This recipe is shapeless :3"));
                TagUtil.tagDisabled(infosign);

                inv.setItem(14, infosign);

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {

                        if(i*3 + j > recipeHolder.getMaterialCarrierArrayList().size() - 1) {
                            break;
                        }

                        ItemStack viewitem = getItemFromMaterialCarrier(recipeHolder.getMaterialCarrierArrayList().get(i*3 + j));
                        inv.setItem(i*9 + j + 10, TagUtil.tagDisabled(viewitem));
                    }
                }
            }
            // The following is not necessarily "clean" but its not strictly an abuse of code either.
            // It could be simplified, but in general I am careful to keep the codebase in check.
            case FURNACE_RECIPE -> {
                addCraftingGlass(inv, Material.BLACK_STAINED_GLASS_PANE);
                craftingIcon = ItemUtil.makeItem(Material.FURNACE, "Furnace",
                        List.of("Cooking time: " + recipeHolder.getCookingTime() / 20 + "s"));
                iconSlot -= 1;
                outputSlot -= 1;
                inv.setItem(20, TagUtil.tagDisabled(getItemFromMaterialCarrier(recipeHolder.getSource())));
            }
            case BLASTING_RECIPE -> {
                addCraftingGlass(inv, Material.BLACK_STAINED_GLASS_PANE);
                craftingIcon = ItemUtil.makeItem(Material.BLAST_FURNACE, "Blast Furnace",
                        List.of("Cooking time: " + recipeHolder.getCookingTime() / 20 + "s"));
                iconSlot -= 1;
                outputSlot -= 1;
                inv.setItem(20, TagUtil.tagDisabled(getItemFromMaterialCarrier(recipeHolder.getSource())));
            }
            case SMOKING_RECIPE -> {
                addCraftingGlass(inv, Material.BLACK_STAINED_GLASS_PANE);
                craftingIcon = ItemUtil.makeItem(Material.SMOKER, "Smoker",
                        List.of("Cooking time: " + recipeHolder.getCookingTime() / 20 + "s"));
                iconSlot -= 1;
                outputSlot -= 1;
                inv.setItem(20, TagUtil.tagDisabled(getItemFromMaterialCarrier(recipeHolder.getSource())));
            }
            case CAMPFIRE_RECIPE -> {
                addCraftingGlass(inv, Material.BLACK_STAINED_GLASS_PANE);
                craftingIcon = ItemUtil.makeItem(Material.CAMPFIRE, "Campfire",
                        List.of("Cooking time: " + recipeHolder.getCookingTime() / 20 + "s"));
                iconSlot -= 1;
                outputSlot -= 1;
                inv.setItem(20, TagUtil.tagDisabled(getItemFromMaterialCarrier(recipeHolder.getSource())));
            }

            default -> {
                p.sendMessage("This recipe is not implemented for viewing.");
                return;
//                craftingIcon = new ItemStack(Material.BARRIER);
            }

        }

        inv.setItem(outputSlot, TagUtil.tagDisabled(item, "", ChatColor.RED + "Preview item"));
        inv.setItem(iconSlot, TagUtil.tagDisabled(craftingIcon, false));

        // back arrow on bottom left
        ItemStack backArrow = ItemUtil.makeItem(Material.ARROW, "Back");
        inv.setItem(45, TagUtil.tagDisabled(backArrow));

        p.openInventory(inv);
    }

    private static void addCraftingGlass(Inventory inv, Material material) {
        ItemStack matty = TagUtil.tagDisabled(ItemUtil.makeItem(material, " "), false);
        ItemStack glass2 = TagUtil.tagDisabled(ItemUtil.makeItem(Material.LIME_STAINED_GLASS_PANE, " "), false);

        for (int i = 0; i < 45; i++) {
            inv.setItem(i, matty);
        }
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, glass2);
        }
    }

    private static ItemStack getItemFromMaterialCarrier(MaterialCarrier mc) {
        if(TagUtil.hasCustomValue(mc.getRecipeChoice().getItemStack())) {
            return ItemMaps.getBlueprint(TagUtil.getCustomValue(mc.getRecipeChoice().getItemStack())).item();
        } else {
            return mc.getRecipeChoice().getItemStack();
        }
    }

    @EventHandler
    public void onClickBackArrow(InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase("Recipe Preview")) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Back")) {
            Player p = (Player) e.getWhoClicked();
            p.performCommand("mall");
        }
    }

}
