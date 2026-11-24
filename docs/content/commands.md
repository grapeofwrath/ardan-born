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
| `star_of_elendil`, `banner_of_the_mark`, `light_of_the_trees`, `mallorn_leaf`, `pearl_of_osse`, `evenstar`, `arkenstone`, `mathom`, `white_hand`, `feather_of_thorondor`, `honey_of_beorn` | A race template: the [Star of Elendil](star-of-elendil.md), [Banner of the Mark](banner-of-the-mark.md), [Light of the Trees](light-of-the-trees.md), [Mallorn Leaf](mallorn-leaf.md), [Pearl of Ossë](pearl-of-osse.md), [Evenstar](evenstar.md), [Arkenstone](arkenstone.md), [Mathom](mathom.md), [White Hand](white-hand.md), [Feather of Thorondor](feather-of-thorondor.md) or [Honey of Beorn](honey-of-beorn.md) |
| `<set>_<material>`, e.g. `arkenstone_diamond_boots`, `alqualonde_netherite_helmet`, `thorondor_diamond_helmet` | A finished armor piece. Sets: `citadel`, `edoras`, `gondolin`, `lorien`, `alqualonde`, `imladris`, `arkenstone`, `mithril`, `isengard`, `thorondor`, `carrock`. E.g. `lorien_diamond_chestplate` is a Cloak of Lórien |
| `horn_of_gondor` | A Horn of Gondor |
| `barding_of_the_mark_diamond`, `barding_of_the_mark_netherite` | A Barding of the Mark |
| `phial_of_galadriel` | A Phial of Galadriel |
| `bow_of_the_galadhrim` | A Bow of the Galadhrim |
| `spear_of_the_havens` | A Spear of the Havens |
| `miruvor` | A draught of Miruvor |
| `arkenstone_pickaxe_diamond`, `arkenstone_pickaxe_netherite` | An Arkenstone Pickaxe |
| `scimitar_of_isengard_diamond`, `scimitar_of_isengard_netherite` | A Scimitar of Isengard |
| `aeglos_diamond`, `aeglos_netherite` | Aeglos |
| `hunting_bow` | A Hunting Bow |
| `anduril_<tier>`, `spear_of_the_mark_<tier>`, `orcrist_<tier>`, `hadhafang_<tier>`, `axe_of_azaghal_<tier>`, `sting_<tier>` | That weapon, `diamond` or `netherite` |
| `hithlain`, `net_of_the_havens`, `longbottom_pipe`, `orc_draught`, `breath_of_manwe`, `skin_changers_claw` | That artifact |
| `rune_stone_<rune>`, e.g. `rune_stone_westernesse` | A [rune-stone](cirth-runes.md). Runes: `westernesse`, `warning`, `frost`, `delving`, `harvest`, `vein` |
| `ring_<ring>`, e.g. `ring_one` | A [Ring of Power](rings-of-power.md). Rings: `seven`, `nine`, `narya`, `nenya`, `vilya`, `one`. Giving a unique ring (the Three or the One) makes that copy the current one, and any older copy crumbles |
| `athelas` | A sprig of [athelas](morgul-wound.md#athelas) |

- `amount` is 1-64 and defaults to 1. Armor pieces come separately.
- What doesn't fit in the inventory drops at the player's feet.

Example: `/ardanborn give @s arkenstone_netherite_boots`

## `/ardanborn ring release`

`/ardanborn ring release <narya|nenya|vilya|one>` sends a unique [Ring of Power](rings-of-power.md#unique-rings) back into the loot pool, so it can be found in chests again. Any copy still in the world crumbles: right away if it's dropped or carried by an online player, otherwise the first time it's seen.

For a unique ring nobody can reach. `/kill` doesn't work: a ring that is killed or cleared is put back where it was.
