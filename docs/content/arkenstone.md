---
title: "Arkenstone"
---
*The Heart of the Mountain.* The dwarves' template, made to suit the [Naugrim](ancestries.md#naugrim-dwarf). It makes the **Arkenstone armor** and the **Arkenstone Pickaxe**.

Like every race item, anyone can use these; Naugrim get extra.

## Crafting the Arkenstone

Shaped recipe in a crafting table. It also shows in the recipe book.

| | | |
|:-:|:-:|:-:|
| Amethyst Shard | Emerald | Amethyst Shard |
| Gold Ingot | Diamond | Gold Ingot |
| Amethyst Shard | Emerald | Amethyst Shard |

The Arkenstone looks like glowing raw gold. It can't be used in any other recipe.

## Armor

At a **smithing table**, put in:

1. the Arkenstone, in the template slot,
2. a **diamond or netherite** helmet, chestplate, leggings or boots,
3. nothing in the third slot.

The armor becomes an Arkenstone piece. Each piece therefore comes in two tiers, diamond and netherite. Effects can be stronger on netherite.

- It keeps its enchantments, trim, damage and any custom name.
- It's still normal armor, so it can be enchanted, trimmed and repaired at an anvil with diamonds (or netherite ingots).
- A diamond piece can be upgraded to netherite at a smithing table as usual.
- Applying the Arkenstone to a piece that's already Arkenstone does nothing.
- Set pieces can't be used in crafting recipes, because those would strip their Arkenstone data. Dyeing is the exception.

| Piece | Effect |
|---|---|
| Helmet | **Ore-sense:** sneak and right-click while holding any pickaxe. Ores within 8 blocks (12 on netherite) glow through the stone for 10 seconds, for you alone. Once a minute. **Naugrim:** 4 blocks further. |
| Boots | **Snowtread:** walk on powder snow without sinking (sneak to sink in), and immune to freezing. **Naugrim, Mountain Stride:** step up full blocks without jumping; on netherite, also move 10% faster on natural stone and deepslate. |
| Chestplate | Not designed yet |
| Leggings | Not designed yet |
| Full set | Not designed yet |

### The boots

In vanilla, only leather boots can walk on powder snow. So Arkenstone boots are **leather boots with the full diamond or netherite stats**: armor, toughness, durability, enchantability and repair material. Netherite boots also survive fire and lava, like other netherite gear.

- They start dyed dark brown (diamond) or gold (netherite), and can be re-dyed like any leather armor. Upgrading to netherite turns the default brown gold, but keeps a color you chose yourself.
- Repair them with diamonds or netherite ingots, or another Arkenstone boot of the same tier. Plain leather boots don't work.
- Upgrade diamond to netherite at a smithing table with a netherite upgrade template and a netherite ingot, as usual.

## Arkenstone Pickaxe

At a **smithing table**, put the Arkenstone with a **diamond or netherite pickaxe** (and nothing in the third slot). It keeps its enchantments, damage and name, and can still be upgraded to netherite as usual.

**Tunnel-bore:** the pickaxe mines a 3×3 area. Sneak to mine a single block.

- The 3×3 lies in the face you dig into: **flat** when you dig into the top or bottom of a block, **upright** when you dig into a side.
- Only blocks a pickaxe mines are taken, and none harder than the block you aimed at, except that ores are always taken. Obsidian, ancient debris and containers (furnaces, hoppers…) are never broken by the extra area.
- Fortune and Silk Touch apply to every block, and each extra block costs durability.
- Protection plugins see each block as broken by you.
- **Naugrim:** the extra blocks cost no durability.

A resource pack can give these items their own look via `custom_model_data` strings `arkenstone` and `arkenstone_pickaxe`.
