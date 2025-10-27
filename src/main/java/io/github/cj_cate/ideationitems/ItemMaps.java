package io.github.cj_cate.ideationitems;

import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class ItemMaps {

    private static final HashMap<String, Blueprint> blooMap = new HashMap<>();

    public static boolean containsKey(String custom_value) { return blooMap.containsKey(custom_value); }
    public static void addToMap(String pdc_key, Blueprint bloo) { blooMap.put(pdc_key, bloo); }
    public static List<Blueprint> getBlueprints() { return blooMap.values().stream().toList(); }
    public static List<String> getKeys() { return blooMap.keySet().stream().toList(); }

    public static Blueprint getBlueprint(String key) { return blooMap.get(key); }
    public static ItemStack getItem(String custom_value) { return blooMap.get(custom_value).item(); }
    public static ItemStack getItem(String custom_value, int amount) {
        if(amount <= 0) throw new IllegalArgumentException("Cannot have invalid amount, reference ItemMaps!");
        ItemStack item =  blooMap.get(custom_value).item();
        item.setAmount(amount);
        return item;
    }


}