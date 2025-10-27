package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.function.Consumer;

public class InteractEffect_ProjectileLaunchEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<ProjectileLaunchEvent> eventImplementation;

    public InteractEffect_ProjectileLaunchEvent(Consumer<ProjectileLaunchEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(ProjectileLaunchEvent event) {
        eventImplementation.accept(event);
    }
}
