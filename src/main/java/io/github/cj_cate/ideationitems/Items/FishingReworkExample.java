package io.github.cj_cate.ideationitems.Items;

import io.github.cj_cate.ideationitems.ItemMaps;
import io.github.cj_cate.ideationitems.Items.Backend.ItemClass;
import io.github.cj_cate.ideationitems.Items.Backend.WeightedRandomBag;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;


public class FishingReworkExample extends ItemClass implements Listener
{
    /**
     * This is an example of how to re-work an existing vanilla system. It also demonstrates how to use the
     * weighted bags, rather in-depth.
     */
    private final Random rand = new Random(System.currentTimeMillis());

    private ItemStack luckPotion;
    public FishingReworkExample() {
        // Example adding a custom item to the fishing pool
        luckPotion = new ItemStack(Material.POTION);
        PotionMeta pm = (PotionMeta) luckPotion.getItemMeta();
        pm.addCustomEffect(new PotionEffect(PotionEffectType.LUCK, 7*60*20, 1, false, true, true), false);
        luckPotion.setItemMeta(pm);
    }

    @EventHandler
    public void catchFishEvent(PlayerFishEvent e) {

        if(!e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) return;

        e.setExpToDrop(0);

        Player p = e.getPlayer();
        int r = rand.nextInt(91); // [0,90]

        // Do all the changes to r here

        // bonuses to r
        // bait logic
        ItemStack offhanditem = e.getPlayer().getInventory().getItemInOffHand();
        if(!offhanditem.getType().equals(Material.AIR)) {

            switch(TagUtil.getCustomValue(offhanditem)) {

                // These are commented because they are not implemented, simply examples.
//                case "custom_spider_eye" -> r += 4;
//                case "custom_fermented_eye" -> r += 8;

                // Case for vanilla items
                case "null" -> {
                    switch(offhanditem.getType()) {
                        case SPIDER_EYE -> {
                            r += 10;
                            offhanditem.setAmount(offhanditem.getAmount() - 1);
                        }
                        case FERMENTED_SPIDER_EYE -> {
                            r += 20;
                            offhanditem.setAmount(offhanditem.getAmount() - 1);
                        }
                    }
                }
            }
        }

        // Luck potion
        if(e.getPlayer().hasPotionEffect(PotionEffectType.LUCK)) {
            r += e.getPlayer().getPotionEffect(PotionEffectType.LUCK).getAmplifier() * 15;
        }

        // Detriments to r
        // Closed water
        if(!e.getHook().isInOpenWater()) {
            r -= 15;
            e.getPlayer().playSound(p, Sound.ENTITY_DROWNED_HURT_WATER, SoundCategory.MASTER, 0.5f, 2f);
        } else {
            e.getPlayer().playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.3f, 1f);
        }

        // No skylight
        if(!e.getHook().isSkyInfluenced()) {
            r /= 2;
        }


        // Roll the item!
        ItemStack drop;
        if(r <= 23) {
            // trash bag
            drop = trashBag(e).get();
        } else if (r <= 93) {
            // normal bag
            drop = normalBag(e).get();
        } else {
            // treasure bag
            drop = treasureBag(e).get();
        }

        /**//**//**//**//**//**//**//**/

