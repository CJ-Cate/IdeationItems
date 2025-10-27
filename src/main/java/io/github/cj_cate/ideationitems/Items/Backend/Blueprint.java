package io.github.cj_cate.ideationitems.Items.Backend;

import io.github.cj_cate.ideationitems.Events.InventoryRefresh;
import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffectHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Main;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

/**
 * This record is used to store an ItemStack and its corresponding Key that will be used in the
 * ItemMaps.item_map Items that are added to the map should never be overwritten, or else existing items will break
 * .
 * Item: Passed in itemstack
 * Value: the string value to the key-value pair, where the key is always (string) "custom"
 * Categories: The category of the item being passed in, found in the Categories enumerator. Used for item sorting in guis
 * RecipeHolder: The custom data class RecipeHolder, created to store all needed information needed to create a recipe in a single object
 * InteractEffect: Snippets of code that run when an event is triggered
**/

@SuppressWarnings("varargs")
public record Blueprint(ItemStack item, String value, Categories category, RecipeHolder recipe, InteractEffectHolder... consumers)
{
    @SafeVarargs
    public Blueprint
    {
        ItemMeta m = item.getItemMeta();

        m.setUnbreakable(true);
        m.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        m.setCustomModelData(Math.abs(value.hashCode())); // has to be abs because custommodeldata cannot be set to negative values

        // if the value is null, then its an item thats retaining vanilla functionality. skip the tagging and just give it something arbitrary and unique
        if(value != null) {
            m.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), "custom"), PersistentDataType.STRING, value);
        } else {
            // currently, this should never happen.
            Main.log("Problemo! A value is null");
            // if no random uuid then sometimes there is overlap between existing vanilla recipes
            // this happens if the registering items reflection code in onEnable runs before the ReimplementationItems constructor
            // to make life easy, just run this.
            value = item.getType().name() + UUID.randomUUID();
        }

        if(category != Categories.VANILLA) {
            item.setItemMeta(m);
        }


    } // constructor ends here

    public static void registerRecipe(Blueprint bloo) {
        // If recipe isn't null, then add the recipe based off of the RecipeType value stored in recipe.
        // If there are errors from generating recipes, that means that you as in me/I fucked up making one (probably)
        RecipeHolder recipe = bloo.recipe();
        if(recipe == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(Main.getMain(), bloo.value());
        ItemStack item = bloo.item();

        switch(recipe.getRecipeType()) {

            case SHAPED_RECIPE -> {

                List<String[]> allPatterns = getAllPatterns(recipe.getRecipeArray());
                for (int i = 0; i < allPatterns.size(); i++) {
                    NamespacedKey _key = new NamespacedKey(Main.getMain(), bloo.value() + "_" + i);

                    if(i == 0 && bloo.category() != Categories.SECRET) {
                        InventoryRefresh.addToEveryRecipeList(_key);
                    }

                    ShapedRecipe sr = new ShapedRecipe(_key, item);

                    String[] pattern = allPatterns.get(i);
                    sr.shape(pattern[0], pattern[1], pattern[2]);

                    for(MaterialCarrier materialCarrier : recipe.getMaterialCarrierArrayList()) {
                        try {
                            sr.setIngredient(materialCarrier.getKey(), materialCarrier.getRecipeChoice());
                        } catch (IllegalArgumentException e) {
                            Main.log("Error with: " + _key.getKey());
                            e.printStackTrace();
                        }
                    }

                    Bukkit.addRecipe(sr);
                }
            }

//            case SHAPED_RECIPE_SIZES -> {
//                // matching logic to SHAPED_RECIPE
//                ShapedRecipe sr = new ShapedRecipe(key, item);
//
//                for(MaterialCarrier materialCarrier : recipe.getMaterialCarrierArrayList())
//                {
//                    sr.setIngredient(materialCarrier.getKey(), materialCarrier.getRecipeChoice());
//                }
//
//                InteractEffectHolder prepare_event = new InteractEffect_PrepareItemCraftEvent(e -> {
//
//                    if(e.getRecipe() == null) return;
//                    if(e.getInventory().getResult() != item) return;
//
//                    ItemStack[] matrix = e.getInventory().getMatrix();
//                    for (int i = 0; i < matrix.length; i++) {
//                        if(matrix[i].getAmount() != recipe.getAmounts()[i]) {
//                            e.getInventory().setResult(new ItemStack(Material.AIR));
//                            return;
//                        }
//                    }
//                });
//                Bukkit.getPluginManager().registerEvents((Listener) prepare_event, Main.getMain());
//
//                for (String[] pattern : getAllPatterns(recipe.getRecipeArray())) {
//                    sr.shape(pattern[0], pattern[1], pattern[2]);
//                    Bukkit.addRecipe(sr);
//                }
//
//            }
            // next: inventory click event to remove the amount of items for the thing

            case SHAPELESS_RECIPE -> {
                ShapelessRecipe sr = new ShapelessRecipe(key, item);
                recipe.getMaterialCarrierArrayList().forEach(i -> sr.addIngredient(i.getRecipeChoice()) );
                Bukkit.addRecipe(sr);
            }

            case SMITHING_RECIPE -> Bukkit.addRecipe(new SmithingTransformRecipe(key, item, recipe.getTemplate(),recipe.getBase(), recipe.getAddition()));


            case STONECUTTING_RECIPE -> Bukkit.addRecipe(new StonecuttingRecipe(key, item, recipe.getSource()));
            case FURNACE_RECIPE -> Bukkit.addRecipe(     new FurnaceRecipe(     key, item, recipe.getSource(), 0, recipe.getCookingTime()));
            case BLASTING_RECIPE -> Bukkit.addRecipe(    new BlastingRecipe(    key, item, recipe.getSource(), 0, recipe.getCookingTime()));
            case SMOKING_RECIPE -> Bukkit.addRecipe(     new SmokingRecipe(     key, item, recipe.getSource(), 0, recipe.getCookingTime()));
            case CAMPFIRE_RECIPE -> Bukkit.addRecipe(    new CampfireRecipe(    key, item, recipe.getSource(), 0, recipe.getCookingTime()));

            default -> throw new IllegalArgumentException("(custom) there is an incorrect recipe type enum thingy!");
        }
    }

    private static List<String[]> getAllPatterns(String[] string_pattern) {
        List<String[]> result = new ArrayList<>();
        int len = string_pattern.length;

        // works generally, because I might be switching to custom crafting
        char[][] pattern = new char[len][len];

        // lets start by getting all (i,j) pairs and then getting the bounds of them
        List<Point> allPoints = new ArrayList<>();

        int min_i = Integer.MAX_VALUE;
        int max_i = 0;

        int min_j = Integer.MAX_VALUE;
        int max_j = 0;

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (string_pattern[i].toCharArray()[j] != ' ') {
                    allPoints.add(new Point(i,j));
                    pattern[i][j] = string_pattern[i].toCharArray()[j];

                    min_i = Math.min(min_i, i);
                    max_i = Math.max(max_i, i);

                    min_j = Math.min(min_j, j);
                    max_j = Math.max(max_j, j);
                } else {
                    pattern[i][j] = ' ';
                }
            }
        }

        Point dimensionOfShape =  new Point(max_i-min_i+1, max_j-min_j+1);

        // breakout case to skip some logic if the thing is already big enough
        if(dimensionOfShape.x == len && dimensionOfShape.y == len) {
            return Collections.singletonList(string_pattern);
        }

        // now the lowest points on (i,j) act as our reference to 'zero' the shape to the top corner
        for(Point point : allPoints) {
            point.x = point.x - min_i;
            point.y = point.y - min_j;
        }


        // pattern is a square grid so this is okay. its just the max size
        // for each spot on the total grid...
        for (int i = 0; i < len - dimensionOfShape.x + 1; i++) {
            for (int j = 0; j < len - dimensionOfShape.y + 1; j++) {
                // ...go through the known points and adjust
                char[][] addToList = new char[len][len];
                Arrays.stream(addToList).forEach(row -> Arrays.fill(row, ' '));

                for(Point d : allPoints) {
                    addToList[d.x + i][d.y + j] = pattern[d.x + min_i][d.y + min_j];
                }

                String[] stringa = new String[len];
                for (int k = 0; k < len; k++) {
                    stringa[k] = new String(addToList[k]);
                }
                result.add(stringa);
            }
        }

        return result;
    }


    /**
     * Resgister the class items and optional event(s)
     * @param clazz class which has 'public Blueprint' methods in it
     * @throws NoSuchMethodException bullshit
     * @throws InvocationTargetException annoying
     * @throws InstantiationException gross
     * @throws IllegalAccessException ew why
     */
    public static void registerClassItems(Class<?> clazz, Object instance) {
        // Get all the blueprint methods in each clazz
        for (Method method : clazz.getDeclaredMethods()) {
            //make sure that the method actually returns a Blueprint and isnt something else
            if (method.getReturnType() != Blueprint.class) continue;

            //ensure the method is public
            if (!method.canAccess(instance)) {
//                Main.debug("skipping method: " + method.getName());
                continue;
            }

            // define the blueprint for each method, then move on to the non-reflection based logic
            Blueprint bloo;
            try {
                bloo = (Blueprint) method.invoke(instance);
            } catch (InvocationTargetException e) {
                Main.log("Catching InvocationTargetException. refer to " + clazz.getSimpleName() + "/" + method.getName());
                e.printStackTrace();
                continue;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Main.log("Catching parameter exception in reflection. Refer to " + method.getName());
                continue;
            }

            // non-reflection based code used to actually put the items into the game
            try {
                // APPARENTLY do not skip this if bloo.category()==Vanilla because KILL YOURSELF NOW!!!!!!!
                ItemMaps.addToMap(bloo.value(), bloo);
            } catch(NullPointerException e ) {
                Main.log("bloo is null after instantiation. refer to " + clazz.getSimpleName() + "/" + method.getName());
                e.printStackTrace();
            }

            // register events tied to items
            for (InteractEffectHolder i : bloo.consumers()) {
                Bukkit.getPluginManager().registerEvents((Listener) i, Main.getMain());
            }
            //
            // non-reflection based code over
        }
    }


    @Override
    public String toString() {
        return "bp: " + item().getType() + "." + value();
    }

}

