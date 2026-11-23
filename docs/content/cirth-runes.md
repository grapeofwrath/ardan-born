---
title: "Cirth Runes"
---
The Cirth were the runes of the Dwarves, and of the Elves of Beleriand. Any may craft a rune-stone, but only a [Smith](classes.md#smith) has the skill to inscribe it; and once inscribed, the rune serves whoever holds the weapon or tool.

## The making of rune-stones

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

The recipes are shaped, and are shown in the recipe book. Rune-stones have the likeness of amethyst shards that glint, and they cannot be used in other recipes.

## Inscribing

At a smithing table the Smith sets these things, and the rune-stone and the lapis are consumed. For any who is not a Smith the result slot remains empty.

| Slot | Item |
|---|---|
| Template | Rune-stone |
| Middle | Weapon or tool |
| Last | 1 lapis lazuli |

The item keeps its enchantments, damage and name, and any race-item upgrade; and an item that bears a rune may still become a [race item](race-items.md#weapons), and keeps its rune. The runes are written at the end of its tooltip.

### How many runes

| Item | Runes |
|---|---|
| Netherite | 2, and never two of the same |
| All else, with bows, crossbows, tridents and maces | 1 |

When an item can hold no more, a new rune takes the place of the oldest. Diamond that is upgraded to netherite keeps its rune, and has room for a second.

## The runes

| Rune | Inscribed upon | Effect |
|---|---|---|
| **Westernesse** | Swords, axes, spears, maces, tridents | ×1.3 melee damage to the [undead](lore-and-items.md#undead) |
| **Warning** | Swords, axes, spears, maces, tridents | While you hold it and a monster is within 16 blocks, your action bar reads *Your blade is kindled blue*, and blue sparks are seen at the blade. |
| **Frost** | Swords, axes, spears, maces, tridents, bows, crossbows | Melee hits, arrows and thrown tridents give Slowness I for 2 seconds. |
| **Delving** | Pickaxes | Iron, copper and gold ore, and their deepslate forms, yield ingots. Fortune multiplies them still. |
| **Harvest** | Hoes | Right-click a ripe crop, and every ripe crop in the 3×3 about it is reaped, and replanted from its own drops, at the cost of 1 durability. |
| **Vein** | Pickaxes | Mine an [ore](lore-and-items.md#ores) while sneaking, and the connected ore of the same kind is broken with it, up to 16 more blocks. |

### Details

- **Frost** is carried by the arrow, and so it remains though you change weapons.
- **Harvest** avails with wheat, carrots, potatoes, beetroots and nether wart, and it respects protection plugins.
- **Vein** breaks each block as though you had mined it. Protection plugins, Fortune, Silk Touch, Delving and the [Ring of the Seven](rings-of-power.md#the-rings) all apply, and each block costs durability. If the pickaxe breaks, it goes no further.

`custom_model_data`: `rune_stone_<rune>`, e.g. `rune_stone_westernesse`
