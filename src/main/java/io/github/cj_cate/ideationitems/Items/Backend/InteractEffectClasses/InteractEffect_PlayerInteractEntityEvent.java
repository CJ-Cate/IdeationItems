package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.function.Consumer;

public class InteractEffect_PlayerInteractEntityEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<PlayerInteractEntityEvent> eventImplementation;

    public InteractEffect_PlayerInteractEntityEvent(Consumer<PlayerInteractEntityEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(PlayerInteractEntityEvent event) {
        eventImplementation.accept(event);
    }

}

