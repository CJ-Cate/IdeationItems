package io.github.cj_cate.ideationitems.Utils;

import io.github.cj_cate.ideationitems.ItemMaps;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemUtil
{

    // Simple static functions to ease the creation of creating items, when needed. Mostly for more
    //   complicated or cumbersome attributes

    // So I dont fuck up the naming of something and spend hours debugging:
//    public final static String unplaceable = "unplaceable";
//    public final static String undroppable = "undroppable";
//    public final static String unmoveable = "unmoveable";
//    public final static String unpickupable = "unpickupable";



    public static ItemStack makeItem(Material material, String name)
    {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        meta.setUnbreakable(true);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material material, String name, List<String> lore)
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + name);
        meta.setLore(lore.stream().map(s -> ChatColor.GRAY + s).toList());

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material material, String name, TrimPattern trimPattern, TrimMaterial trimMaterial, List<String> lore) {
        ItemStack item = new ItemStack(material);


        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
        meta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);

        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }


    /**
     * If 1, return 1.
     * Elif less than 0.9, return self
     * Else, return a half of what was passed in
     * @param p_getAttackCooldown
     * @return
     */
    public static float getAttackCooldownMultiplier(float p_getAttackCooldown)
    {
        if(p_getAttackCooldown == 1f)
        {
            return 1;
        } else if(p_getAttackCooldown <= 0.9f) {
            return p_getAttackCooldown;
        } else return 0.5f*p_getAttackCooldown;
    }

    public static boolean compareItems(ItemStack held, ItemStack comparator_lol)
    {
        if(held == null || comparator_lol == null) return false;
        return held.equals(comparator_lol);
    }

    // is this bad practice?
    private static HashMap<Integer, ChatColor> rainbowMap = new HashMap<>() {{
        put(1, ChatColor.RED);
        put(2, ChatColor.GOLD);
        put(3, ChatColor.YELLOW);
        put(4, ChatColor.DARK_GREEN);
        put(5, ChatColor.DARK_AQUA);
        put(6, ChatColor.BLUE);
        put(7, ChatColor.LIGHT_PURPLE);
    }};

    /**
     * @param input: input string
     * @param offset: single optional integer to offset the rainbow
     * @return: a rainbow version of the input string
     */
    public static String makeTextRainbow(String input, int... offset)
    {
        int ofset;
        try {
            ofset = offset[0];
        } catch(Exception e) {
            ofset = 0;
        }

        StringBuilder product = new StringBuilder();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            product.append(rainbowMap.get((i + ofset) % rainbowMap.size()) + (chars[i] + ""));
        }

        return product.toString();
    }


    // cooldown map. stores the player, and for each player the cooldown for each item
    private static HashMap<UUID, HashMap<String, Long>> cdMap = new HashMap<>();

    public static boolean cooldownCheck(Player p, ItemStack item, float cooldown) {

        String value = TagUtil.getCustomValue(item, "custom");
        if(value.equals("null")) return false; // if the item is not custom, it must be impossible to cooldown
        UUID u = p.getUniqueId();
        long timeLeft = System.currentTimeMillis() - cdMap.get(u).get(value);

        // if there is no map to the player it must be false
        if (!cdMap.containsKey(u)) {
            cdMap.put(u, new HashMap<String, Long>());
        }

        if(!cdMap.get(u).containsKey(value)) {
            cdMap.get(u).put(value, System.currentTimeMillis());
            return true;
        } else if (timeLeft > cooldown) {
            // otherwise check to see if the cooldown is done
            p.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + timeLeft + ChatColor.RESET + ChatColor.GOLD + " seconds left...");
            p.playSound(p, Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.4f, 0.4f);
            return false;
        } else {
            cdMap.get(u).remove(value);
            cdMap.get(u).put(value, System.currentTimeMillis());
            return true;
        }

    }

    /**
     * Compares the item passed in to its code-based counterpart. Used for testing if items have been updated in code.
     * Returns false if there is no "custom" pdc
     * @param item Item to compare
     * @return True if it is up-to-date with the code-based counterpart
     */
    public static boolean compareCustom(ItemStack item) {
        if(item == null) return false;
        if(!TagUtil.hasCustomValue(item)) return false;

        ItemStack codeItem = ItemMaps.getBlueprint(TagUtil.getCustomValue(item)).item();
        ItemMeta codeMeta = codeItem.getItemMeta();
        ItemMeta meta = item.getItemMeta();

        return item.isSimilar(codeItem) &&
               meta.getLore().equals(codeMeta.getLore()) &&
               meta.getDisplayName().equalsIgnoreCase(codeMeta.getDisplayName()) &&
               item.getType().equals(codeItem.getType());

    }

    /**
     * Minikloons UUID
     * @return a constant UUID
     */
    public static UUID getKloonUUID() {
        return UUID.fromString("20934ef9-488c-4651-80a7-8f861586b4cf");
    }

    public static ItemStack makeTippedArrow(Color color) {
        return makeTippedArrow(color, null, 0, 0);
    }
    public static ItemStack makeTippedArrow(Color color, PotionEffectType effect, int seconds, int amplifier) {
        ItemStack result = TagUtil.tagDisabled(ItemUtil.makeItem(Material.TIPPED_ARROW, "name"), false);
        // name has to be re-set due to the nature of my sanity

        PotionMeta meta = (PotionMeta) result.getItemMeta();
        meta.setColor(color);
        meta.setHideTooltip(false);

        if(effect != null) {
            // I dont know what 'b:true' is. Ambient? I guess keep it true?
            meta.addCustomEffect(new PotionEffect(effect, seconds*20, amplifier), true);
        }

        result.setItemMeta(meta);

        return result;
    }


}
