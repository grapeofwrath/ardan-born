---
title: "Configuration"
---
For server owners. Every setting ArdanBorn has, and the server settings worth knowing about.

## ArdanBorn settings

`plugins/ArdanBorn/config.yml` is created on first start. Restart the server after changing it. A setting missing from an older file uses its default; you don't need to add it.

```yaml
morgul-wound:
  enabled: true
  chance: 0.10
  servant-chance: 0.50
  min-level: 30
  stage-minutes: 3
race-items:
  power: 1.0
  overrides: {}
  disabled: []
```

### Morgul wound

These control the [Morgul wound](morgul-wound.md).

| Setting | Default | What it does |
|---|---|---|
| `enabled` | `true` | Set to `false` to turn wounds off entirely |
| `chance` | `0.10` | Chance per melee hit from an eligible mob (0.10 is 10%) |
| `servant-chance` | `0.50` | Chance per melee hit from a Servant of the Enemy or a Ringwraith |
| `min-level` | `30` | With LevelledMobs: the lowest mob level that can wound |
| `stage-minutes` | `3` | Online minutes before the wound worsens |

**[LevelledMobs](https://modrinth.com/plugin/levelledmobs)** is optional, and ArdanBorn has no code dependency on it: it reads the level LevelledMobs stores on each mob. Set `min-level` to suit your LevelledMobs rules. If its levels rarely reach 30 on your server (with weighted-random levels most mobs are low), lower it.

### Race items

These set how strong every race item and armor effect is. The pages here give the numbers at power 1.0; item tooltips give none.

| Setting | Default | What it does |
|---|---|---|
| `power` | `1.0` | From 0.5 to 3.0. Scales bonus damage, damage reduction, damage dealt, healing and chances. At 2.0, +25% damage becomes +50%. Try 1.5 if mobs are tough, such as with high LevelledMobs levels |
| `overrides` | none | A power for single items or armor effects, instead of the one above, e.g. `{sting: 1.5}` |
| `disabled` | none | Items and armor effects that do nothing, e.g. `[sting, fellowship]` |

- One reduction never goes above 50%, and one chance never above 60%.
- Cooldowns, ranges, speed, durations, potion effects and Soft Landing don't change.
- Ids are the item's or effect's name in lower case with underscores and no accents or apostrophes, e.g. `axe_of_azaghal`, `durins_wealth`. The Mithril Coat is `mithril`. A typo is reported in the console at startup.
- What a piece does just by being its base item (walking on powder snow, the spyglass, the turtle shell) can't be disabled.

## Rings of Power

`plugins/ArdanBorn/rings.yml` tracks the unique [Rings of Power](rings-of-power.md#unique-rings). Don't edit it while the server is running.

**Item-clearing plugins** (ClearLag and the like) and scheduled `/kill @e[type=item]` commands are fine to keep. A unique ring they remove is put straight back where it was. Whitelisting the rings in those plugins saves the round trip, but isn't needed.

## Loot refills

Rings of Power are found in [chests](rings-of-power.md#finding-a-ring), and chests that have been looted stay empty. To let rings keep turning up, turn on Paper's loot refills in `config/paper-world-defaults.yml` (no plugin needed):

```yaml
lootables:
  auto-replenish: true
```

Looted chests then refill between `refresh-min` and `refresh-max` after being looted (12 hours to 2 days by default), and `restrict-player-reloot` stops one player farming the same chest. Refilled chests can hold rings too, and unique rings still can't be duplicated.

## Renamed ids

Some origin files and power ids were renamed to match the names players see. ArdanBorn updates its own files in `plugins/ArdanBorn/origins/` and the default origins in Origins-Reborn's `config.yml` when the server starts, and keeps your edits. If both the old and new file exist, the old one is set aside as `.json.old`. Players keep their origins.

If you've used an ArdanBorn power in your own origin files elsewhere, update its id yourself:

| Old power id | New power id |
|---|---|
| `ardanborn:bloodlust` | `ardanborn:battle_fury` |
| `ardanborn:eldar_body` | `ardanborn:undying_body` |
| `ardanborn:eldar_lore` | `ardanborn:deep_lore` |
| `ardanborn:falmari_lungs` | `ardanborn:breath_of_the_sea` |
| `ardanborn:gentle_friend` | `ardanborn:friend_of_beasts` |
| `ardanborn:keen_tracker` | `ardanborn:hunt_prey` |
| `ardanborn:kings_hands` | `ardanborn:hands_of_the_healer` |
| `ardanborn:last_stand` | `ardanborn:hope_unquenched` |
| `ardanborn:master_smith` | `ardanborn:mending_craft` |
| `ardanborn:tree_roots` | `ardanborn:tough_as_old_tree_roots` |
| `ardanborn:wizard_staff` | `ardanborn:light_of_the_staff` |

| Old origin file | New origin file |
|---|---|
| `alchemist.json` | `herbalist.json` |
| `bard.json` | `minstrel.json` |
| `champion.json` | `defender.json` |
| `cleric.json` | `healer.json` |
| `druid.json` | `beastfriend.json` |
| `eagle.json` | `great_eagle.json` |
| `fighter.json` | `warrior.json` |
| `inventor.json` | `smith.json` |
| `rogue.json` | `burglar.json` |
| `size_normal.json` | `normal_size.json` |
| `size_small.json` | `small_size.json` |
| `wizard.json` | `loremaster.json` |
