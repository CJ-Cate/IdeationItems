package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeType;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Read through this to get a high-level overview of how to make items
 */

// All item classes should extend ItemClass and live in this Items folder
public class StartHere extends ItemClass
{

    // Create a public Blueprint function. The name is irrelevant. If it is private, it will be skipped over and not
    //   appear in game.
    public Blueprint makeRebreather()
    {
        // Create an item however you see fit. I include the ItemUtil class for your leisure. There are many different
        //   ways to create and modify items with this class, so I recommend reading through it
        ItemStack item = ItemUtil.makeItem(Material.LEATHER_HELMET, ChatColor.BLUE + "Rebreather");

        // Because we are making a leather helmet we need special logic to change the colour, so we
        //   use LeatherArmorMeta instead of ItemMeta to specify the colour.
        // Note that LeatherArmorMeta is a child class of ItemMeta, so we can still set everything like normal.
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, false);
        meta.setColor(Color.AQUA);
        item.setItemMeta(meta);

        // The most convenient way to return a Blueprint is in-line at the return statement. I will explain each line.

        // (required) First, we pass in the item we made.

        // (required) The "value" is the most important part of the item. Once it is set on an item, it should never be changed.
        //   The Value is how the item is tracked by the plugin and how all of its custom features will work. If it is
        //   changed and the item is distributed there is no good way of fixing it.

        // (required) We add a Category that matches the item the best.

        // If you want your item to have a recipe, you pass in a new RecipeHolder. From here you define the RecipeType,
        //   and then whichever things the recipe requires. To see how to add different types of recipe (shapeless,
        //   cooking, smithing, etc.) then visit the RecipeHolder class. Finally, material characters are defined by
        //   MaterialCarrier's which pass in a Material or the String value from another item.

        // Custom events (InteractEffectHolder's) are defined and in-context elsewhere.

        return new Blueprint(
            item,
            "rebreather",
            Categories.ARMOR,
            new RecipeHolder(
                RecipeType.SHAPED_RECIPE,
                new String[]{
                    "ggg",
                    "ghg",
                    "ggg"
                },
                new MaterialCarrier('g', Material.SEAGRASS),
                new MaterialCarrier('h', Material.LEATHER_HELMET)
            ));
    }


}
