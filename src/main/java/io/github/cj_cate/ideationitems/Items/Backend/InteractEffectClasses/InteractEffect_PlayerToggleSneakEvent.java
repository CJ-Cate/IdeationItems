package io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.function.Consumer;

public class InteractEffect_PlayerToggleSneakEvent extends InteractEffectHolder implements Listener
{

    private final Consumer<PlayerToggleSneakEvent> eventImplementation;

    public InteractEffect_PlayerToggleSneakEvent(Consumer<PlayerToggleSneakEvent> eventImplementation) {
        this.eventImplementation = eventImplementation;
    }

    @EventHandler
    public void eventToRegister(PlayerToggleSneakEvent event) {
        eventImplementation.accept(event);
    }
}
