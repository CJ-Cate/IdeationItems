package io.github.cj_cate.ideationitems.Utils;

import io.github.cj_cate.ideationitems.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class TagUtil
{
    public enum Tag {

        DISABLED("disabled"),
        UNPLACEABLE("unplaceable"),
        ENCHANTABLE("enchantable"),
        ENCHANTED("enchanted"),
        CUSTOM("custom");

        private final String enumTag;

        Tag(String tag) {
            this.enumTag = tag.toLowerCase();
        }

        public String getTag() {
            return enumTag;
        }
    }

    /**
     * Returns the items value for the "custom" key
     * **/
    public static String getCustomValue(ItemStack item)
    {
        if(item == null || item.getItemMeta() == null || !hasCustomValue(item)) {
            return "null"; // String so .equals() can safely be applied
        }
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getMain(), Tag.CUSTOM.getTag()), PersistentDataType.STRING);
    }

    public static String getCustomValue(ItemStack item, String key)
    {
        if(item == null || item.getItemMeta() == null || !hasCustomValue(item)) {
            return "null"; // String so .equals() can safely be applied
        }
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getMain(), key), PersistentDataType.STRING);
    }

    public static boolean hasCustomValue(ItemStack item)
    {
        if(item == null || item.getItemMeta() == null) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getMain(), Tag.CUSTOM.getTag()), PersistentDataType.STRING);
    }

    public static boolean hasCustomValue(ItemStack item, String key)
    {
        if(item == null || item.getItemMeta() == null) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getMain(), key), PersistentDataType.STRING);
    }

    /**
     * Pass in an item to get a disabled version of it out. Disabled items should not be movable, droppable,
     * pickup-able, and so on. Used mostly for visuals, but in some cases like sickles for occluding an inventory spot
     */
    public static ItemStack tagDisabled(ItemStack input)
    {
        if(input.getType() == Material.AIR || input.getItemMeta() == null) return null;

        ItemMeta meta = input.getItemMeta();
        meta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.RED + "Disabled item")));
        meta.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), Tag.DISABLED.getTag()), PersistentDataType.STRING, "disabled");
        ItemStack returner = ItemUtil.makeItem(input.getType(), input.getItemMeta().getDisplayName());

        returner.setItemMeta(meta);
        return returner;
    }

    // make one with custom lore
    public static ItemStack tagDisabled(ItemStack input, String... lore)
    {
        if(input.getType() == Material.AIR || input.getItemMeta() == null) return null;

        ItemMeta meta = input.getItemMeta();
        meta.setLore(new ArrayList<>(Arrays.asList(lore)));
        meta.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), Tag.DISABLED.getTag()), PersistentDataType.STRING, "disabled");
        ItemStack returner = ItemUtil.makeItem(input.getType(), input.getItemMeta().getDisplayName());

        returner.setItemMeta(meta);
        return returner;
    }

    // make one with no description and no name
    public static ItemStack tagDisabled(ItemStack input, boolean has_description)
    {
        if(has_description) return tagDisabled(input);
        if(input.getType() == Material.AIR || input.getItemMeta() == null) return new ItemStack(Material.STRUCTURE_VOID);

        ItemMeta meta = input.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), Tag.DISABLED.getTag()), PersistentDataType.STRING, "disabled");
        meta.setHideTooltip(true);
        ItemStack returner = ItemUtil.makeItem(input.getType(), input.getItemMeta().getDisplayName());

        returner.setItemMeta(meta);
        return returner;
    }

    public static boolean isDisabled(ItemStack item) {
        return hasCustomValue(item, Tag.DISABLED.getTag());
    }

    public static ItemStack tagUnplaceable(ItemStack input) {
        ItemMeta meta = input.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), Tag.UNPLACEABLE.getTag()), PersistentDataType.STRING, "true");

        input.setItemMeta(meta);
        return input;
    }

    private static boolean hasBooleanTag(ItemStack item, String key) {
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getMain(), key));
    }

    public static ItemStack tagEnchantable(ItemStack item, boolean bool) {
        ItemMeta m = item.getItemMeta();
        m.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), Tag.ENCHANTABLE.getTag()), PersistentDataType.BOOLEAN, bool);
        item.setItemMeta(m);
        return item;
    }
    public static boolean hasTagEnchantable(ItemStack item) {
        return hasBooleanTag(item, Tag.ENCHANTABLE.getTag());
    }

    public static void setSpawnerMob(Entity entity) {
        entity.getPersistentDataContainer().set(new NamespacedKey(Main.getMain(), "spawner_mob"), PersistentDataType.STRING, "irrelevant");
    }
    public static boolean isNotSpawnerMob(Entity entity) {
        return !entity.getPersistentDataContainer().has(new NamespacedKey(Main.getMain(), "spawner_mob"), PersistentDataType.STRING);
    }
}
