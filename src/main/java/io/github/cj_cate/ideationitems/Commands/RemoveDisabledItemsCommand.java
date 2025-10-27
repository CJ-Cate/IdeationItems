package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RemoveDisabledItemsCommand implements CommandExecutor {
    /**
     * On occasion, when dealing with items with the "disabled" tag, they get stuck or duplicated. This command will
     * remove all tagged items from the senders inventory, which should always be safe.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)) return true;
        if(!command.getName().equalsIgnoreCase("remove")) return true;

        ItemStack[] inv = p.getInventory().getContents();
        for (int i = 0; i < inv.length; i++) {
            if(inv[i] == null) continue;
            if(TagUtil.isDisabled(inv[i])) p.getInventory().setItem(i, new ItemStack(Material.AIR));
        }

        return true;
    }

}
