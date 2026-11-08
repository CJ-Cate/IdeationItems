# Lunar Celestial Events — Design Document

**Status:** Draft, awaiting review
**Target:** Ideation Items (Spigot + NMS, Minecraft `1.21.10`)
**Scope:** Global changes to hostile mob spawning tied to the moon phase. Only the **full moon** and the **new moon** are changed; the other six phases stay vanilla.

---

## 1. Summary

| Phase | Index | Behaviour |
|---|---|---|
| Full moon | 0 | **Blood Moon (working name):** more hostile mobs, stronger mobs, higher effective regional difficulty |
| Waning gibbous → waxing gibbous (excluding new moon) | 1, 2, 3, 5, 6, 7 | Vanilla |
| New moon | 4 | **Still Night (working name):** no hostile mobs spawn naturally on the surface |

Both events run for one night per 8-day lunar cycle, so a player sees one of the two events roughly every 4 in-game days.

---

## 2. Vanilla moon mechanics (what we build on)

### 2.1 How the phase is determined
- There are 8 phases. `phase = (worldFullTime / 24000) % 8`. Phase 0 is full moon and phase 4 is new moon.
- The phase changes at time `0` (06:00, sunrise), so **a whole night is always a single phase**. There is no mid-night phase change to handle.
- Each world tracks time on its own. Only worlds with a sky that shows the moon (the Overworld and custom overworld-type worlds) are relevant. The Nether and the End have no moon.
- `/time set` changes the time of day but keeps the day count. `/time add` can move the phase. Both must be handled (see §6).
- Bukkit API: `World#getFullTime()` gives the absolute time. Spigot has no moon-phase enum, so we compute it ourselves. NMS has `Level#getMoonPhase()` and `Level#getMoonBrightness()` if we want to read it directly.

### 2.2 Moon brightness
Vanilla turns the phase into a "moon brightness" value that several systems read:

| Phase | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|---|---|---|---|---|---|---|---|---|
| Brightness | 1.00 | 0.75 | 0.50 | 0.25 | 0.00 | 0.25 | 0.50 | 0.75 |

### 2.3 Systems that already read the moon
1. **Swamp slimes.** Slimes spawn in swamps (and mangrove swamps) between Y 51 and 69 only when a random roll is under the moon brightness. They spawn most on a full moon and **never on a new moon**. This is the closest vanilla gets to "the new moon is safe", and it is the precedent this design expands on.
2. **Regional (local) difficulty.** The moon adds up to `+0.25` to the regional difficulty of every chunk:
   - `moonFactor = clamp(moonBrightness × 0.25, 0, worldAgeFactor)`, which is halved on Easy.
   - `worldAgeFactor` grows from 0 to 0.25 between about 3 and 63 in-game days of world age. **On a new world the moon has no effect**, because the cap is 0. On an established server (63+ days) a full moon gives the full +0.25.
   - Regional difficulty then drives, among other things:
     - the chance that zombies and skeletons spawn with armour, and the armour tier;
     - the chance of enchanted gear;
     - the chance that a mob can pick up items;
     - the chance that a spider spawns with a potion effect on Hard (Speed, Strength, Regeneration, Invisibility);
     - zombie reinforcement chance, and the chance that a zombie can break doors;
     - the chance that a skeleton horse trap spawns from lightning, and how strong the riders are.
3. **Black cats.** In villages on a full moon, there is a 50% chance a newly spawned cat is black.

Nothing in vanilla changes the **number** of hostile mobs by moon phase (apart from slimes), and nothing stops hostile spawns. Those two parts are new.

---

## 3. Full moon — "Blood Moon"

Goal: noticeably more dangerous than a normal night, but survivable for a geared player. Every number is in `config.yml` (§7).

### 3.1 Effects (proposed, all toggleable)
| # | Effect | How | Default |
|---|---|---|---|
| F1 | **More hostile mobs** | Raise the per-world monster spawn cap (`World#setSpawnLimit(SpawnCategory.MONSTER, n)`) and spawn more often (`World#setTicksPerSpawns(SpawnCategory.MONSTER, n)`). Restore both at dawn. | cap ×1.5 (70 → 105), ticks-per-spawn 1 (vanilla) |
| F2 | **Maximum moon factor regardless of world age** | Vanilla caps the moon's +0.25 by world age. On a full moon we treat the moon factor as the full +0.25 on any world. Via NMS, or by rolling gear and effects ourselves in `CreatureSpawnEvent` using a raised "effective difficulty". | on |
| F3 | **Extra difficulty bonus** | An extra `+X` to effective regional difficulty for spawn-time rolls (armour, enchantments, spider effects). | +0.5 |
| F4 | **Mob buffs** | On natural spawn, apply attribute modifiers: e.g. +20% max health, +10% movement speed, +2 follow range. Mark mobs with a PDC tag so the buffs are not stacked twice and can be found later. | on, small values |
| F5 | **No sunlight burning for Blood Moon mobs (optional, spicy)** | Zombies and skeletons that spawned during the Blood Moon do not burn at sunrise and despawn instead after N minutes. | **off** |
| F6 | **Announcements** | A chat line and sound at dusk ("The moon rises red…"), and an optional boss bar for the length of the night. | on |
| F7 | **Rewards (optional)** | Extra XP or loot drops from Blood Moon mobs, so the danger is worth it. A natural fit for Blueprint drops. | off |

