package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.CustomMobs.AegisDummy;
import io.github.cj_cate.ideationitems.Items.Backend.Blueprint;
import io.github.cj_cate.ideationitems.Items.Backend.Categories;
import io.github.cj_cate.ideationitems.Items.Backend.InteractEffectClasses.InteractEffect_PlayerToggleSneakEvent;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class ExampleMobSpawning extends ItemClass
{

    // Mob-spawning Shield item
    public Blueprint makeAegis()
    {
        ItemStack item = ItemUtil.makeItem(Material.SHIELD, ChatColor.GOLD + "" + ChatColor.BOLD + "Aegis",
            new ArrayList<>(Arrays.asList(
                ChatColor.AQUA + "The legendary shield",
                ChatColor.AQUA + "of the ancient greeks"
            )));

        return new Blueprint(item, "aegis_shield", Categories.ARMOR,
            null, // Because this recipe is commented out, it is not accessible in-game by default
            /*new RecipeHolder(
                    RecipeType.SHAPED_RECIPE,
                    new String[]{
                            "III",
                            "ISI",
                            "III"
                    },
                    new MaterialCarrier('I', Material.IRON_BLOCK),
                    new MaterialCarrier('S', Material.SHIELD)),*/
            new InteractEffect_PlayerToggleSneakEvent(e -> {
                if(e.isSneaking() &&
                    e.getPlayer().isBlocking() &&
                    e.getPlayer().getInventory().getItemInOffHand().isSimilar(item))
                {
                    double radius = 2.0d; //radius (in blocks) to spread golems from the player
                    int amount = 5; //amount of dummy golems to spawn
                    double degrees = 120; //the arc of which golems will be spread

                    summonLocalAegisGolem(e.getPlayer(), radius, amount, degrees);
                }
            }));

    }

    /**
     * @param p Player to target
     * @param radius Radius (in blocks) to spread golems from the player
     * @param amount Amount of dummy golems to spawn
     * @param degrees The arc of which golems will be spread
     */
    private void summonLocalAegisGolem(Player p, double radius, int amount, double degrees) {
        double looking = p.getLocation().getYaw();
        double px = p.getLocation().getX();
        double py = p.getLocation().getY();
        double pz = p.getLocation().getZ();
        World pw = p.getWorld();

        double degree_space = degrees / (amount-1); // the space inbetween each golem in degrees
        double effective_starting_degree = 0 - (degrees / 2) + looking; // the effective starting spot for the 'first' golem to be placed

        Location loc = new Location(pw, px, py, pz);
        for (int i = 0; i < amount; i++) {
            // This is why you pay attention in math class
            double x = effective_starting_degree + (i*degree_space) + 90;
            loc.setX(px + (radius * Math.cos(x * Math.PI / 180 )));
            loc.setZ(pz + (radius * Math.sin(x * Math.PI / 180 )));
            new AegisDummy(loc).summon(p, (float) ((degree_space*i) - (degrees/2)));
        }
    }

}
