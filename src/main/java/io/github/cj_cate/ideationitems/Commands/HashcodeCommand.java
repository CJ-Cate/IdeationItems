package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HashcodeCommand implements CommandExecutor {
    /**
     * This class lets the user run /hashcode to give them the hashcode of the item's custom tag. This is helpful for
     * people who want to make texture packs, because this is the value they will use to reference the item.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!command.getName().equalsIgnoreCase("hashcode") || !(sender instanceof Player p)) {
            return true;
        }

        if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
            p.sendMessage(ChatColor.DARK_AQUA + "Hold a custom item to read its hashcode");
            return true;
        }

        ItemStack item = p.getInventory().getItemInMainHand();

        if(!TagUtil.hasCustomValue(item)) {
            p.sendMessage(ChatColor.DARK_AQUA +"This item has no custom value!");
            return true;
        }

        p.sendMessage(ChatColor.AQUA + TagUtil.getCustomValue(item) + ".hashCode()" + ChatColor.RESET + " -> " + ChatColor.GOLD + Math.abs(TagUtil.getCustomValue(item).hashCode()));

        return true;
    }
}
