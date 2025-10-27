package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.function.Consumer;

public class InteractEffect_PlayerFishEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<PlayerFishEvent> eventImplementation;

    public InteractEffect_PlayerFishEvent(Consumer<PlayerFishEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(PlayerFishEvent event) {
        eventImplementation.accept(event);
    }
}
