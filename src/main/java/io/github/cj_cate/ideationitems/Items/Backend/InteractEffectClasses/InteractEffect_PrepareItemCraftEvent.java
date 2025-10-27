package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.function.Consumer;

public class InteractEffect_PrepareItemCraftEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<PrepareItemCraftEvent> eventImplementation;

    public InteractEffect_PrepareItemCraftEvent(Consumer<PrepareItemCraftEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(PrepareItemCraftEvent event) {
        eventImplementation.accept(event);
    }
}
