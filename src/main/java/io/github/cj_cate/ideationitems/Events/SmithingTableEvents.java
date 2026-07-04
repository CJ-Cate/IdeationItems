package io.github.cj_cate.ideationitems.Events;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;

public class SmithingTableEvents implements Listener {
    /**
     * If there is a vanilla item CATEGORY output, then change it with the true vanilla item.
     */
    @EventHandler
    public void changeCustomVanillaSmithingOutput(PrepareSmithingEvent e) {
        ItemStack result = e.getResult();
        if(TagUtil.hasVanillaCategory(result)) {
            e.setResult(new ItemStack(result.getType(), result.getAmount()));
        }
    }
}
