package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Consumer;


public class InteractEffect_PlayerMoveEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<PlayerMoveEvent> eventImplementation;

    public InteractEffect_PlayerMoveEvent(Consumer<PlayerMoveEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(PlayerMoveEvent event) {
        eventImplementation.accept(event);
    }

}
