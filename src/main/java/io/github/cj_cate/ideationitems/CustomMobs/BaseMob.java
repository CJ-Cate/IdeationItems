package io.github.cj_cate.ideationitems.CustomMobs;

import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;

// Don't touch anything here.
public interface BaseMob<T extends Entity>
{
    default T getEntity() { return (T) this; }
    HashMap<Integer, Runnable> attacks = new HashMap<>();

    default void summon(Location loc) {
        getEntity().setPos(loc.getX(), loc.getY(), loc.getZ());
        ((CraftWorld)loc.getWorld()).getHandle().addFreshEntity(getEntity(), CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
    default void doPacketStuff() { }
    void setLocation(Location location);
}
