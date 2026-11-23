---
title: "Morgul Wound"
---
*A cold blade's wound, that works its way toward the heart.* Strong undead can leave a **Morgul wound** that worsens in stages until it is cured. Left alone, it can kill, and dying won't rid you of it.

## How you're wounded

Each **melee hit** from an eligible mob has a **10% chance** to wound you. You can only carry one wound at a time.

- **With [LevelledMobs](https://modrinth.com/plugin/levelledmobs) on the server:** undead of level **30** or higher can wound.
- **Without it:** wither skeletons can wound.
- Either way, the **Servants of the Enemy** and **Ringwraiths** can always wound, and far more often: **50%** of their melee hits. They come for those who carry the [Rings of Power](rings-of-power.md#those-who-hunt-the-rings).

Undead are zombies and their kin, skeletons and their kin, the Wither, phantoms, zoglins, zombified piglins and undead horses. Server owners can change the chance and the level; see [Configuration](configuration.md#morgul-wound).

## The stages

The wound worsens every **3 minutes** you're online. Logging out pauses it.

| Stage | Effects |
|---|---|
| 1. A cold wound | Slowness I |
| 2. The chill spreads | Slowness I, Weakness I, Mining Fatigue I |
| 3. The shadow takes hold | As stage 2, plus Darkness and bouts of Wither I |
| 4. Fading | As stage 2, plus Darkness and Wither II until treated. **This can kill you.** |

Your action bar tells you when the wound worsens, and a faint grey smoke trails from you while you're wounded.

### Dying

Dying **doesn't** cure the wound. You come back with it, and its timer starts over.

- There's a 25% chance it eases by one stage. It never eases away completely: a stage 1 wound stays at stage 1.
- A stage 4 wound always eases to stage 3, so you don't respawn only to fade again.

## Treatment

| Treatment | Effect |
|---|---|
| **[Athelas](#athelas)** | Cures the wound completely |
| **[Miruvor](evenstar.md)** | Eases it by one stage and restarts the timer. A first-stage wound is cured |
| A [Healer](classes.md#healer)'s [Hands of the Healer](powers.md#hands-of-the-healer) | **Holds it back:** restarts the timer, so it takes a full stage longer to worsen. It doesn't cure it. Each wounded player can be held back once every 10 minutes, by any Healer |

## Athelas

*Kingsfoil. In the hands of the king it has a healing virtue.*

- **Finding it:** anyone breaking **ferns, large ferns or short grass** has a 1% chance to find a sprig. It doesn't drop in creative mode.
- **Using it on yourself:** right-click it to cure your wound.
- **Using it on someone else:** sneak and right-click a wounded player with it to cure theirs.
- Used on someone who isn't wounded, it gives Regeneration I for 5 seconds instead.
- Each use takes one sprig. It looks like a fern, but can't be planted or used in recipes.

## For server owners

The chances, level and timing can be changed, or wounds turned off, in [Configuration](configuration.md#morgul-wound).

A resource pack can give athelas its own look via `custom_model_data` string `athelas`.
