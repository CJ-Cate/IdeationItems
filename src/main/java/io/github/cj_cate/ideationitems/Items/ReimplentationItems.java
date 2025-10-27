package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_EntityDamageByEntityEvent;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeType;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

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
            Material.CHAIN
    ));

    public ReimplentationItems() {
        rand = new Random();
    }

    @EventHandler
    public void goatDropMutton(EntityDeathEvent e) {
        if(e.getEntityType() == EntityType.GOAT) {
            e.getDrops().add(new ItemStack(Material.MUTTON, rand.nextInt(2)));
        }
    }

    public Blueprint makeVanillaChain() {
        return new Blueprint(new ItemStack(Material.CHAIN, 3), "vanilla_chain", Categories.VANILLA,
            new RecipeHolder(
                RecipeType.SHAPED_RECIPE,
                new String[]{
                        " i "," T "," i "
                },
                new MaterialCarrier('i', Material.IRON_NUGGET),
                new MaterialCarrier('T', Material.IRON_INGOT)
            ));
    }




}
