---
title: "Commands"
---
| Command | Who can use it | What it does |
|---|---|---|
| `/ardanborn` | Everyone | Shows the plugin version |
| `/ardanborn give <players> <item> [amount]` | Ops (permission `ardanborn.give`) | Gives ArdanBorn items |
| `/ardanborn ring release <ring>` | Ops (permission `ardanborn.rings`) | Sends a unique Ring of Power back into the loot pool |

## `/ardanborn give`

Vanilla `/give` can't create ArdanBorn items. Tab completion lists every item.

| Item id | Item |
|---|---|
| `arkenstone`, `pearl_of_osse`, `feather_of_thorondor`, `honey_of_beorn`, `light_of_the_trees`, `mallorn_leaf`, `star_of_elendil`, `evenstar`, `mathom`, `white_hand` | A race template: the [Arkenstone](arkenstone.md), [Pearl of Ossë](pearl-of-osse.md), [Feather of Thorondor](feather-of-thorondor.md), [Honey of Beorn](honey-of-beorn.md), [Light of the Trees](light-of-the-trees.md), [Mallorn Leaf](mallorn-leaf.md), [Star of Elendil](star-of-elendil.md), [Evenstar](evenstar.md), [Mathom](mathom.md) or [White Hand](white-hand.md) |
| `<set>_<material>`, e.g. `arkenstone_diamond_boots`, `alqualonde_netherite_helmet`, `thorondor_diamond_helmet` | A finished armor piece. Sets: `arkenstone`, `alqualonde`, `thorondor`, `lorien`, `mithril`, `gondolin`, `citadel`, `imladris`, `isengard`, `carrock`. E.g. `lorien_diamond_chestplate` is a Cloak of Lórien |
| `spear_of_the_havens` | A Spear of the Havens |
| `aeglos_diamond`, `aeglos_netherite` | Aeglos |
| `arkenstone_pickaxe_diamond`, `arkenstone_pickaxe_netherite` | An Arkenstone Pickaxe |
| `hunting_bow` | A Hunting Bow |
| `phial_of_galadriel` | A Phial of Galadriel |
| `horn_of_gondor` | A Horn of Gondor |
| `miruvor` | A draught of Miruvor |
| `scimitar_of_isengard_diamond`, `scimitar_of_isengard_netherite` | A Scimitar of Isengard |
| `axe_of_azaghal_<tier>`, `sting_<tier>`, `orcrist_<tier>`, `anduril_<tier>`, `hadhafang_<tier>` | That weapon, `diamond` or `netherite` |
| `bow_of_the_galadhrim` | A Bow of the Galadhrim |
| `net_of_the_havens`, `breath_of_manwe`, `hithlain`, `longbottom_pipe`, `orc_draught`, `skin_changers_claw` | That artifact |
| `rune_stone_<rune>`, e.g. `rune_stone_westernesse` | A [rune-stone](cirth-runes.md). Runes: `westernesse`, `warning`, `frost`, `delving`, `harvest`, `vein` |
| `ring_<ring>`, e.g. `ring_one` | A [Ring of Power](rings-of-power.md). Rings: `seven`, `nine`, `narya`, `nenya`, `vilya`, `one`. Giving a unique ring (the Three or the One) makes that copy the current one, and any older copy crumbles |
| `athelas` | A sprig of [athelas](morgul-wound.md#athelas) |

- `amount` is 1-64 and defaults to 1. Armor pieces come separately.
- What doesn't fit in the inventory drops at the player's feet.

Example: `/ardanborn give @s arkenstone_netherite_boots`

## `/ardanborn ring release`

`/ardanborn ring release <narya|nenya|vilya|one>` sends a unique [Ring of Power](rings-of-power.md#unique-rings) back into the loot pool, so it can be found in chests again. Any copy still in the world crumbles: right away if it's dropped or carried by an online player, otherwise the first time it's seen.

For a unique ring nobody can reach. `/kill` doesn't work: a ring that is killed or cleared is put back where it was.
