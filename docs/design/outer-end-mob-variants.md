# Outer End Mob Variants: Design Document

**Status:** Design only. Nothing is implemented yet.
**Part of:** the light End overhaul (custom structures, custom mobs, custom dragon fight, space helmet, rising ambient particles). This doc covers the mobs only.
**Scope:** Six variants of the vanilla ender mobs (endermen, endermites, shulkers). They spawn **only in the outer End islands** and never on the main dragon island.

| Variant | Base mob | How it appears | Threat in one line |
|---|---|---|---|
| Void Stalker | Enderman | 15% of outer enderman spawns | Moves only when you look away, then teleports behind you |
| Pilferer | Enderman | 8% of outer enderman spawns | Steals your space helmet and runs |
| Displacer | Enderman | 10% of outer enderman spawns | Its hits teleport you a few blocks |
| Chorus Mite | Endermite | Bursts out of broken chorus plants | Small swarm, bites can blink you |
| Warden Shulker | Shulker | Placed by the new structures, and rarely on island undersides | Blinds you and calls every enderman nearby |
| Ender Elder | Enderman | 1% of outer enderman spawns, or one per large structure | Mini-boss with mite summons and thrown end stone |

All numbers here are first guesses for a `outer-end-mobs` block in `config.yml`, so `/reload-ideas` can tune them.

---

## 1. What vanilla already does

- **Outer islands:** everything about 1,000 blocks or more from (0, 0) in the End. The main island and the gap around it use the biome `the_end`. The outer islands use `end_highlands`, `end_midlands`, `end_barrens` and `small_end_islands`.
- **Natural End spawns:** endermen are the only mob that spawns naturally in the End, in groups of up to 4. Shulkers only come from End City structure generation, and endermites only come from thrown ender pearls (5%).
- **So the outer islands feel empty.** Apart from End Cities, the only thing out there is endermen. That gives this design room: replacing a share of enderman spawns adds variety without raising mob density.

---

## 2. Shared rules for every variant

### 2.1 Where they spawn
A spawn counts as "outer End" when all of these are true:
- the world's environment is `THE_END`;
- the biome at the spawn block is **not** `the_end`;
- the horizontal distance from (0, 0) is at least **1,000** blocks (a safety check in case a datapack or the structures work adds biomes).

### 2.2 How they spawn
- **Enderman variants** replace vanilla ones. On `CreatureSpawnEvent` for an enderman with reason `NATURAL`, roll the weight table in section 1. If a variant hits, the enderman is turned into that variant in place (see 2.3), so the spawn cap and density stay vanilla. Only one roll per spawn, and only the first enderman of a group can become an Ender Elder.
- **Chorus Mites** spawn from `BlockBreakEvent` on chorus plants (section 6).
- **Warden Shulkers** are placed by the structures work and, rarely, by chunk generation on island undersides (section 7).
- Spawners, spawn eggs, commands and `/endmob summon` always work, anywhere, for testing.

### 2.3 How a variant is built
Recommendation: **decorate a vanilla mob instead of subclassing it in NMS.**

The `CustomMobs` package today subclasses NMS mobs (`AegisDummy extends IronGolem implements BaseMob`). That works for short-lived mobs like the Aegis golems, which are removed after 5 seconds. It does not work well for mobs that live in the world: an NMS subclass is saved to disk as a plain vanilla enderman, so after a chunk unload or a restart the custom behaviour is gone.

So each variant is a vanilla mob with:
- an `outer_end_variant` key in its PersistentDataContainer, holding the variant id (`void_stalker`, `pilferer`, and so on). The PDC is saved with the mob, so it survives unloads and restarts;
- attributes set once at spawn (max health, scale, speed, attack damage);
- behaviour driven by listeners and one shared repeating task that checks tagged mobs near players;
- on `EntitiesLoadEvent`, tagged mobs are re-registered with that task.

Paper's mob goal API (`Bukkit.getMobGoals()`) can add or remove AI goals on a vanilla mob without NMS, if a variant needs real goal changes. The Void Stalker is the only one that might.

### 2.4 How players tell them apart
Entities can't be retextured without a resource pack trick, so each variant is marked by things the server controls:
- **the block it carries** (endermen hold a block, and it shows in their hands);
- **scale** (the `scale` attribute);
- **ambient particles** around it;
- a distinct **sound** cue before its special move.

