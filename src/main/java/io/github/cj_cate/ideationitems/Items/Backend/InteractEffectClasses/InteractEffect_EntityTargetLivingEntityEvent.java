package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.function.Consumer;

public class InteractEffect_EntityTargetLivingEntityEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<EntityTargetLivingEntityEvent> eventImplementation;

    public InteractEffect_EntityTargetLivingEntityEvent(Consumer<EntityTargetLivingEntityEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(EntityTargetLivingEntityEvent event) {
        eventImplementation.accept(event);
    }
}
