package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.function.Consumer;

public class InteractEffect_EntityDamageByEntityEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<EntityDamageByEntityEvent> eventImplementation;

    public InteractEffect_EntityDamageByEntityEvent(Consumer<EntityDamageByEntityEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(EntityDamageByEntityEvent event) {
        eventImplementation.accept(event);
    }

}