No name tags, so the outer End keeps its eerie feel. Open question 1 asks if you want names.

### 2.5 Fairness rules for the void
The outer islands are surrounded by the void, so any move that pushes, pulls or teleports a player must check it is safe:
- a teleport target must have a solid block under it and two free blocks of air above;
- no variant pushes, pulls or teleports a player who is gliding with an elytra;
- if no safe target is found in 8 tries, the move does nothing.

---

## 3. Void Stalker (enderman)

**Idea:** the reverse of a normal enderman. A normal enderman gets angry when you look at it. The Stalker only moves when you **aren't** looking at it.

- **Look:** carries **crying obsidian**. Scale 1.1. Slow purple `portal` particles drift off it, about one every half second.
- **Behaviour:**
  - While a player within 24 blocks is looking at it (the same eye check vanilla uses), it freezes in place: no walking, no teleporting.
  - When that player has looked away for **3 seconds**, it teleports to a safe block **2 to 3 blocks behind** them and attacks. A low `enderman.stare` sound plays half a second before it lands, so a player who turns fast can catch it.
  - Staring at it never angers it. Paper's `EndermanAttackPlayerEvent` is cancelled for Stalkers.
  - After each hit it teleports away again (vanilla enderman retreat) and restarts the 3 second timer.
- **Stats:** health 50 (vanilla 40), damage 8 (vanilla 7).
- **Drops:** vanilla enderman drops, plus 1 to 2 crying obsidian.

## 4. Pilferer (enderman)

**Idea:** a thief that targets the space helmet. It ties the mobs into the breathing mechanic, so a fight in the outer End has a clock on it.

