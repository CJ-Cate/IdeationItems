package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReimplentationItems extends ItemClass implements Listener
{

    /**
     * This class removes the vanilla recipes for items and lets you re-implement them. To do this, add the material
     * to the list and then write a blueprint function for how it should be re-implemented, including it's recipe.
     * .
     * Constructor logic must only ever be called once, and it has to be before the methods in this class.
     * This is because if you add the custom recipes first then iterate to remove them, it removes both.
     * AFAIK this is the best way; test with caution because this took me quite a while to settle on this solution.
     */
    private final Random rand;

    // even though its probably better practice to have these declared in the constructor, I want to have this here so
    // that I can easily just pop them in when dealing with the vanilla shit. It would also be better to put it into the
    // only class where it is used but this is way more convenient
    public static ArrayList<Material> markToRemove = new ArrayList<>(List.of(
            Material.POWERED_RAIL, // If you simply want to remove an item
            Material.IRON_CHAIN
    ));

    public ReimplentationItems() {
        rand = new Random();
    }

    public Blueprint makeVanillaChain() {
        return new Blueprint(new ItemStack(Material.IRON_CHAIN, 3), "vanilla_chain", Categories.VANILLA,
            new RecipeHolder(
                RecipeType.SHAPED_RECIPE,
                new String[]{
                        " i "," T "," i "
                },
                new MaterialCarrier('i', Material.IRON_NUGGET),
                new MaterialCarrier('T', Material.IRON_INGOT)
            ));
    }


    /*
    // Without this implemented, the item is simply just simply uncraftable.
    public Blueprint makeVanillaPoweredRail() {
        return new Blueprint(new ItemStack(Material.POWERED_RAIL, 8), "vanilla_powered_rail", Categories.VANILLA,
            new RecipeHolder(
                RecipeType.SHAPED_RECIPE,
                new String[]{
                        "c c",
                        "cSc",
                        "crc"
                },
                new MaterialCarrier('c', Material.COPPER_INGOR),
                new MaterialCarrier('S', Material.STICK),
                new MaterialCarrier('r', Material.REDSTONE_DUST)
            ));
    }
     */





}
