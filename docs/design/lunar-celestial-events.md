# Lunar Celestial Events: Design Document

**Status:** Prototype on `IdeationItems-Hydrogen`, branch `claude/project-thread-qektw3`, in `Events/CelestialLunarEvents.java`
**Scope:** Lunar events change what happens at night during particular moon phases. Only the **full moon** and the **new moon** are changed. The other six phases stay vanilla.

| Phase | Index | Event |
|---|---|---|
| Full moon | 0 | **Dangerous night.** Stronger, armored, enchanted mobs, and more of them. None of their gear drops. They all despawn shortly after daybreak. |
| New moon | 4 | **Peaceful night.** No hostile mobs spawn on the surface. Gold particles float in the sky above players. |
| Any other phase | 1–3, 5–7 | Vanilla |

Events only run at night (time 12542 to 23460) and only in overworld-type worlds. All numbers are set under `lunar-events` in `config.yml`, and `/reload-ideas` applies changes.

---

## 1. What vanilla already does

- **Phase:** `(fullTime / 24000) % 8`. Phase 0 is full moon and phase 4 is new moon. The phase changes at 06:00, so a whole night is always one phase.
- **Moon brightness by phase:** 1.0, 0.75, 0.5, 0.25, **0.0**, 0.25, 0.5, 0.75.
- **Swamp slimes:** their spawn chance scales with moon brightness. They spawn most on a full moon and **never on a new moon**. This is the precedent the new moon event builds on.
- **Regional difficulty:** the moon adds up to +0.25, scaled by brightness. The bonus is capped by world age and reaches its full value only after about 63 in-game days. On a new world it is 0. Regional difficulty drives how often mobs get armor and enchanted gear, spider potion effects on Hard, zombie reinforcements, and door breaking. The full moon event takes that idea much further, and it applies on any world.
- **Black cats:** on a full moon, cats that spawn in villages have a 50% chance to be black.

Nothing in vanilla changes the **number** of hostile mobs by moon phase, and nothing blocks hostile spawns. Both of those are new here.

---

## 2. Full moon: dangerous night

### 2.1 Stronger mobs
Every hostile mob that spawns from `NATURAL`, `JOCKEY`, `REINFORCEMENTS` or `PATROL` is **empowered**:

- **Armor (zombies and skeletons):** a full set, with the tier chosen by weight: chainmail 15, iron 45, diamond 30, netherite 10. Each piece gets Protection I to IV.
- **Zombie weapons:** a spear (50%) or a sword of the same tier (chainmail tier gets stone). Sharpness I to V, and a 25% chance of Fire Aspect. Drowned keep their trident.
- **Skeleton bows:** Power I to V, a 30% chance of Punch, and a 20% chance of Flame.
- **Health:** max health ×1.5, for every hostile mob, including creepers and spiders.
- **No loot from gear:** every equipment slot gets drop chance 0, so the mobs are harder to kill but never hand out their armor. Normal mob drops such as bones and rotten flesh are unchanged.

Mob enchants use vanilla enchantments, which work on mobs without the plugin's custom enchant system. Because the gear never drops, it never reaches that system.

### 2.2 More mobs
- The world's monster spawn cap goes from 70 to 105 (×1.5) for the night, and goes back at daybreak.
- **Bonus spawns:** each natural hostile spawn has a 25% chance to bring one extra group, chosen by weight:
  - **Chicken jockey:** a baby zombie on a chicken.
  - **Spider jockey:** a skeleton on a spider.
  - **Zombie horseman:** a zombie with a spear on a zombie horse.
  - **Skeleton variant:** a stray, bogged or parched skeleton.
- **Skeleton variants anywhere:** a plain skeleton has a 50% chance to spawn as a stray, bogged or parched skeleton instead, in any biome. Variant names are looked up at runtime, so a variant that doesn't exist on the server's version is skipped instead of crashing.

### 2.3 Custom mob replacement hook
`fullMoonReplacements` is a map from a vanilla mob type to a list of entries, each with a chance out of 1000 and a custom mob. When a matching vanilla mob spawns naturally, the entries roll in order. The first one that hits cancels the vanilla spawn and summons the custom mob in its place. The map is empty for now. Example:

```java
put(EntityType.SPIDER, List.of(new FullMoonReplacement(100, SpitterSpider::new)));
```

