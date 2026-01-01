package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_PlayerInteractEvent;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeType;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Encyclopedia extends ItemClass implements Listener {

    public Blueprint makeEncyclopedia() {
        ItemStack item = ItemUtil.makeItem(Material.KNOWLEDGE_BOOK, ChatColor.GREEN + "Recipe Repo");
        return new Blueprint(item, "codex", Categories.MISC,
                new RecipeHolder(RecipeType.SHAPED_RECIPE,
                        new String[] {"bbb", "bbb", "bbb"},
                        new MaterialCarrier('b', Material.BOOK)),
                new InteractEffect_PlayerInteractEvent(e -> {
                    if(e.getItem() != null && e.getItem().isSimilar(item)) {
                        e.setCancelled(true);
                        e.getPlayer().performCommand("mall");
                    }
                }));
    }

}
