package io.github.cj_cate.ideationitems.Items.Backend;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

//@GenerateEventHolder(eventType = PlayerInteractEvent.class, className = "PlayerInteractEventHolder")
//@GenerateEventHolder(eventType = ExplosionPrimeEvent.class, className = "ExplosionPrimeEventHolder")
public abstract class EventHolder<T extends Event> implements Listener {
    protected Consumer<T> eventConsumer;

    protected EventHolder(Plugin plugin, Consumer<T> eventConsumer) {
        this.eventConsumer = eventConsumer;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public abstract void onEvent(T event);
}