Custom mobs placed this way are tagged too, so they despawn at daybreak.

### 2.4 Daybreak cleanup
- Every mob the event spawned or empowered (including the chickens and horses that jockeys ride) gets a `lunar_mob` tag in its PersistentDataContainer.
- **45 seconds after daybreak** (configurable), every tagged mob in the world is removed with a puff of smoke, and a chat message plays. If a new full moon night has started by then, for example through `/time set`, the cleanup is skipped.
- **Unloaded chunks:** tagged mobs in chunks that were not loaded at daybreak are removed when the chunk loads again (`EntitiesLoadEvent`). The same check runs once at server start, so a restart in the middle of a full moon doesn't leave armored mobs around.
- Skipping the night by sleeping counts as daybreak.

### 2.5 Messages
- Dusk: "**The full moon rises.** Something stronger walks tonight..."
- Cleanup: "The full moon sets, and its creatures fade with the dawn."

---

## 3. New moon: peaceful night

### 3.1 No hostile surface spawns
`CreatureSpawnEvent` is cancelled when all of these are true:
- the world is in a new moon night;
- the mob is hostile (`Enemy`, which covers monsters, slimes, phantoms and so on);
- the spawn is at or above **sea level − 30** (so Y 33 and up on a default world). The 30 blocks of margin stop drowned spawning in the ocean, and caves below that stay vanilla;
- the spawn reason is `NATURAL`, `JOCKEY`, `PATROL`, `REINFORCEMENTS` or `TRAP` (configurable).

Spawners, trial spawners, raids, spawn eggs, commands and the plugin's custom mobs are **not** blocked by default.

### 3.2 Gold sky particles
For every player at or above the same sea level − 30 line, about 4 particles a second appear at random points in a cylinder above them: radius 18, from 4 to 25 blocks above the player. Only that player sees their own particles. The default particle is `firefly` (glowing yellow, drifting). If that particle doesn't exist on the server, it falls back to gold dust.

### 3.3 Messages
- Dusk: "*The sky is moonless. The night is still.*"

---

## 4. Testing

- `/lunar` shows the phase, the time, and the active event.
- `/lunar full`, `/lunar new` and `/lunar off` force or clear an event in your world. Events still only run at night, so use `/time set night`.
- This needs the `ideation.debug` permission.

---

## 5. Open questions for cj

Each one has a default in the prototype that can be changed.

1. **Spawners on a new moon.** Should a surface mob spawner (a dungeon that is exposed, or a mob farm) keep working? *Default: yes.*
2. **Structure spawns on a new moon.** Witch huts, pillager outposts and ocean monuments use `NATURAL`, so they are blocked when above the line. Is that right? *Default: blocked.*
3. **Mobs already alive at new-moon dusk.** Should hostile mobs that are already on the surface be removed when the new moon rises? *Default: left alone.*
4. **Underground on a full moon.** The full moon empowers cave spawns too. Should it be surface only, using the same sea level − 30 line? *Default: everywhere.*
5. **Despawn delay.** The prototype uses 45 seconds. Is 30, 45 or 60 better?
6. **Gear tiers and enchant levels.** Is netherite at 10% too much? Should armor get Thorns or Projectile Protection as well as Protection?
7. **The third skeleton variant.** I took "parched" (the desert skeleton) as the third variant next to strays and bogged. Is that the one you meant?
8. **Particle look.** Firefly, or gold dust? Should other players see them too?
9. **Rewards.** Should full moon mobs drop extra XP or a custom item, so the danger pays off?
10. **Phantoms.** Phantoms are blocked on a new moon, but insomnia still builds up, so they come back the next night. Is that OK?

---

## 6. Prototype notes

- All the logic is in `Events/CelestialLunarEvents.java`. The only other changes are registering it in `Main`, the `/lunar` command in `plugin.yml`, and the `lunar-events` block in `config.yml`.
- The prototype **has not been compiled**: the build sandbox can't reach the Paper or Spigot repositories. Spots to check first, where the API may have moved: `Attribute.MAX_HEALTH`, the `AttributeModifier(NamespacedKey, …, EquipmentSlotGroup)` constructor, `Zombie#setBaby()`, `Registry.PARTICLE_TYPE`, and `BaseMob#getEntity().getBukkitEntity()`.
