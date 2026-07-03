package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_EntityShootBowEvent;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_PlayerInteractEntityEvent;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_PlayerMoveEvent;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_PlayerToggleSneakEvent;
import io.github.cj_cate.ideationitems.Items.Backend.InstanceData.InstanceData;
import io.github.cj_cate.ideationitems.Items.Backend.InstanceData.InstanceDataFields;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeType;
import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.*;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExampleItems extends ItemClass
{

    public Blueprint makeTransmutationshroom()
    {
        String custom_value = "transmutation_mushroom";
        ItemStack item = ItemUtil.makeItem(Material.CRIMSON_FUNGUS, ChatColor.GREEN + "Transmutation Shrooom",
                new ArrayList<>(List.of("Its all natrual, duddeeeee")));

        TagUtil.tagUnplaceable(item);

        return new Blueprint(item, custom_value, Categories.UTILITY,
            new RecipeHolder(
                RecipeType.SHAPELESS_RECIPE,
                new MaterialCarrier(Material.PUFFERFISH),
                new MaterialCarrier(Material.RED_MUSHROOM),
                new MaterialCarrier(Material.APPLE),
                new MaterialCarrier(Material.FEATHER),
                new MaterialCarrier(Material.BONE_MEAL)
            ),
            null,
            new InteractEffect_PlayerInteractEntityEvent(e -> {
                if(!TagUtil.hasCustomValueOf(e.getPlayer().getItemInUse(), custom_value)) {
                    return;
                }
                for(EntityType[] entityTypePool : entityPools)
                {
                    if (!List.of(entityTypePool).contains(e.getRightClicked().getType())) {
                        continue;
                    }

                    // Maybe lets not transmutate peoples pets! On the other hand...
                    Entity clicked_entity = e.getRightClicked();
                    if (clicked_entity.getCustomName() != null && clicked_entity.isCustomNameVisible()) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.RED + "You monster!");
                    }

                    Location entity_spawn_location = e.getRightClicked().getLocation();
                    EntityType entity_to_spawn = entityTypePool[ThreadLocalRandom.current().nextInt(entityTypePool.length)];
                    e.getPlayer().getWorld().spawnEntity(entity_spawn_location, entity_to_spawn);

                    // Send the mob that was clicked on into the void
                    e.getRightClicked().teleport(new Location(e.getPlayer().getWorld(), 0, -100, 0)); // LOL
                    // Subtract 1 from the item
                    e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                    return;
                }


            }));
    }
    private final EntityType[][] entityPools = {new EntityType[]{EntityType.COW, EntityType.CHICKEN, EntityType.PIG, EntityType.SHEEP, EntityType.RABBIT},
            new EntityType[]{EntityType.COD, EntityType.SALMON, EntityType.TROPICAL_FISH, EntityType.AXOLOTL, EntityType.DOLPHIN},
            new EntityType[]{EntityType.SQUID, EntityType.GLOW_SQUID},
            new EntityType[]{EntityType.FROG, EntityType.ARMADILLO, EntityType.OCELOT}, // add camels and llamas, but patch out riding them and dying in the void
            new EntityType[]{EntityType.POLAR_BEAR, EntityType.PANDA}};



    public Blueprint getStormcaller()
    {
        String custom_value = "stormcall_bow";
        ItemStack item = ItemUtil.makeItem(Material.BOW, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Stormcaller Bow",
            new ArrayList<>(Arrays.asList(
                "A single shot of this bow",
                "is enough to make the heavens cry"
            )));

        return new Blueprint(item, custom_value, Categories.UTILITY,
                new RecipeHolder(
                    RecipeType.SHAPED_RECIPE,
                    new String[]{
                        " WS",
                        "WLS",
                        " WS"
                    },
                    new MaterialCarrier('W', Material.STICK),
                    new MaterialCarrier('L', Material.LIGHTNING_ROD),
                    new MaterialCarrier('S', Material.STRING)
                ),
            null,
            new InteractEffect_EntityShootBowEvent(e -> {
                if(!TagUtil.hasCustomValueOf(e.getBow(), custom_value)) {
                    return;
                }
                double startingY = e.getEntity().getLocation().getY();
                Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
                    if(e.getProjectile().getLocation().getY() >= startingY + 55) { // 55 blocks is a good value for this
                        if(!e.getEntity().getEquipment().getItemInMainHand().isSimilar(item)) {
                            // Patch being able to drop the bow after firing the arrow
                            e.getEntity().sendMessage(ChatColor.GREEN + "> keep the bow in your main hand to bring the rain");
                        }

                        e.getProjectile().remove();
                        e.getEntity().getWorld().setStorm(true);
                        e.getEntity().getWorld().setWeatherDuration(3 * 60*20); // n tick-minutes = n [minutes] * 60 [seconds/minute] * 20 [ticks/second]
                        e.getBow().setAmount(0);
                        if(e.getEntity() instanceof Player player) {
                            player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 2, 2);
                        }
                    }
                }, 40); // 2 seconds

            })
        );
    }

    public Blueprint getRotor() {
        ItemStack item = ItemUtil.makeItem(Material.IRON_SHOVEL, ChatColor.GRAY + "" + ChatColor.BOLD + "Boat Rotor",
            new ArrayList<>(Arrays.asList(
                "Hold this in your off-hand while",
                "in a boat to nyooommmm"
            )));

        return new Blueprint(item, "rotor", Categories.MISC, new RecipeHolder(
            RecipeType.SHAPED_RECIPE,
            new String[]{
                "iri",
                "iri",
                " s ",
            },
            new MaterialCarrier('i', Material.IRON_INGOT),
            new MaterialCarrier('r', Material.REDSTONE_BLOCK),
            new MaterialCarrier('s', Material.IRON_SHOVEL)),
            null,
            new InteractEffect_PlayerMoveEvent(e -> {
                if(e.getPlayer().getVehicle() == null) return;

                // General boat name since the addition of boat types
                if(e.getPlayer().getVehicle().getType().getName().contains("BOAT"))
                {
                    Boat boat = (Boat) e.getPlayer().getVehicle();
                    if(!boat.isOnGround() && e.getPlayer().getInventory().getItemInOffHand().equals(item))
                    {
                        // (hacky) Math to modify the character velocity in the direction of x,z
                        Vector v = e.getPlayer().getVelocity();
                        double xz = 20;
                        double x = v.getX() * xz;
                        double y = e.getPlayer().getVehicle().getVelocity().getY();
                        double z = v.getZ() * xz;

                        e.getPlayer().getVehicle().setVelocity(new Vector(x,y,z));
                    }
                }
            }));
    }

    // Colors cycle in this order and loop back around to red
    private final Color[] rainbowColors = {
        Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE
    };

    public Blueprint getMagicalPants() {
        ItemStack item = ItemUtil.makeItem(Material.LEATHER_LEGGINGS, "Magical Pants");
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(rainbowColors[0]);
        item.setItemMeta(meta);

        String custom_value = "magical_pants";
        InstanceData instanceData = new InstanceData(InstanceDataFields.LEATHER_COLOR);

        return new Blueprint(item, custom_value, Categories.ARMOR, null, instanceData,
            new InteractEffect_PlayerToggleSneakEvent(e -> {
                if(!e.isSneaking()) return;
                if(e.getPlayer().getInventory().getLeggings() == null) return;
                ItemStack leggings = e.getPlayer().getInventory().getLeggings();
                if(!TagUtil.hasCustomValueOf(leggings, custom_value)) return;

                LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
                int currentIndex = Arrays.asList(rainbowColors).indexOf(leggingsMeta.getColor());
                Color nextColor = rainbowColors[(currentIndex + 1) % rainbowColors.length];

                instanceData.set(leggings, InstanceDataFields.LEATHER_COLOR, nextColor);
                e.getPlayer().getInventory().setLeggings(leggings);
            })
        );
    }


}
