package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.function.BiConsumer;


public class UtilCommands implements CommandExecutor
{
    /**
     * A class to write yourself some quick util commands under a single permission.
     * */

    private final HashMap<String, BiConsumer<Player, String[]>> snippets = new HashMap<>();

    public UtilCommands() {

        snippets.put("gmc", (p, args) -> {
            p.setGameMode(GameMode.CREATIVE);
        });
        snippets.put("gms", (p, args) -> {
            p.setGameMode(GameMode.SURVIVAL);
        });
        snippets.put("gma", (p, args) -> {
            p.setGameMode(GameMode.ADVENTURE);
        });
        snippets.put("gmsp", (p, args) -> {
            p.setGameMode(GameMode.SPECTATOR);
        });
        snippets.put("gm", (p, args) -> {
            switch(args[0]) { // There is a slight chance these numbers are in the incorrect order
                case("1") -> p.setGameMode(GameMode.CREATIVE);
                case("2") -> p.setGameMode(GameMode.SURVIVAL);
                case("3") -> p.setGameMode(GameMode.ADVENTURE);
                case("4") -> p.setGameMode(GameMode.SPECTATOR);
                default -> {}
            }
            p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Changing gamemode to " + p.getGameMode());
        });

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player p) || !p.hasPermission("ideation.utils")) return true;
        snippets.get(command.getName()).accept(p, args);
        return true;

    }
}