        // Error handling
        if(drop == null) {
            e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "you broke something :(, report please");
            e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "state: " + e.getState() + ", caught: " + e.getCaught());
            throw new NullPointerException("Fishing drop is null");
        }

        // Set the caught thing to the drop
        ((Item) e.getCaught()).setItemStack(drop);

        // (Optional, but I like it) Send the player information on what the r-value was, +if it was open water
        e.getPlayer().spigot().sendMessage(
                ChatMessageType.ACTION_BAR, new TextComponent("r:" + r +
                ", caught " + e.getCaught().getName().toLowerCase() +
                ", open_water: " + e.getHook().isInOpenWater()));
        e.getHook().setHookedEntity(e.getCaught());


    }

    @EventHandler
    public void dropMoreSpiderEyes(EntityDeathEvent e) {
        if(e.getEntityType() == EntityType.SPIDER && TagUtil.isNotSpawnerMob(e.getEntity())) {
            e.getDrops().add(new ItemStack(Material.SPIDER_EYE, rand.nextInt(1,4)));
        }
    }

    /*
    Things you can check for check:
    - Rod
    - Bait
    - Biome
    - - World (nether, end, overworld, other)
    - Luck
    - Weather
    - Sky access
    - Proper fishing spot
    - Elevation (surface, sky, underground, etc.)
     */

    // Run logic to determine the item pool of the player
    private WeightedRandomBag<ItemStack> normalBag(PlayerFishEvent e) {
        WeightedRandomBag<ItemStack> bag = new WeightedRandomBag<>();
        Player p = e.getPlayer();

        switch(p.getWorld().getBiome(p.getLocation())) {
            case JUNGLE, BAMBOO_JUNGLE, SPARSE_JUNGLE -> {

                bag.add(new ItemStack(Material.TROPICAL_FISH), 3);
                bag.add(new ItemStack(Material.PUFFERFISH), 0.5);

                bag.add(new ItemStack(Material.JUNGLE_SAPLING), 0.3);
                bag.add(new ItemStack(Material.VINE, rand.nextInt(1,3)), 0.5);
                bag.add(new ItemStack(Material.REDSTONE), 1);
                bag.add(new ItemStack(Material.BAMBOO_SAPLING), 1);
            }
            case TAIGA, SNOWY_TAIGA, OLD_GROWTH_PINE_TAIGA, OLD_GROWTH_SPRUCE_TAIGA, FROZEN_RIVER -> {

                bag.add(new ItemStack(Material.SALMON), 3);
                bag.add(new ItemStack(Material.COD), 1);

                bag.add(new ItemStack(Material.SNOWBALL), 0.5);
                bag.add(new ItemStack(Material.SWEET_BERRIES), 1);
                bag.add(new ItemStack(Material.SPRUCE_SAPLING), 1);
                bag.add(new ItemStack(Material.ICE, rand.nextInt(2,5)), 1);
            }
            case DESERT, SAVANNA, SAVANNA_PLATEAU, WINDSWEPT_SAVANNA,
                 BADLANDS, ERODED_BADLANDS, WOODED_BADLANDS -> {

                bag.add(new ItemStack(Material.COD), 3);
                bag.add(new ItemStack(Material.ACACIA_SAPLING), 0.5);
                bag.add(new ItemStack(Material.GOLD_NUGGET, rand.nextInt(3,7)), 1);
                bag.add(new ItemStack(Material.DEAD_BUSH), 0.5);
                bag.add(new ItemStack(Material.BONE_MEAL, rand.nextInt(1,2)), 0.5);
            }
            default -> {
                bag.add(new ItemStack(Material.COD), 3);
                bag.add(new ItemStack(Material.SALMON), 2);
                bag.add(new ItemStack(Material.PUFFERFISH), 1);
                bag.add(new ItemStack(Material.TROPICAL_FISH), 0.2);
            }

        }

        return bag;
    }

    private WeightedRandomBag<ItemStack> treasureBag(PlayerFishEvent e) {
        WeightedRandomBag<ItemStack> bag = new WeightedRandomBag<>();
        Player p = e.getPlayer();

        bag.add(new ItemStack(Material.RAW_IRON, rand.nextInt(1, 5)), 1);
        bag.add(new ItemStack(Material.COPPER_INGOT, rand.nextInt(2,7)), 1);
        bag.add(ItemMaps.getItem("ivory_1", rand.nextInt(1,4)), 1);
        bag.add(ItemMaps.getItem("diamond_upgrade_core"), 1);
        bag.add(new ItemStack(Material.SADDLE), 1);
        bag.add(luckPotion, 1);

        if(p.getWorld().hasStorm() && e.getHook().isSkyInfluenced()) {
            bag.add(new ItemStack(Material.TURTLE_SCUTE), 1);
            bag.add(new ItemStack(Material.NAUTILUS_SHELL), 1);
        }

        return bag;
    }

    private WeightedRandomBag<ItemStack> trashBag(PlayerFishEvent e) {
        WeightedRandomBag<ItemStack> bag = new WeightedRandomBag<>();
        Player p = e.getPlayer();

        int r = rand.nextInt(1, 3);
        bag.add(new ItemStack(Material.TRIPWIRE_HOOK), 1);
        bag.add(new ItemStack(Material.STICK, r), 1);
        bag.add(new ItemStack(Material.STRING, r), 1);
        bag.add(new ItemStack(Material.INK_SAC, r), 1);
        bag.add(new ItemStack(Material.BONE, r), 1);
        bag.add(new ItemStack(Material.BOWL), 1);
        bag.add(new ItemStack(Material.LEATHER, r), 1);
        bag.add(new ItemStack(Material.ROTTEN_FLESH), 1);
        bag.add(new ItemStack(Material.SPIDER_EYE, r), 1);

        switch(p.getWorld().getBiome(p.getLocation())) {
            case JUNGLE, BAMBOO_JUNGLE, SPARSE_JUNGLE -> {
                bag.add(new ItemStack(Material.COCOA_BEANS, rand.nextInt(1,5)), 1);
            }
        }

        return bag;
    }


}
