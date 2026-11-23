---
title: "Rings of Power"
---
Great was the strength of the Rings of Power, and anyone might take it up. But upon every one of them lay a curse; and in those who wear them the [Shadow](#shadow) grows.

## Finding a ring

The rings cannot be crafted. They lie hidden in the chests of ancient cities, of strongholds and of bastion remnants, for those who have the fortune to find them.

| Ring | Chance in each chest |
|---|---|
| Ring of the Seven | 3% |
| Ring of the Nine | 3% |
| Narya, Nenya, Vilya, the One Ring | 0.5% each, and only while that ring is not already in the world ([unique](#unique-rings)) |

## Wearing a ring

Take the ring in your off-hand and right-click to put it on, and right-click again to take it off; in the main hand it does nothing. If it is moved from your off-hand it is taken off, and no one can wear two at once.

## The rings

| Ring | Power while worn | Shadow each second |
|---|---|---|
| **Ring of the Seven** | [Ores](lore-and-items.md#ores) yield as though your tool has 2 more levels of Fortune, and at times 1-3 gold nuggets besides. With each ore mined greed adds 1 Shadow, save to the Dwarves, who do not feel it. | 0.5 |
| **Ring of the Nine** | **Deathless:** a lethal hit leaves you with 1 heart, at the price of 40 Shadow. **Wraith-world:** monsters more than 8 blocks away do not perceive you. | 1.5 |
| **Narya**, the Ring of Fire | You have Fire Resistance, and your melee hits set your foes alight; and you and all players within 8 blocks have Strength I. | 0.25 |
| **Nenya**, the Ring of Adamant | You have Water Breathing, Dolphin's Grace and Resistance I, and your gear loses no durability. | 0.25 |
| **Vilya**, the Ring of Air | You have Speed I and Slow Falling, and every 5 seconds you and all players within 8 blocks are healed of 1 heart of harm. | 0.25 |
| **The One Ring** | You have Strength II and Resistance I; monsters more than 6 blocks away do not perceive you; and you are Deathless, at the same price of 40 Shadow. | 2 |

Ores taken with Silk Touch are not multiplied. Deathless is of no avail against the void or `/kill`. And though monsters may not perceive you, one that you have struck can still strike back.

## Shadow

Shadow is numbered from 0 to 100, and it is not lost by logging out. While a ring is on, your action bar shows how far it has grown.

| Cause | Change |
|---|---|
| While a ring is on | It rises at the rate of that ring. In Hobbits it rises at half the rate, and greed likewise. |
| While no ring is on | It falls by 1 every 10 seconds, so that from 100 to 0 is about 17 minutes. |
| [Miruvor](evenstar.md#miruvor) | It falls by 15. |
| [Longbottom Pipe](mathom.md#longbottom-pipe) | It falls by 5, or by 10 from the pipe of a Hobbit. |

| Shadow | What befalls |
|---|---|
| 25 and above | Whispers come to your ear. |
| 50 and above | Hunger comes upon you, and now and then Darkness. |
| 75 and above | You suffer Wither I while the ring is on; and if you take damage with the ring in your off-hand, there is a 20% chance that it puts itself on. |
| 100 | The ring [betrays](#betrayal) you. |

### Betrayal

When the Shadow is full the ring forsakes its bearer. It drops from your hand, and may be found as far as 16 blocks away; Wither II is upon you for 10 seconds, and your Shadow falls to 50. Then two **Servants of the Enemy** come, in the form of wither skeletons. But if it was the One Ring that betrayed you, three **Ringwraiths** come in their stead, wither skeletons clad in black and riding skeleton horses.

For 2 minutes they hunt you, and then they vanish, and they leave nothing behind when slain. Half of their melee hits leave a [Morgul wound](morgul-wound.md).

## Those who hunt the rings

### The new moon

On nights of the new moon the Black Riders are abroad in the Overworld, and they seek those who carry Narya, Nenya, Vilya or the One Ring anywhere in their inventory. The Seven and the Nine do not draw them.

| | If the ring is carried | If the ring is worn |
|---|---|---|
| Chance each minute | 3% | 10% |
| Chance over a whole night | About 1 in 4 | About 2 in 3 |

- They come no more than once in each new moon: 3 Ringwraiths, or 5 for the One Ring.
- They appear 32-48 blocks away and out of sight, and ride toward you; and when they are within 16 blocks they attack. Your action bar gives warning: *The Black Riders are abroad…*
- They depart at dawn, or after 5 minutes. If you carry the ring no longer, they lose your trail after 30 seconds.
- From them the wraith-world does not hide you. They leave nothing behind when slain, and half of their melee hits leave a [Morgul wound](morgul-wound.md).

The new moon returns every 8 days, four days after the full.

### The One Ring in the Nether

When the One Ring is carried into the Nether, whether it is worn or not, the Eye is drawn to it, and your action bar reads *The Eye is turned upon you.*

| Peril | Effect |
|---|---|
| The burden | You have Slowness II for as long as you remain there with the ring. Hobbits bear it better, and have Slowness I. |
| The Eye | Shadow rises by 0.5 each second, and by the ring's own rate besides if it is worn. In Hobbits it rises by half. The wraith-world does not hide you. |
| The stirring of the Nether | Every 5 seconds the hostile creatures within 24 blocks turn upon you: piglins and brutes, whether you wear gold or no, ghasts, blazes, wither skeletons, hoglins and magma cubes. Zombified piglins do not heed it. |
| The Nazgûl | Every 3 minutes there is a 30% chance that 2 Ringwraiths are sent, from 24-32 blocks away. They hunt as on the new moon, and turn back if you leave the Nether or lose the ring. |

Only at [Mount Doom](#mount-doom) may this be ended.

## Unique rings

Of Narya, Nenya, Vilya and the One Ring there is but one each in the world. When one is dropped it does not despawn, and nothing can destroy it save Mount Doom; and if `/kill` or a plugin should remove it, it is set again where it lay.

A unique ring is lost when its item falls into the void, or when no player has carried it for 7 days; and then it may be found anew. Once that has happened, any older copy crumbles when someone picks it up, opens the chest where it lies, or logs in carrying it: *The ring is gone, as a shadow in the dusk…*

### Mount Doom

If the One Ring is cast into lava in the Nether, it is unmade. Then the Shadow of every player online falls to 0, and the whole server is told of it; and to the one who cast it in are given 30 levels' worth of experience. Thereafter the One Ring may be found again.

Fire alone cannot unmake it, and neither fire nor lava can unmake Narya, Nenya or Vilya.

## For server owners

- [Ring tracking, item-clearing plugins and loot refills](configuration.md#rings-of-power)
- [`/ardanborn give`](commands.md#ardanborn-give) can give any ring, and a unique ring so given becomes the current one.
- [`/ardanborn ring release`](commands.md#ardanborn-ring-release) returns a unique ring to the loot pool when it lies where none can reach it.

`custom_model_data`: `ring_<ring>`, e.g. `ring_one`, `ring_narya`
