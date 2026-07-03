package io.github.cj_cate.ideationitems.Items.Backend;

import io.github.cj_cate.ideationitems.Commands.InventoryRefresh;
import io.github.cj_cate.ideationitems.Events.DisableDurabilityEvents;
import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.InstanceData.InstanceData;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffectHolder;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_CraftItemEvent;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_PrepareItemCraftEvent;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * This class is used to store an ItemStack and its corresponding Key that will be used in the
 * ItemMaps.item_map Items that are added to the map should never be overwritten, or else existing items will break
 * .
 * Item: Passed in itemstack
 * Value: the string value to the key-value pair, where the key is always (string) "custom"
 * Categories: The category of the item being passed in, found in the Categories enumerator. Used for item sorting in guis
 * RecipeHolder: The custom data class RecipeHolder, created to store all needed information needed to create a recipe in a single object
 * InteractEffect: Snippets of code that run when an event is triggered
**/

@SuppressWarnings("varargs")
public class Blueprint
{
    private final ItemStack item;
    public ItemStack item() { return item; }

    private final String value;
    public String value() { return value; }

    private final Categories category;
    public Categories category() { return category; }

    private final RecipeHolder recipe;
    public RecipeHolder recipe() { return recipe; }

    private final InteractEffectHolder[] consumers;
    public InteractEffectHolder[] consumers() { return consumers; }

    private final InstanceData instanceData;
    public InstanceData instanceData() { return instanceData; }

