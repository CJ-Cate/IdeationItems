package io.github.cj_cate.ideationitems;

import io.github.cj_cate.ideationitems.Commands.*;
import io.github.cj_cate.ideationitems.Events.DisabledItemEvents;
import io.github.cj_cate.ideationitems.Events.ModifyVanillaEvents;
import io.github.cj_cate.ideationitems.Commands.InventoryRefresh;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Utils.JarPackageScanner;
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
        InventoryRefresh inventoryRefresh = new InventoryRefresh();

        // Loop through all ItemClass in Items directory and...
        //  1. find all public Blueprint methods to implement the item
        //  2. register events in said classes
        try {
            // Predicate.not(Class::isAnonymousClass) === aclass -> !aclass.isAnonymousClass() // logical equivalency, not a line of code to run
            for(Class<?> clazz : JarPackageScanner.findClassesInJar("io.github.cj_cate.ideationitems.Items", Predicate.not(Class::isAnonymousClass),false))
            {
                // make sure its an itemclass
                if(!ItemClass.class.isAssignableFrom(clazz)) continue;

                // try to instantiate the class
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

//        new ScoreboardUtil(); // for appy appy constructor mefod :3

        // //
        // // Event Registering starts here
        // // IMPORTANT: Events that are in an ItemClass are automatically registered. Don't double-register them!
        // // 

        // Refreshes inventory when a command is run or player joins the server. You want this.
        Bukkit.getPluginManager().registerEvents(inventoryRefresh, this);

        // The events created to disable certain vanilla game aspects. Opinionated, so go configure it. You probably want this.
        Bukkit.getPluginManager().registerEvents(new ModifyVanillaEvents(), this);
        Bukkit.getPluginManager().registerEvents(new DisabledItemEvents(), this);


        MallCommand mallCommand = new MallCommand();
        Bukkit.getPluginManager().registerEvents(mallCommand, this);
        GetRecipeCommand getRecipeCommand = new GetRecipeCommand();
        Bukkit.getPluginManager().registerEvents(getRecipeCommand, this);

        // Debug specific stuff, enable when you need it.
//        Bukkit.getPluginManager().registerEvents(new DebugEvents(), this);

        // //
        // // Command registering start here
        // //

        // Mall command to preview recipes and cheat in items (with admin permissions)
        getCommand("mall").setExecutor(mallCommand);

        // Manually refresh your own inventory
        // TODO: add an admin-only param to refresh someone elses inventory
        getCommand("invr").setExecutor(inventoryRefresh);

        // Various commands re-implemnented
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

        // Removes extra disabled items if they get stuck in your inventory
        getCommand("remove").setExecutor(new RemoveDisabledItemsCommand());
        // Get the hashcode of an item, used to create resource packs. No
        getCommand("hashcode").setExecutor(new HashcodeCommand());
        // Get the recipe for an item without going through the mall
        getCommand("getrecipe").setExecutor(getRecipeCommand);

    }

    @Override
    public void onDisable() {
        log("Ideation Items properly shutting down...");
    }


}

