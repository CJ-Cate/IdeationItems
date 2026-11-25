# Raid-Style Ender Dragon Fight — Design Doc

> Status: **Design only.** Nothing here is implemented yet. Part of the light End overhaul (custom outer-island mobs, data-pack structures, space helmet, ambient particles).

## Goals

- Turn the dragon from "shoot crystals, wait for perch, hit with sword" into a **coordinated group encounter** inspired by Destiny 2 raids: clear mechanics, roles, a damage phase you earn, and a wipe condition.
- **Every player who meaningfully participated gets their own loot.** No last-hit stealing, no shared chest race.
- Stay **solo-possible but group-favored**: scales with player count, never needs a fixed fireteam size.
- Reuse what the plugin already has: `Blueprint` items for loot, `BaseMob` NMS mobs for adds, `ScoreboardUtil` teams for glow/roles, embedded data pack for the arena.

## Non-goals (for now)

- Replacing the vanilla first-kill flow (exit portal, egg, gateway). The raid fight is the **respawned** dragon, so the first kill stays vanilla and progression isn't broken.
- Final loot tables and item stats. Placeholders below.

---

## Core raid vocabulary (how D2 ideas map to Minecraft)

| Raid concept | In this fight |
|---|---|
| Encounter start / "ready check" | Players place 4 end crystals on the exit portal as vanilla, plus everyone in the arena gets a 10 s "Gathering" bossbar before the fight locks in. |
| Roles | Soft roles granted by picking up a **buff**, not chosen in a menu. Anyone can do any job. |
| Mechanic → DPS phase loop | Clear a mechanic to force the dragon to perch and become vulnerable ("damage phase"). Outside damage phase the dragon takes heavily reduced damage. |
| Enrage / wipe | Each damage phase you fail to trigger in time advances an **Enrage** counter. At max Enrage the dragon casts a wipe attack. |
| Revive tokens | Limited **Echoes**: a downed player can be revived by an ally in a short window; limited per phase. |
| Callouts | Randomized symbols that players must communicate (shown only to the player holding the buff). |

---

## Arena

- Central island, unchanged layout, but the **obsidian pillars get colored "Sigil" markers** (glazed terracotta or banners on top, one of 5 symbols). Placed via the embedded data pack as a structure template on fight start, removed after.
- **Outer ring platforms**: 3 small floating platforms ~40 blocks out at different heights, reachable by a temporary chorus/end-rod bridge that grows in during the Mechanic phase. Ties into the outer-island feel.
- **No space helmet needed on the central island** (it has "atmosphere" from the portal). The outer ring platforms **do** require the helmet. Forgetting it is a soft punishment, not an instant death. Cross-reference the helmet thread.
- The ambient upward particles get **faster and denser as Enrage rises**, giving a visual timer without UI.

---

## Phases

### Phase 0 — Gathering (10 s)
- Respawn crystals placed → bossbar "The Void Stirs". Everyone within 150 blocks of the portal is enrolled as a participant (see *Participation*).
- Late joiners can still enroll during Phase 1 by entering the arena.

### Phase 1 — Crystal Ward (mechanic → DPS loop, repeats ~3×)

**Mechanic:**
1. Dragon circles and is **Warded** (takes 10% damage, purple particle shell).
2. Pillar crystals respawn, each **shielded** and tagged with a Sigil symbol.
3. One random player gets the **Voidsight** buff (glowing, title message). Only they see a sequence of 3 Sigils (shown in their action bar).
4. The rest of the team must break the crystals **in that order**. Wrong order → crystal respawns and that player is slowed + gets *Void Rot* (damage over time).
5. Breaking a shielded crystal needs a melee hit from a player holding **Charged Shard** (see Runner role) or 3 arrows within 2 s from different players (encourages bow teamwork).

**Adds during the mechanic:** waves of outer-island ender variants (from the custom mobs thread) teleport onto the island. Killing a specific "Bearer" add drops a **Charged Shard**.

**Damage phase:** all 3 crystals broken in order → dragon perches, Ward drops for **20 s**. Players get *Unity* (+15% damage per nearby ally, capped). When it ends, the dragon screams, knocks everyone back, and the loop repeats with a new Voidsight player.

**Timer:** 90 s per mechanic. Miss it → Enrage +1, dragon does a **Breath Sweep** over the whole island, then a new loop starts.

### Phase 2 — The Outer Ring (at 50% HP)
- Dragon flies to the outer ring and **becomes untargetable** from the island.
- Bridges grow to the 3 outer platforms. Each platform has an **anchor** (a respawning shulker variant or a block structure) that must be destroyed **within 5 s of each other** — classic "simultaneous plates" raid check. Needs splitting into up to 3 groups; solo players get a longer window that scales with player count.
- Helmet required out there. Players who fall get caught by a slow-falling "void tether" once per phase, then die.
- Success → dragon crashes onto the central island for a **long damage phase (30 s)**.
- Failure → Enrage +1, bridges retract, try again after 30 s.

### Phase 3 — Final Stand (at 15% HP)
- No Ward. The dragon is always vulnerable, but every 15 s it marks one player with **Collapse**. That player must stand in the center portal before the timer runs out or the mark explodes on everyone near them.
- Basically a DPS check with one movement mechanic. Kill it before Enrage maxes out.

