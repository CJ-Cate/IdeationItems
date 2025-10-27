package io.github.cj_cate.ideationitems.Commands;

import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.BiConsumer;


public class DebugCommands implements CommandExecutor {

    /**
     * This class is how I write commands to debug things on my own. I am leaving it relatively untouched from
     * my own private configuration, but feel free to change it at will.
     */

    private final HashMap<String, BiConsumer<Player, String[]>> snippets = new HashMap<>();

    public DebugCommands() {
        snippets.put("getItemHeld", (p, args) -> {
            ItemStack i = p.getInventory().getItemInMainHand();

            p.sendMessage("has: " + i.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getMain(), "custom"), PersistentDataType.STRING));
            p.sendMessage("pdc: " + i.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getMain(), "custom"), PersistentDataType.STRING));
            p.sendMessage("pdc: " + i.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Main.getMain(), "disabled"), PersistentDataType.STRING));
//            p.sendMessage("droppable: " + i.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "droppable"), PersistentDataType.STRING));
//            p.sendMessage("moveable: " + i.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "moveable"), PersistentDataType.STRING));
//            p.sendMessage("pickupable: " + i.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "pickupable"), PersistentDataType.STRING));

        });

        // Check to see if the item being held is in the item map
        snippets.put("checkItemMap", (p, args) -> {
            p.sendMessage("Starting");
            for(Blueprint bloo : ItemMaps.getBlueprints()) {
                p.sendMessage(bloo.category() + " | " + bloo.value() + " : " + bloo.item().getItemMeta().getDisplayName());
                System.out.println(bloo.hashCode());
            }
            p.sendMessage("Done");
        });

        // Testing (p)ackets and stuff.
        snippets.put("p", (p, args) -> {
            if(args.length == 0) {
                p.sendMessage(ChatColor.DARK_RED + "Need args");
                return;
            }
            CraftPlayer craftPlayer = (CraftPlayer) p;
            ServerGamePacketListenerImpl serverGamePacketListener = craftPlayer.getHandle().connection;
            switch(args[0])
            {
                case "rain" -> {
                    serverGamePacketListener.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 0));
                }
                case "flipspiders" -> {
                    for(Entity e : p.getNearbyEntities(10d, 10d, 10d))
                    {
                        if(e instanceof Spider s)
                        {
                            serverGamePacketListener.send(new ClientboundMoveEntityPacket.Rot(s.getEntityId(), (byte) 180, (byte) 180, false) );
                        }
                    }
                }
            }

        });

        snippets.put("debug", (p, args) -> {
            for (int i = p.getEquipment().getArmorContents().length; i >= 0; i--) {
                String equip = (p.getEquipment().getArmorContents()[i] == null) ? "n/a" : p.getEquipment().getArmorContents()[i].getType().toString();
                Main.debug("i="+i + " -> " + equip);
            }
        });

        // Reload the plugin config
        snippets.put("reload_config", (p, args) -> {
            Main.getMain().reloadConfig();
            p.sendMessage(ChatColor.GREEN + "Plugin configuration reloaded.");
        });

        // Get a player uuid while in-game
        snippets.put("uuid", (p, args) -> {
            if(args.length != 0) {
                p.sendMessage("uuid: " + UUID.fromString(args[0]));
            } else p.sendMessage("uuid: " + p.getUniqueId());
        });

        // Useful to check the insides of an item without reading its raw minecraft output
        snippets.put("has", (p, args) -> p.sendMessage(TagUtil.hasCustomValue(p.getInventory().getItemInMainHand()) + ""));

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p) || !p.hasPermission("ideation.debug")) return true;
        snippets.get(command.getName()).accept(p, args);
        return true;
    }

}