### 3.2 Recommended v1
F1 + F2 + F6, with F3 and F4 at low values. F5 and F7 are follow-ups once the base feel is right.

### 3.3 Things to watch
- A higher spawn cap only changes the **count**. It does not change the light rules, so well-lit bases stay safe. That is intended.
- The spawn cap is per world but is split across loaded chunks around players, so on busy servers the effect per player is smaller. Consider scaling by player count.
- Slimes already spawn the most on a full moon. Leave that alone.

---

## 4. New moon — "Still Night"

Goal: **no hostile mobs spawn naturally on the surface, at all.** The world is calm for one night.

### 4.1 Core rule
Cancel `CreatureSpawnEvent` when **all** of these are true:
1. The world is an overworld-type world and is in phase 4 (new moon).
2. It is night (or, as an option, any time during the new-moon day; see open question Q5).
3. The entity is hostile: `entity instanceof Enemy`/`Monster`, or `SpawnCategory.MONSTER`. Slimes, phantoms, ghasts and so on should be checked by name because some are not `Monster`.
4. The spawn reason is in the **blocked reasons** list (default: `NATURAL`, plus the others in §4.2 marked "block").
5. The spawn location counts as **"surface"** (§4.3).

### 4.2 Spawn reasons: default proposal
| Spawn reason / source | Default | Reasoning |
|---|---|---|
| `NATURAL` (normal night spawns) | **Block** | The core of the feature |
| Phantoms (`NATURAL`, spawn in the air above sleepless players) | **Block** | They are a surface night threat. Insomnia still builds up |
| `PATROL` (pillager patrols) | **Block** | Surface threat that is not tied to a structure |
| `REINFORCEMENTS` (zombie calls for help) | **Block** | The parent can't spawn naturally anyway; this covers zombies left over from before the night |
| `JOCKEY` (spider/chicken jockeys) | **Block** | Part of a natural spawn |
| `LIGHTNING` / `TRAP` (skeleton horse traps) | **Block** | Surface threat during storms |
| `SPAWNER` (monster spawners) | **? Open (Q1)** | Usually underground. Blocking would also break mob farms |
| `TRIAL_SPAWNER` | **Allow** | Underground, player-started challenge |
| `RAID` | **Allow** | The player started it with Bad Omen |
| `BUILD_WITHER`, `BUILD_IRONGOLEM`, `SPAWNER_EGG`, `COMMAND`, `CUSTOM`, `DISPENSE_EGG` | **Allow** | Player or plugin intent (this includes our own `CUSTOM` mobs such as `AegisDummy`) |
| `SLIME_SPLIT`, `INFECTION`, `DROWNED`, `CURED` | **Allow** | Follow-ons from a mob that already exists |
| Structure spawns (witch huts, ocean monuments, pillager outposts, nether fortresses) | **? Open (Q2)** | Swamp huts and outposts are on the surface, but they are "places" not "the night" |
| Swamp slimes | Already 0 in vanilla | Nothing to do |
| Drowned in oceans and rivers | **? Open (Q3)** | Underwater, but under open sky |

### 4.3 What counts as "surface"
Options, in order of preference:
1. **Sky-light test (recommended):** `block.getLightFromSky() > 0` at the spawn location, or the location is at or above `World#getHighestBlockYAt(x, z)`. Cheap and matches what a player means by "outside". It also counts open-roof ravines and big caves open to the sky as surface.
2. **Y-level test:** Y ≥ sea level (63). Simple, but it blocks mountain caves and lets through low surface areas.
3. **Both:** sky access **or** Y above a set threshold.

Caves, deep dark and anything without a view of the sky stay vanilla. The new moon makes the **surface** safe, not the whole world.

### 4.4 Mobs already alive at dusk
Mobs that spawned before the new-moon night (in caves that open onto the surface, or that did not despawn from the previous night) would still walk around. Options:
- **A. Do nothing (default).** Vanilla despawning clears most of them fast.
- **B. Despawn hostile surface mobs at dusk** that are not name-tagged and not persistent (`isPersistent()` / `getRemoveWhenFarAway()`).
- **C. Make them passive** (cancel `EntityTargetLivingEntityEvent` against players on the surface). The repo already has an `InteractEffect_EntityTargetLivingEntityEvent` wrapper we could reuse.

