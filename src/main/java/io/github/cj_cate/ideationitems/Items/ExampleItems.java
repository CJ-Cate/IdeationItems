package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_EntityShootBowEvent;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_PlayerInteractEntityEvent;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.MaterialCarrier;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeHolder;
import io.github.cj_cate.ideationitems.Items.Backend.RecipeStuffs.RecipeType;
import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExampleItems extends ItemClass
{

    public Blueprint makeTransmutationshroom()
    {
        ItemStack item = ItemUtil.makeItem(Material.CRIMSON_FUNGUS, ChatColor.GREEN + "Transmutation Shrooom",
                new ArrayList<>(List.of("Its all natrual, duddeeeee")));
        TagUtil.tagUnplaceable(item);

        return new Blueprint(item, "transmutation_mushroom", Categories.UTILITY,
            new RecipeHolder(
                RecipeType.SHAPELESS_RECIPE,
                new MaterialCarrier(Material.PUFFERFISH),
                new MaterialCarrier(Material.RED_MUSHROOM),
                new MaterialCarrier(Material.APPLE),
                new MaterialCarrier(Material.FEATHER),
                new MaterialCarrier(Material.BONE_MEAL)
            ),
            new InteractEffect_PlayerInteractEntityEvent(e -> {
                if(!e.getPlayer().getInventory().getItemInMainHand().isSimilar(item)) {
                    return;
                }
                for(EntityType[] entityTypePool : entityPools)
                {
                    if (List.of(entityTypePool).contains(e.getRightClicked().getType())) {

                        e.getPlayer().getWorld().spawnEntity(e.getRightClicked().getLocation(), entityTypePool[ThreadLocalRandom.current().nextInt(entityTypePool.length)]);
                        e.getRightClicked().teleport(new Location(e.getPlayer().getWorld(), 0, -100, 0)); // LOL

                        e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                        return;
                    }
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
        ItemStack item = ItemUtil.makeItem(Material.BOW, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Stormcaller Bow",
            new ArrayList<>(Arrays.asList(
                "A single shot of this bow",
                "is enough to make the heavens cry"
            )));

        return new Blueprint(item, "stormcall_bow", Categories.UTILITY, null,
            new InteractEffect_EntityShootBowEvent(e -> {
//                    if(ItemUtil.)
                if(!(e.getBow() != null && e.getBow().isSimilar(item))) {
                    return;
                }
                double startingY = e.getEntity().getLocation().getY();
                Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
                    if(e.getProjectile().getLocation().getY() >= startingY + 55) {
                        if(!e.getEntity().getEquipment().getItemInMainHand().isSimilar(item)) {
                            e.getEntity().sendMessage(ChatColor.GREEN + "> keep the bow in your main hand to bring the rain");
                        }

                        e.getProjectile().remove();
                        e.getEntity().getWorld().setStorm(true);
                        e.getEntity().getWorld().setWeatherDuration(3 * 60*20); // n tick-minutes = n [minutes] * 60 [seconds/minute] * 20 [ticks/second]
                        e.getBow().setAmount(0);
                        if(e.getEntity() instanceof Player) {
                            ((Player) e.getEntity()).playSound(e.getEntity(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 2, 2);
                        }
                    }
                }, 40);

            })
        );
    }


}
