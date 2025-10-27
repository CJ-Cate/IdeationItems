package io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs;

import io.github.cj_cate.ideationitems.ItemMaps;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.Objects;

// Material is the material or itemstack passed into the recipe as a character
public class MaterialCarrier {
    private char key = 0;
    private RecipeChoice recipeChoice = null;

    public char getKey() {
        return key;
    }

    // if the recipechoice isnt null, then return it. otherwise, return the item from the map which we need
    public RecipeChoice getRecipeChoice() {
        return Objects.requireNonNullElseGet(recipeChoice, () -> new RecipeChoice.ExactChoice(ItemMaps.getItem(itemValue)));
    }
    public String itemValue; // save this string to bring the item in later after bps load into ItemMap

    // default key,recipechoice
    public MaterialCarrier(char key, RecipeChoice recipeChoice) {
        this.key = key;
        this.recipeChoice = recipeChoice;
    }

    // for shapeless
    public MaterialCarrier(RecipeChoice recipeChoice) {
        this.recipeChoice = recipeChoice;
    }

    // for shapeless
    public MaterialCarrier(Material material) {
        this.recipeChoice = new RecipeChoice.MaterialChoice(material);
    }

    // for custom item material
    public MaterialCarrier(char key, String itemstring_recipechoice) {
        this.key = key;
        this.itemValue = itemstring_recipechoice;
    }

    // for custom item material in a smithing table, where only the item is needed
    public MaterialCarrier(String itemstring_recipechoice_smithing) {
        this.itemValue = itemstring_recipechoice_smithing;
    }

    // for custom item material
    public MaterialCarrier(char key, ItemStack item_recipechoice) {
        this.key = key;
        this.recipeChoice = new RecipeChoice.ExactChoice(item_recipechoice);
    }

    // for basic material
    public MaterialCarrier(char key, Material material) {
        this.key = key;
        this.recipeChoice = new RecipeChoice.MaterialChoice(material);
    }

    // For when need an item but no key, like with smithing or furnace
    public MaterialCarrier(ItemStack item) {
        this.recipeChoice = new RecipeChoice.ExactChoice(item);
    }

}
