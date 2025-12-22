package io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs;

import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The RecipeHolder is a piece of the blueprint that holds all the recipe information for an item.
 * Because there are many different types of recipes, most of which with non-overlapping elements, we set a RecipeType
 *   and only reference the pieces used by that specific RecipeType.
 *
 * I have made the intentional design choice to separate default furnace/smoker/blast-furnace recipes, so that recipes
 *   added to a smoker are not automatically furnace cookable. This lets you add recipes for food that needs to be
 *   cooked in a "food only environment." It's sortof a gimmick, but it really encourages fun in-game behavior of
 *   having cooking areas and smelting areas.
 */
public class RecipeHolder
{
    // Enum for recipe type. Used for the switch in the blueprint to implement the proper recipe.
    private RecipeType recipeType;

    // The base item for when it's needed. For example, when you cook a steak the raw beef is the 'source'.
    private MaterialCarrier source;

    // For shaped recipes (and some shapeless)
    private String[] recipeArray;
    private ArrayList<MaterialCarrier> materialCarrierArrayList = new ArrayList<>();
    // ...and for sizes
    private int[] amounts = new int[9];

    // For any of the "cooking" recipes
    private int cookingTime;

    // For the smithing table recipes
    private MaterialCarrier base;
    private MaterialCarrier addition;
    private MaterialCarrier template;


    // Shaped Recipes
    public RecipeHolder(RecipeType recipeType_shapedRecipe, String[] recipeArray, MaterialCarrier... materialCarriers) {
        this.recipeType = recipeType_shapedRecipe;
        this.recipeArray = recipeArray;

        materialCarrierArrayList.addAll(Arrays.asList(materialCarriers));
    }

    // Shaped Recipes (with amounts)
    public RecipeHolder(RecipeType recipeType_shapedRecipeSize, String[] recipeArray, int[] amounts, MaterialCarrier... materialCarriers) {
        this.recipeType = recipeType_shapedRecipeSize;
        this.recipeArray = recipeArray;
        this.amounts = amounts;

        materialCarrierArrayList.addAll(Arrays.asList(materialCarriers));
    }

    // Smithing recipes (apparently broken, fault of spigot. Will test.)
    public RecipeHolder(RecipeType recipeType_smithingRecipe, MaterialCarrier template, MaterialCarrier base, MaterialCarrier addition) {
        this.recipeType = recipeType_smithingRecipe;
        this.base = base;
        this.addition = addition;
        this.template = template;
    }

    // Cooking recipes (assume xp = 0)
    public RecipeHolder(RecipeType recipeType_furnaceRecipe_xp0, Material source, int cookingTime) {
        this.recipeType = recipeType_furnaceRecipe_xp0;
        this.source = new MaterialCarrier(source);
        this.cookingTime = cookingTime;
    }

    public RecipeHolder(RecipeType recipeType_furnaceRecipe_xp0, String source, int cookingTime) {
        this.recipeType = recipeType_furnaceRecipe_xp0;
        this.source = new MaterialCarrier(source);
        this.cookingTime = cookingTime;
    }

    /**
     * Shapeless recipes do not use the characters passed in. It is simply ignored.
     * @param recipeType_shaplessRecipe
     * @param materialCarriers
     */
    public RecipeHolder(RecipeType recipeType_shaplessRecipe, MaterialCarrier... materialCarriers) {
        this.recipeType = recipeType_shaplessRecipe;
        materialCarrierArrayList.addAll(Arrays.asList(materialCarriers));
    }

    public ArrayList<MaterialCarrier> getMaterialCarrierArrayList() {
        return materialCarrierArrayList;
    }
    public RecipeType getRecipeType() {
        return recipeType;
    }
    public MaterialCarrier getSource() {
        return this.source;
    }
    public String[] getRecipeArray() {
        return recipeArray;
    }
    public int getCookingTime() {
        return cookingTime;
    }
    public RecipeChoice getBase() {
        return base.getRecipeChoice();
    }
    public RecipeChoice getAddition() {
        return addition.getRecipeChoice();
    }
    public RecipeChoice getTemplate() {
        return template.getRecipeChoice();
    }
    public int[] getAmounts() {
        return amounts;
    }

}

