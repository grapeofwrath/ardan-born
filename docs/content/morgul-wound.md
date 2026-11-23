---
title: "Morgul Wound"
---
Of all the hurts that the servants of the Enemy could deal, the Morgul wound was the most feared; for time did not heal it, but made it worse, and it sought ever toward the heart.

## How the wound is dealt

It is dealt by a melee hit, and by these mobs only; and no one can bear more than one such wound at a time.

| Mob | Chance with each hit |
|---|---|
| [Undead](lore-and-items.md#undead) of level 30 or higher, where [LevelledMobs](https://modrinth.com/plugin/levelledmobs) is installed | 10% |
| Wither skeletons, where it is not | 10% |
| [Servants of the Enemy and Ringwraiths](rings-of-power.md#those-who-hunt-the-rings) | 50% |

## The stages

With every 3 minutes that you are online the wound grows worse; but while you are logged out it sleeps.

| Stage | Effects |
|---|---|
| 1. A cold wound | Slowness I |
| 2. The chill spreads | Slowness I, Weakness I and Mining Fatigue I |
| 3. The shadow takes hold | All that came in the second stage, and with it Darkness, and bouts of Wither I |
| 4. Fading | All that came in the second stage, with Darkness and Wither II that do not cease until it is treated. This is lethal. |

As it worsens your action bar will give you warning, and a grey smoke goes behind all who are wounded.

### Dying

Death itself is no cure. The wounded return with the cold still in them, and the timer begins anew. Yet there is a 25% chance that the wound is eased by one stage, though never below the first; and one who has come to the fourth is always returned to the third.

## Treatment

| Treatment | Effect |
|---|---|
| [Athelas](#athelas) | The wound is wholly cured. |
| [Miruvor](evenstar.md#miruvor) | The wound is eased by one stage and the timer begins anew. A wound of the first stage is cured. |
| [Hands of the Healer](powers.md#hands-of-the-healer) | The timer begins anew, and so the wound is held back, but it is not cured. This may be done once every 10 minutes for each of the wounded, whoever the Healer may be. |

## Athelas

Athelas grows unregarded in wild places, and few remember the virtue that is in it. A sprig is found in 1% of the ferns, large ferns and short grass that are broken, though never in creative mode. It has the likeness of a fern, but it cannot be planted, nor used in any recipe, and each use consumes one sprig.

| Use | Effect |
|---|---|
| Right-click | Your own wound is cured. |
| Sneak and right-click another player | Their wound is cured. |
| Given to one who is not wounded | Regeneration I for 5 seconds. |

Those who keep a server may alter these things in the [Configuration](configuration.md#morgul-wound).

`custom_model_data`: `athelas`