    public Blueprint(ItemStack item, String value, Categories category, RecipeHolder recipe, InstanceData instanceData, InteractEffectHolder... consumers) {

        this.item = item;
        this.value = value;
        this.category = category;
        this.recipe = recipe;
        this.instanceData = instanceData;
        this.consumers = consumers;


        ItemMeta m = item.getItemMeta();

        if(DisableDurabilityEvents.durabilityIsOn_and_MaterialInList(item.getType())){
            DisableDurabilityEvents.setUnbreakable(item);
        }

        // 1.21.5+ way to set custom texture packs
        CustomModelDataComponent component = m.getCustomModelDataComponent();
        component.setStrings(List.of(value));
        m.setCustomModelDataComponent(component); // has to be abs because custommodeldata cannot be set to negative values

        m.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), TagUtil.Tag.CUSTOM.getTag()), PersistentDataType.STRING, value);

        if(instanceData != null) {
            instanceData.applyDefaults(m);
        }

        item.setItemMeta(m);

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

            // Vanilla shaped recipes only ever check for presence (1 item) per slot, and only ever consume 1
            // per slot per craft. amounts[i] (indexed by grid position, row-major 0..8) lets a slot require and
            // consume more than 1 - nothing about that is natively supported, so it needs two event hooks on
            // top of the plain recipe registration below. Unlike SHAPED_RECIPE this doesn't auto-generate every
            // shifted position, since amounts is pinned to one specific placement in the grid.
            case SHAPED_RECIPE_SIZES -> {
                String[] pattern = recipe.getRecipeArray();
                ShapedRecipe sr = new ShapedRecipe(key, item);
                sr.shape(pattern[0], pattern[1], pattern[2]);

                for(MaterialCarrier materialCarrier : recipe.getMaterialCarrierArrayList()) {
                    try {
                        sr.setIngredient(materialCarrier.getKey(), materialCarrier.getRecipeChoice());
                    } catch (IllegalArgumentException e) {
                        Main.log("Error with: " + key.getKey());
                        e.printStackTrace();
                    }
                }

                if(bloo.category() != Categories.SECRET) {
                    InventoryRefresh.addToEveryRecipeList(key);
                }
                Bukkit.addRecipe(sr);

                int[] amounts = recipe.getAmounts();

                // Hide the result in the preview until every slot has enough stock to actually pay for the craft.
                Bukkit.getPluginManager().registerEvents((Listener) new InteractEffect_PrepareItemCraftEvent(e -> {
                    if(e.getRecipe() == null || !TagUtil.hasCustomValueOf(e.getRecipe().getResult(), bloo.value())) return;

                    ItemStack[] matrix = e.getInventory().getMatrix();
                    for (int i = 0; i < matrix.length; i++) {
                        if(matrix[i] != null && matrix[i].getAmount() < amounts[i]) {
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                            return;
                        }
                    }
                }), Main.getMain());

                registerExtraAmountConsumption(bloo, (i, slotItem) -> amounts[i]);
            }

            case SHAPELESS_RECIPE -> {
                ShapelessRecipe sr = new ShapelessRecipe(key, item);
                recipe.getMaterialCarrierArrayList().forEach(i -> sr.addIngredient(i.getRecipeChoice()) );
                Bukkit.addRecipe(sr);
            }

            // Vanilla shapeless matching only ever needs 1 of each listed ingredient, and satisfies a repeated
            // ingredient by spreading it across separate grid slots - it can't express "one slot needs 2 of the
            // same item stacked together", which is what a player naturally expects "requires 2 X" to mean. So
            // this registers a plain 1-of-each shapeless recipe and, like SHAPED_RECIPE_SIZES, layers the same
            // per-slot amount gate/consumption on top, matching a slot back to its ingredient via RecipeChoice#test.
            case SHAPELESS_RECIPE_SIZES -> {
                ShapelessRecipe sr = new ShapelessRecipe(key, item);
                recipe.getMaterialCarrierArrayList().forEach(mc -> sr.addIngredient(mc.getRecipeChoice()));
                Bukkit.addRecipe(sr);

                Bukkit.getPluginManager().registerEvents((Listener) new InteractEffect_PrepareItemCraftEvent(e -> {
                    if(e.getRecipe() == null || !TagUtil.hasCustomValueOf(e.getRecipe().getResult(), bloo.value())) return;

                    for (ItemStack slot : e.getInventory().getMatrix()) {
                        if(slot == null) continue;
                        Integer required = requiredAmountForSlot(recipe, slot);
                        if(required != null && slot.getAmount() < required) {
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                            return;
                        }
                    }
                }), Main.getMain());

                registerExtraAmountConsumption(bloo, (i, slotItem) -> requiredAmountForSlot(recipe, slotItem));
            }

            case SMITHING_RECIPE -> Bukkit.addRecipe(new SmithingTransformRecipe(key, item, recipe.getTemplate(),recipe.getBase(), recipe.getAddition()));


            case STONECUTTING_RECIPE -> Bukkit.addRecipe(new StonecuttingRecipe(key, item, recipe.getSource().getRecipeChoice()));
            case FURNACE_RECIPE -> Bukkit.addRecipe(     new FurnaceRecipe(     key, item, recipe.getSource().getRecipeChoice(), 0, recipe.getCookingTime()));
            case BLASTING_RECIPE -> Bukkit.addRecipe(    new BlastingRecipe(    key, item, recipe.getSource().getRecipeChoice(), 0, recipe.getCookingTime()));
            case SMOKING_RECIPE -> Bukkit.addRecipe(     new SmokingRecipe(     key, item, recipe.getSource().getRecipeChoice(), 0, recipe.getCookingTime()));
            case CAMPFIRE_RECIPE -> Bukkit.addRecipe(    new CampfireRecipe(    key, item, recipe.getSource().getRecipeChoice(), 0, recipe.getCookingTime()));

            default -> throw new IllegalArgumentException("(custom) there is an incorrect recipe type enum thingy!");
        }
    }

    // Maps a crafting-grid slot back to the MaterialCarrier (and thus required amount) it satisfies -
    // shapeless recipes have no positional chars to index amounts by, unlike shaped ones.
    private static Integer requiredAmountForSlot(RecipeHolder recipe, ItemStack slotItem) {
        List<MaterialCarrier> carriers = recipe.getMaterialCarrierArrayList();
        int[] amounts = recipe.getAmounts();
        for (int i = 0; i < carriers.size(); i++) {
            if (carriers.get(i).getRecipeChoice().test(slotItem)) {
                return amounts[i];
            }
        }
        return null;
    }

    // Consumes the "extra" (beyond vanilla's own 1-per-slot) amount a *_SIZES recipe requires. requiredAmountFor
    // maps (matrix slot index, that slot's item) to the total amount required there; null or <=1 skips it.
    private static void registerExtraAmountConsumption(Blueprint bloo, BiFunction<Integer, ItemStack, Integer> requiredAmountFor) {
        Bukkit.getPluginManager().registerEvents((Listener) new InteractEffect_CraftItemEvent(e -> {
            if(e.getRecipe() == null || !TagUtil.hasCustomValueOf(e.getRecipe().getResult(), bloo.value())) return;

            // getRecipe() still reflects the type-matched recipe even when our PrepareItemCraftEvent gate
            // blanked the result to AIR for insufficient amounts - the actually-clicked item is what tells
            // us whether a craft really happened, so bail out here instead of consuming anything.
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            if(e.isShiftClick()) {
                // Vanilla's own shift-click batch-craft only knows how to consume 1 of each ingredient per
                // repeat, so it'd happily over-craft past what a >1 amount requirement should allow. Cancel
                // it and redo the whole batch ourselves instead of trying to correct it after the fact.
                e.setCancelled(true);

                CraftingInventory shiftCraftingInventory = e.getInventory();
                HumanEntity shiftWho = e.getWhoClicked();
                ItemStack resultTemplate = e.getCurrentItem().clone();

                // Deferred a tick, same reason as the non-shift path below: cancelling the event and then
                // mutating the same inventories synchronously within that event's handling doesn't reliably
                // stick (the client/server resync that follows a cancelled click can clobber it) - the item
                // would disappear instead of landing in the player's inventory.
                Bukkit.getScheduler().runTask(Main.getMain(), () ->
                    craftShiftClickBatch(shiftCraftingInventory, shiftWho, resultTemplate, requiredAmountFor));
                return;
            }

            // Deferred a tick so this runs after vanilla's own "take 1 per slot" has already happened -
            // doing this synchronously inside the event races with that and consumes the wrong amount.
            CraftingInventory craftingInventory = e.getInventory();
            Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                ItemStack[] matrix = craftingInventory.getMatrix();
                for (int i = 0; i < matrix.length; i++) {
                    ItemStack slot = matrix[i];
                    if(slot == null) continue;

                    Integer required = requiredAmountFor.apply(i, slot);
                    if(required == null || required <= 1) continue;

                    int extra = required - 1;
                    if(slot.getAmount() <= extra) {
                        matrix[i] = null;
                    } else {
                        slot.setAmount(slot.getAmount() - extra);
                    }
                }
                craftingInventory.setMatrix(matrix);
            });
        }), Main.getMain());
    }

    // Figures out the true number of times the recipe can be paid for (the minimum, across every occupied
    // slot, of that slot's stock divided by its required amount), consumes exactly that much from each slot,
    // and hands the player that many results - any overflow that doesn't fit in their inventory is dropped
    // at their feet, same as vanilla does when a shift-click batch-craft can't all fit.
    private static void craftShiftClickBatch(CraftingInventory craftingInventory, HumanEntity who, ItemStack resultTemplate, BiFunction<Integer, ItemStack, Integer> requiredAmountFor) {
        ItemStack[] matrix = craftingInventory.getMatrix();

        int maxRepeats = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            ItemStack slot = matrix[i];
            if(slot == null) continue;

            int required = Objects.requireNonNullElse(requiredAmountFor.apply(i, slot), 1);
            maxRepeats = Math.min(maxRepeats, slot.getAmount() / required);
        }

        if(maxRepeats <= 0) return;

        for (int i = 0; i < matrix.length; i++) {
            ItemStack slot = matrix[i];
            if(slot == null) continue;

            int required = Objects.requireNonNullElse(requiredAmountFor.apply(i, slot), 1);
            int consumed = required * maxRepeats;
            if(slot.getAmount() <= consumed) {
                matrix[i] = null;
            } else {
                slot.setAmount(slot.getAmount() - consumed);
            }
        }
        craftingInventory.setMatrix(matrix);

        ItemStack result = resultTemplate.clone();
        result.setAmount(result.getAmount() * maxRepeats);

        for(ItemStack leftover : who.getInventory().addItem(result).values()) {
            who.getWorld().dropItem(who.getLocation(), leftover);
        }
    }

    // Method to implement shaped crafting in all valid positions
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

