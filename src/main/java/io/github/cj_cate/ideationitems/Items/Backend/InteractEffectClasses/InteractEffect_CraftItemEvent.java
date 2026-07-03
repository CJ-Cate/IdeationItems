package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.function.Consumer;

public class InteractEffect_CraftItemEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<CraftItemEvent> eventImplementation;

    public InteractEffect_CraftItemEvent(Consumer<CraftItemEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(CraftItemEvent event) {
        eventImplementation.accept(event);
    }
}
