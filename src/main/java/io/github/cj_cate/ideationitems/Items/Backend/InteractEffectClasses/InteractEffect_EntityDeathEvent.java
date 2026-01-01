package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.function.Consumer;

public class InteractEffect_EntityDeathEvent extends InteractEffectHolder implements Listener //
{

    private final Consumer<EntityDeathEvent> eventImplementation; //

    public InteractEffect_EntityDeathEvent(Consumer<EntityDeathEvent> eventImplementation) { //
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(EntityDeathEvent event) { //
        eventImplementation.accept(event);
    }
}
