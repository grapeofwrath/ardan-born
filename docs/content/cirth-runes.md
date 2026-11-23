---
title: "Cirth Runes"
---
The Cirth are the runes of the Dwarves and the Elves of Beleriand. **Anyone** can craft a rune-stone, but only a **[Smith](classes.md#smith)** can inscribe one onto a weapon or tool. Bring your stones to a Smith.

Runes work for whoever holds the item. They aren't tied to any ancestry.

## Crafting rune-stones

Shaped recipe in a crafting table: an **amethyst shard** in the center, ingredient **A** in the corners and ingredient **B** on the edges. Every recipe shows in the recipe book.

| | | |
|:-:|:-:|:-:|
| A | B | A |
| B | Amethyst Shard | B |
| A | B | A |

| Rune | A (corners) | B (edges) |
|---|---|---|
| Westernesse | Bone | Gold Nugget |
| Warning | Glow Ink Sac | Lapis Lazuli |
| Frost | Blue Ice | Snowball |
| Delving | Coal | Raw Iron |
| Harvest | Wheat | Bone Meal |
| Vein | Redstone Dust | Iron Ingot |

Rune-stones look like glinting amethyst shards. They can't be used in any other recipe.

## Inscribing a rune

At a **smithing table**: the **rune-stone** in the template slot, the **weapon or tool** in the middle slot, and **one lapis lazuli** in the last slot.

- The result only appears for a Smith. For anyone else the result slot stays empty.
- The rune-stone and the lapis are used up.
- The item keeps its enchantments, damage, name and any race-item upgrade (for example, a runed sword can still become a [Scimitar of Isengard](white-hand.md), and keeps its rune).
- The runes are listed at the end of the item's tooltip.

### How many runes

| Item | Runes it can hold |
|---|---|
| Netherite items | 2, never two of the same |
| Everything else (any other tier, and bows, crossbows, tridents, maces) | 1 |

When an item is full, a new rune **replaces the oldest**. A diamond item with a rune keeps it when you upgrade it to netherite, and then has room for a second.

## The runes

| Rune | Goes on | Effect |
|---|---|---|
| **Westernesse** (the barrow-blades) | Swords, axes, spears, maces, tridents | ×1.3 melee damage against the undead |
| **Warning** (as Sting) | Swords, axes, spears, maces, tridents | While you hold it and a monster is within 16 blocks, your action bar reads *Your blade glows blue* and blue sparks show at the blade |
| **Frost** (the Helcaraxë) | Swords, axes, spears, maces, tridents, bows, crossbows | Hits give the target Slowness I for 2 seconds: melee hits, and arrows or thrown tridents |
| **Delving** | Pickaxes | Iron, copper and gold ore (and their deepslate forms) drop ingots instead of raw metal. Fortune still multiplies them |
| **Harvest** | Hoes | Right-click a ripe crop: every ripe crop in the 3×3 around it is reaped and replanted from its own drops. 1 durability per use |
| **Vein** | Pickaxes | Mine an ore **while sneaking** and the connected ore of the same kind breaks too, up to 16 more blocks |

### Details

- **Undead** for Westernesse are zombies and their kin, skeletons and their kin, the Wither, phantoms, zoglins, zombified piglins and undead horses.
- **Frost** on a bow is carried by the arrow, so it still applies if you switch weapons before it lands.
- **Harvest** works on wheat, carrots, potatoes, beetroots and nether wart. Protection plugins are respected.
- **Vein** breaks each block as if you'd mined it yourself: protection plugins, Fortune, Silk Touch, Delving and a [Ring of the Seven](rings-of-power.md) all apply, and each block costs the pickaxe durability as normal. It stops if the pickaxe breaks. Ores are coal, iron, copper, gold, redstone, lapis, diamond, emerald and nether quartz ore.

A resource pack can give rune-stones their own look via `custom_model_data` strings `rune_stone_<rune>`, e.g. `rune_stone_westernesse`.
