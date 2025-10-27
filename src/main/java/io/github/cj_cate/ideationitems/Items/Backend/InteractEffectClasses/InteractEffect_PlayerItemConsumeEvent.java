package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.function.Consumer;

public class InteractEffect_PlayerItemConsumeEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<PlayerItemConsumeEvent> eventImplementation;

    public InteractEffect_PlayerItemConsumeEvent(Consumer<PlayerItemConsumeEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(PlayerItemConsumeEvent event) {
        eventImplementation.accept(event);
    }
}

