package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.function.Consumer;

public class InteractEffect_EntityShootBowEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<EntityShootBowEvent> eventImplementation;

    public InteractEffect_EntityShootBowEvent(Consumer<EntityShootBowEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(EntityShootBowEvent event) {
        eventImplementation.accept(event);
    }
}