- **Look:** carries **nothing** until it steals, then carries **glass**, which looks like the stolen helmet. Scale 0.9, so it reads as smaller and quicker. Faint `reverse_portal` particles.
- **Behaviour:**
  - Neutral. It never attacks.
  - It sneaks toward players who are wearing the space helmet. When it is within 2 blocks **behind** a player, it plays an `amethyst_block.chime` sound and, **1 second later**, takes the helmet off their head if they haven't turned to face it.
  - It then teleports 12 to 20 blocks away, on the same island where possible, and keeps teleporting away whenever a player gets within 6 blocks. While it holds the helmet, it gives off a trail of `item_snowball`-style glass shards so it can be followed.
  - The player starts losing air at once, under the space helmet rules.
  - Killing it drops the **exact helmet item** it took (the item is stored in the Pilferer's PDC, so it survives restarts). If it despawns or falls into the void, the helmet is dropped at the player's feet instead, so it is never lost for good.
- **Limits:** it won't steal from a player who is gliding, who is within 4 blocks of an island edge, or who was robbed in the last 60 seconds.
- **Stats:** health 24, speed ×1.3.
- **Drops:** vanilla enderman drops, plus the helmet if it holds one.

## 5. Displacer (enderman)

**Idea:** every hit moves you, so footing matters.

- **Look:** carries **purpur block**. Scale 1.0. A ring of `witch` particles around its feet.
- **Behaviour:**
  - Hostile when stared at, like vanilla.
  - Each melee hit teleports the player **4 to 8 blocks** in a random direction, like eating chorus fruit, using the fairness rules in 2.5. The teleport plays the chorus fruit sound.
  - Every third hit, it teleports **itself** to the player's old spot, so the fight keeps moving.
- **Stats:** health 40, damage 5 (lower than vanilla, because the displacement is the danger).
- **Drops:** vanilla enderman drops, plus 1 to 3 popped chorus fruit.

## 6. Chorus Mite (endermite)

**Idea:** chorus forests become something to be careful in, not just a fruit farm.

- **Spawn:** when a player breaks a chorus plant or chorus flower in the outer End, there is a **4% chance** that 3 to 5 mites burst out of it. Breaking a whole tree rolls once per block, so tall trees are riskier.
- **Look:** vanilla endermite at scale 1.3. Purple `dragon_breath` particles when they bite.
- **Behaviour:**
  - Hostile to the player who broke the plant.
  - Each bite has a **15% chance** to blink the player 2 blocks (fairness rules apply).
  - They die on their own after **30 seconds**, with a puff of particles, so swarms don't build up.
- **Stats:** health 8, damage 2.
- **Drops:** 1 chorus fruit each, 25% chance.

## 7. Warden Shulker (shulker)

**Idea:** a lookout for the new structures. It turns a quiet structure into a fight if you're careless.

- **Spawn:** placed by the structures work (the embedded data pack can place a tagged shulker with a function or a structure block's entity data). As a fallback, chunk generation in the outer End has a 1 in 40 chance per chunk to place one on the underside of an island.
- **Look:** shulker with a **black** shell (a vanilla dye colour). Constant `squid_ink` particles.
- **Behaviour:**
  - Its bullets give **Darkness for 3 seconds** instead of Levitation. Levitation over the void is too punishing here.
  - The first time it is hit, it lets out a sound (`shulker.ambient` at low pitch) and **angers every enderman within 24 blocks** at the player who hit it, variants included.
  - It never teleports away from its spot, so it guards a fixed place.
- **Stats:** health 40 (vanilla 30).
- **Drops:** a shulker shell, 75% chance (vanilla is 50%).

## 8. Ender Elder (enderman, mini-boss)

**Idea:** a rare, readable mini-boss that makes a found structure feel earned. It is the place to hand out a material for the overhaul.

- **Spawn:** 1% of outer enderman spawns, at most one within 128 blocks of another Elder. The structures work can also place one in each large structure.
- **Look:** carries **end stone**. Scale **1.6**. A boss bar named "Ender Elder" shows to players within 32 blocks.
- **Phases:**
  1. **Above 50% health:** melee and teleport, like a strong enderman. Every 10 seconds it throws its carried end stone as a falling block at the player, for 6 damage, then picks up a new one.
  2. **Below 50% health:** it summons **3 Chorus Mites** every 15 seconds (at most 6 alive) and teleports every 5 seconds, leaving an afterimage (a short-lived armor stand or display entity with particles) where it stood.
- **Stats:** health 150, damage 10, knockback resistance 0.5.
- **Drops:** a guaranteed **Void Shard**, a new custom item to use in recipes later, for example a better space helmet. Plus 20 to 30 XP.

---

## 9. Testing

- `/endmob summon <id>` spawns a variant at your feet.
- `/endmob list` shows every tagged variant loaded in the world, with location.
- `/endmob info` shows the variant you are looking at and its state, for example the Stalker's timer or the Pilferer's stored helmet.
- These need the `ideation.debug` permission, like `/lunar`.

---

## 10. Open questions for cj

Each one has a default above that can be changed.

1. **Names.** Should variants show a name tag, or stay unnamed and identified only by look? *Default: unnamed.*
2. **The Pilferer.** Is stealing the helmet fun or just mean? The other option is that it **cracks** the helmet so you have, say, 20 seconds of air left until you swap in a new one. *Default: steals it.*
3. **Spawn rates.** 15% Stalker, 8% Pilferer, 10% Displacer and 1% Elder means about a third of outer endermen are variants. Too many?
4. **Resource pack.** The repo already ships texture packs. Do you want real new textures for these mobs later (with item display models), or keep them vanilla-looking?
5. **The Void Shard.** Should the Elder's drop feed into the space helmet, the dragon fight, or something else?
6. **Main island.** Should any variant ever show up on the main island, for example during the dragon fight? *Default: never.*
7. **End Cities.** Should Warden Shulkers replace some vanilla shulkers in End Cities too? *Default: no, End Cities stay vanilla.*
8. **Light level.** Should the variants only spawn in the dark, so the new structures can use light to make safe rooms? *Default: no, same rules as vanilla endermen.*

---

## 11. Notes for the build

- The variants should live in their own class, for example `Events/OuterEndMobs.java` on Hydrogen, with one small class per variant behind a shared interface, so the spawn table, the repeating task and the load listener live in one place.
- The full moon design already plans a "custom mob replacement" map (vanilla type to chance and custom mob). The enderman replacement here is the same idea and can share that code.
- Things to check in the API first: `EndermanAttackPlayerEvent` (Paper), `Enderman#setCarriedBlock`, the `scale` attribute name on the server's version, `Shulker#setColor`, and `Bukkit.getMobGoals()`.