### 4.5 Announcement
A quiet chat line at dusk ("The sky is moonless. The night is still."). No boss bar.

---

## 5. Architecture sketch

This is an outline for review, not an implementation.

- **`LunarEventManager`** (new, registered in `Main#onEnable`):
  - A repeating task (for example every 100 ticks) works out each world's phase and day or night state and keeps a `Map<World, LunarEvent>` of active events.
  - When a night starts or ends (time crosses about 12542 or 23460, the vanilla monster-spawn window), it calls `onStart(World)` and `onEnd(World)` on the event for that phase.
- **`LunarEvent`** interface with `FullMoonEvent` and `NewMoonEvent`. This leaves room for more phases later (see §8).
- **`LunarSpawnListener`**: one `CreatureSpawnEvent` handler at `HIGH` priority that hands off to the active event (block for new moon, buff for full moon).
- **State restore:** spawn limits and ticks-per-spawn changed in F1 are saved on start and restored on end **and** in `onDisable`, so a restart during a Blood Moon does not leave a world at 1.5× forever.
- **Commands** (admin, alongside `DebugCommands`): `/lunar status`, `/lunar force <full|new|off> [world]` for testing without skipping days.

---

## 6. Edge cases

| Case | Handling |
|---|---|
| Players sleep through the night | The event ends at dawn as usual. Sleeping is a valid way to skip a Blood Moon. Optional: make sleeping impossible during a Blood Moon (config) |
| `/time set` or `/time add` in the middle of the night | The repeating task notices the change on its next run and starts or ends events as needed. No state depends on event order |
| `doDaylightCycle false` | The phase is frozen. If it is frozen on a full or new moon, the event stays on for as long as it is night |
| Server restart during an event | Restore spawn limits in `onDisable`; on enable, the first task run starts the event again if it is still the right phase |
| Several overworld-type worlds | Each is tracked on its own |
| Thunderstorms (monsters can spawn in daytime under storms) | New moon: blocked by the same rule if Q5 = "whole day". Full moon: no change |
| Peaceful difficulty | Do nothing |

---

## 7. Proposed config (`config.yml`)

```yaml
lunar-events:
  enabled: true
  worlds: []                 # empty = all overworld-type worlds
  full-moon:
    enabled: true
    announce: true
    bossbar: true
    monster-spawn-cap-multiplier: 1.5
    force-max-moon-factor: true
    bonus-regional-difficulty: 0.5
    buffs:
      max-health-multiplier: 1.2
      speed-multiplier: 1.1
    no-sunburn: false
    prevent-sleep: false
  new-moon:
    enabled: true
    announce: true
    window: night            # night | full-day
    surface-test: sky-light  # sky-light | y-level | either
    surface-min-y: 63        # used by y-level / either
    blocked-reasons: [NATURAL, PATROL, REINFORCEMENTS, JOCKEY, LIGHTNING, TRAP]
    block-spawners: false
    block-structure-spawns: false
    existing-mobs: none      # none | despawn | passive
```

---

## 8. Open questions for cj

The original request was cut off at *"So if there's a mob spawning…"*. These are my guesses at what that sentence was about. Each has a proposed default above that can be changed.

- **Q1. Spawners on the new moon.** If there's a mob spawner on the surface (or any spawner), should it still work? Default: spawners still work, so mob farms keep running.
- **Q2. Structure spawns.** Witch huts and pillager outposts are on the surface. Should they go quiet on the new moon too? Default: they keep spawning.
- **Q3. Drowned and water mobs.** Do oceans and rivers count as "surface"? Default: yes if under open sky, so drowned are blocked.
- **Q4. Mobs already alive.** Should hostile mobs that already exist when the new moon rises be removed, made passive, or left alone? Default: left alone.
- **Q5. Daytime of the new-moon day.** Should storm spawns during the daytime of a new-moon day also be blocked, or only the night itself? Default: night only.
- **Q6. Underground on the full moon.** Should the Blood Moon buffs apply to cave spawns too, or only the surface? Default: everywhere, since the spawn cap is global.
- **Q7. Custom mobs.** Should our own custom mobs (`CustomMobs/*`) be able to spawn naturally on a Blood Moon as part of the danger?
- **Q8. Names.** Are "Blood Moon" and "Still Night" OK as player-facing names?

---

## 9. Future extensions
- Seasonal or rare events: a "Harvest Moon" (a full moon at a set day count) with greater danger and greater loot.
- Using the other six phases with small effects, so the cycle feels continuous (for example scaling F1 by moon brightness).
- Lunar-themed Blueprint items: a "Moonstone" that only charges under a full moon, or a charm that brings a Still Night to the area around the player.
