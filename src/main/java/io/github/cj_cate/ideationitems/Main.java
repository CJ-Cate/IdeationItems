package io.github.cj_cate.ideationitems;

import io.github.cj_cate.ideationitems.Commands.*;
//import io.github.cj_cate.ideationitems.Events.CustomDisabledItemEvents;
import io.github.cj_cate.ideationitems.Events.DisableVanillaEvents;
import io.github.cj_cate.ideationitems.Events.InventoryRefresh;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Utils.JarPackageScanner;
import io.github.cj_cate.ideationitems.Utils.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;

public final class Main extends JavaPlugin {

    public static void debug(String s) {
//        if(!((boolean) config("debug-messages"))) return;

        for (Player p : getMain().getServer().getOnlinePlayers()) {
            p.sendMessage("debug " + s);
        }
    }

    public static void debug() {
        for (Player p : getMain().getServer().getOnlinePlayers()) {
            p.sendMessage("debug " + (System.currentTimeMillis() % 1000));
        }
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&d" + message));
    }

    public static <T> Object config(String path) {
        Object obj = mainn.getConfig().get(path);
        return (obj == null) ? null : (T) obj;
    }

    //needed for calling instances of main
    private static Main mainn;
    public static Main getMain() {
        return mainn;
    }

    @Override
    public void onLoad() {
        mainn = this;
    }

    @Override
    public void onEnable() {
        log("Ideation Items is starting!!! Yippieee!!!");

        // run this constructor before all the other recipes so that only vanilla ones are removed
        InventoryRefresh _inventoryRefresh = new InventoryRefresh();

        // implement
        try {
            // Predicate.not(Class::isAnonymousClass) === aclass -> !aclass.isAnonymousClass()
            for(Class<?> clazz : JarPackageScanner.findClassesInJar("io.github.cj_cate.ideationitems.Items", Predicate.not(Class::isAnonymousClass),false))
            {
                // make sure its an itemclass
                if(!ItemClass.class.isAssignableFrom(clazz)) continue;

                Object instance;
                try {
                    instance = clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    log("problem with the instance. refer to " + clazz.getSimpleName());
                    throw new RuntimeException(e);
                }
                // register events tied to the class
                try {
                    if(Listener.class.isAssignableFrom(clazz)) {
                        Bukkit.getPluginManager().registerEvents((Listener) instance, this);
                    }
                } catch (Exception e) {
                    log("Error registering dynamic class events. Class: " + clazz.getSimpleName());
                }
//                log("Registering " + clazz.getSimpleName());
                Blueprint.registerClassItems(clazz, instance);
            }
            // register recipes now that items have been added
            for(Blueprint bloo : ItemMaps.getBlueprints()) {
                Blueprint.registerRecipe(bloo);
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            log("Missing constructor? Apparently?");
            throw new RuntimeException(e);
        }

        new ScoreboardUtil(); // for appy appy constructor mefod :3

        /* Event Registering starts here */

        Bukkit.getPluginManager().registerEvents(_inventoryRefresh, this); // Refreshes inventory when a command is run or player joins the server

        Bukkit.getPluginManager().registerEvents(new DisableVanillaEvents(), this); // The events created to disable certain vanilla game aspects
//        Bukkit.getPluginManager().registerEvents(new CustomDisabledItemEvents(), this); // Events to handle custom disabled items

        //Disabled during testing AND until I can find a way to make the constructors appy appy
//        Bukkit.getPluginManager().registerEvents(new CustomMobSpawning(), this); // Handles spawning NMS entities in the world
//        Bukkit.getPluginManager().registerEvents(new DebugEvents(), this); // Debug specific stuff

        MallCommand _mallCommand = new MallCommand();
        Bukkit.getPluginManager().registerEvents(_mallCommand, this);

        /* */
        /* Command registering start here */
        /* */

        getCommand("mall").setExecutor(_mallCommand); // ADVANCED mall command

        // TODO: add an admin-only param to refresh someone elses inventory
        getCommand("invr").setExecutor(_inventoryRefresh); // Manually refresh your own inventory

        UtilCommands utilCommands = new UtilCommands();
        getCommand("rename").setExecutor(utilCommands);
        getCommand("gmc").setExecutor(utilCommands);
        getCommand("gms").setExecutor(utilCommands);
        getCommand("gma").setExecutor(utilCommands);
        getCommand("gmsp").setExecutor(utilCommands);
        getCommand("gm").setExecutor(utilCommands);

        DebugCommands debugCommands = new DebugCommands();
        getCommand("p").setExecutor(debugCommands);
        getCommand("debug").setExecutor(debugCommands);
        getCommand("checkItemMap").setExecutor(debugCommands); // Debug command
        getCommand("getItemHeld").setExecutor(debugCommands); // Debug command
        getCommand("uuid").setExecutor(debugCommands); // Debug command
        getCommand("reload-ideas").setExecutor(debugCommands); // Debug command

        getCommand("remove").setExecutor(new RemoveDisabledItemsCommand());
        getCommand("hashcode").setExecutor(new HashcodeCommand());

    }

    @Override
    public void onDisable() {
        log("Ideation Items properly shutting down...");
    }


}

