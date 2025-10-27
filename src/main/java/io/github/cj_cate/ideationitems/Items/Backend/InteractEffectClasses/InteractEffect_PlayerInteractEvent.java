package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.function.Consumer;

public class InteractEffect_PlayerInteractEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<PlayerInteractEvent> eventImplementation;

    public InteractEffect_PlayerInteractEvent(Consumer<PlayerInteractEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(PlayerInteractEvent event) {
        eventImplementation.accept(event);
    }

}
