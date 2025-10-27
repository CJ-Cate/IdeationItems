package io.github.cj_cate.ideationitems.CustomMobs;

import io.github.cj_cate.ideationitems.Main;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;


public class AegisDummy extends IronGolem implements BaseMob {
    /**
     * When doing _anything_ with custom mobs, you are dealing with minecraft NMS code. This isn't scary, but you need
     * to know what you're doing. As long as you aren't a super-beginner, you can play with this.
     * .
     * As a tip from me to you, a lot of these functions are unintuitive by name. Ctrl+click into them to read what
     * they actually do.
     */

    private Location loc;
    public AegisDummy(Location location)
    {
        super(EntityType.IRON_GOLEM, ((CraftWorld) location.getWorld()).getHandle());
        this.loc = location;
        Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> this.remove(RemovalReason.DISCARDED), 5 * 20);
    }

    @Override
    protected void spawnSprintParticle() { super.spawnSprintParticle();}

    @Override
    protected void registerGoals() { }

    protected SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.PIG_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PIG_DEATH;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public void move(MoverType enummovetype, Vec3 vec3d) { }

    public void summon(Player p, float offset)
    {
        this.setYBodyRot(p.getLocation().getYaw() + offset);
        this.setYHeadRot(p.getLocation().getYaw() + offset);

        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        ((CraftWorld)loc.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void setLocation(Location location) {
        this.loc = location;
    }
}