### Enrage & wipe
- Enrage goes from 0 to 3. Each level gives the dragon +10% damage and faster breath.
- At Enrage 3 the dragon casts **Void Collapse**: a 10 s countdown, then lethal damage to everyone in the arena. That's the wipe.
- On wipe: dragon despawns, crystals are not consumed (so the retry is free), Enrage resets, participants keep their enrollment for 5 minutes.

---

## Roles (soft, buff-driven)

| Role | How you get it | Job |
|---|---|---|
| **Seer** | Random assignment of *Voidsight* each Phase 1 loop | Calls out the Sigil order. Can't break crystals while holding the buff, which forces communication. |
| **Runner** | Pick up a *Charged Shard* from a Bearer add | Breaks shielded crystals; shard lasts 15 s. |
| **Add Clear** | Implicit | Keeps endermen variants off the Seer and Runners. |
| **Anchor team** | Phase 2 self-split | One group per platform. |

Scaling:
- 1 player: Voidsight is shown to the same player, shards last longer, Phase 2 window 20 s.
- 2–3 players: Phase 2 window 10 s.
- 4+ players: full rules. Dragon HP scales `base × (1 + 0.5 × (players − 1))`, capped at 8 players.

---

## Participation & loot (every participant gets loot)

**Enrollment:** any player in the End within 150 blocks of (0, 0) during Phase 0 or Phase 1.

**Counts as having participated** (any one of these):
- dealt damage to the dragon, a crystal, an add, or an anchor,
- completed a mechanic action (Seer callout phase survived, crystal broken, anchor destroyed),
- revived another player,
- was in the arena for ≥ 50% of the fight duration.

That last rule lets healers, support players, or players who are still learning get credit while blocking AFK players who show up at the last second.

**Tracking:** a per-fight `RaidSession` object holds `Map<UUID, ParticipationRecord>` (damage dealt, actions, time present). It isn't saved to disk, so a server restart mid-fight cancels the fight.

**Loot delivery:**
- On kill, each eligible player gets a **personal drop**: an item spawned only for them (other players can't pick it up) at their feet, or sent straight to a per-player **"Raid Cache" chest GUI** if their inventory is full. Prefer the cache so nothing gets lost in the void.
- Rolls are per player, using the existing `WeightedRandomBag`.
- **First clear per week** (per player) gets a guaranteed rare-pool roll. Later clears roll the common pool only. This mirrors D2 lockouts without blocking replays.
- **Triumph bonuses** (optional challenges, like "no one used an Echo" or "no wrong crystal order") add one extra roll for the whole team.

**Loot pool ideas (placeholders, all as Blueprints):**
- *Void-Touched Elytra*: elytra with a small boost on sneak, single-use per flight.
- *Seer's Lens*: helmet that counts as a space helmet AND shows nearby outer-island mob variants through walls.
- *Shard of the Ward*: offhand item, short damage-reduction bubble on use.
- *Dragon Scale*: crafting material for the above, guaranteed one per clear.
- Cosmetic: a dragon-breath trail particle (ties into the particle system).

---

## Technical notes (for when this gets implemented)

- **Detecting the raid fight:** listen for `EnderDragon` spawn in `THE_END` where `world.getEnderDragonBattle().hasBeenPreviouslyKilled()` is true. Wrap it in a `RaidSession`.
- **Dragon control:** drive phases using `EnderDragon.setPhase(...)` (`CIRCLING`, `LAND_ON_PORTAL`, `HOVER`, `FLY_TO_PORTAL`, etc.) rather than a custom NMS dragon at first. Move to an NMS subclass under `CustomMobs/` only if vanilla phases fight back too much.
- **Damage gating (Ward):** `EntityDamageEvent` on the dragon (and its `ComplexEntityPart`s). Scale damage by phase state.
- **Bossbar:** one `BossBar` per session showing HP + current mechanic + Enrage pips.
- **Glow/roles:** `ScoreboardUtil.addEntityToTeam` with colored teams for Seer / Runner glow.
- **Arena pieces:** structure templates in the embedded data pack (shared with the structures thread). Place and clear them with `/place template` or `StructureManager`.
- **Adds:** reuse the outer-island ender variants as `BaseMob` subclasses; the "Bearer" is a variant with a shard drop.
- **Personal loot:** store a `PersistentDataContainer` owner tag on dropped `Item` entities and cancel `EntityPickupItemEvent` for non-owners, or just open a cache GUI with `GuiUtil`.
- **Weekly lockout:** player PDC timestamp of their last rare-pool roll.

---

## Open questions for cj

1. Should the **first-ever kill** stay vanilla, or should the raid fight replace it too? (This doc assumes vanilla first kill.)
2. Is a **hard wipe** (everyone dies) too punishing on a survival SMP? An alternative is a "soft wipe" where everyone gets teleported to the outer edge with no death.
3. How much **voice/chat callout** reliance is OK? The Seer mechanic assumes players talk. A fallback could show the Seer's order as a chat message after 30 s.
4. Weekly lockout on rare loot: wanted, or should every clear be equal?
5. Dragon HP scaling cap: is 8 players right for your server size?
