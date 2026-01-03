package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeType;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NetherItems extends ItemClass
{
    /*
    This class is a good example for how you can manage the creation of an item that needs to be used throughout the
      class. Remember, ItemClass only looks for PUBLIC blueprint methods to add, so a private method will be skipped.
      This can be leveraged to create private items that you can reference throughout the class while having a
      simple "public Blueprint getItem()" method to have it be registered.

     Note: still give items unqiue values because it is used for registering the recipes i think. If you want to do
     more than one recipe for a vanilla thing to it like "vanilla_thing_1", "vanilla_thing_2", etc.
     */

    // This is an example, so that you could use the blazeDust blueprint in other places around here.
    private Blueprint blazeDust;
    public NetherItems() {
        blazeDust = makeBlazeDust();
    }
    public Blueprint getBlazeDust() { return blazeDust; }

    private Blueprint makeBlazeDust() {
        ItemStack item = ItemUtil.makeItem(Material.HONEYCOMB, ChatColor.GOLD + "Blaze Dust", new ArrayList<>(List.of("Can be smelted into blaze powder")));

        return new Blueprint(item, "blaze_dust", Categories.MATERIAL,
                new RecipeHolder(
                        RecipeType.SHAPED_RECIPE,
                        new String[]{"ses",
                                     "fos",
                                     "ssQ" },
                        new MaterialCarrier('s', Material.FLINT_AND_STEEL),
                        new MaterialCarrier('e', Material.ENDER_PEARL),
                        new MaterialCarrier('f', Material.FIRE_CHARGE),
                        new MaterialCarrier('o', Material.OBSIDIAN),
                        new MaterialCarrier('Q', Material.QUARTZ)
                )
        );
    }

    public Blueprint makeVanillaBlazePowder() {
        return new Blueprint(new ItemStack(Material.BLAZE_POWDER), "vanilla_blaze_powder", Categories.VANILLA,
                new RecipeHolder(
                        RecipeType.BLASTING_RECIPE,
                        "blaze_dust",
                        400
                ));
    }

    public Blueprint makeVanillaNetherrack() {
        ItemStack item = new ItemStack(Material.NETHERRACK);
        item.setAmount(8);

        return new Blueprint(item, "vanilla_netherrack", Categories.VANILLA,
                new RecipeHolder(RecipeType.SHAPED_RECIPE,
                        new String[]{"nnn",
                                     "nbn",
                                     "nnn"},
                        new MaterialCarrier('n', Material.STONE),
                        new MaterialCarrier('b', Material.BLAZE_POWDER)
                )
        );
    }

    public Blueprint makeVanillaBlackstone() {
        ItemStack item = new ItemStack(Material.BLACKSTONE);
        item.setAmount(8);

        return new Blueprint(item, "vanilla_blackstone", Categories.VANILLA,
                new RecipeHolder(RecipeType.SHAPED_RECIPE,
                        new String[]{"nnn",
                                     "nbn",
                                     "nnn"},
                        new MaterialCarrier('n', Material.DEEPSLATE),
                        new MaterialCarrier('b', Material.BLAZE_POWDER)
                )
        );
    }

    public Blueprint makeVanillaQuartz() {
        ItemStack item = new ItemStack(Material.QUARTZ);
        item.setAmount(2);

        return new Blueprint(item, "vanilla_quartz", Categories.VANILLA,
                new RecipeHolder(RecipeType.SHAPED_RECIPE,
                        new String[]{"vvv",
                                     "vCv",
                                     "vov"},
                        new MaterialCarrier('v', Material.POINTED_DRIPSTONE),
                        new MaterialCarrier('C', Material.CALCITE),
                        new MaterialCarrier('o', "blaze_dust")
                )
        );

    }

    /*
    quartz
    the trees
    nether wart
    blackstone
    blaze powder

    no:
    soul sand
     */
}
