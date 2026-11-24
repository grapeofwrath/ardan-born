---
title: "Configuration"
---
## ArdanBorn settings

`plugins/ArdanBorn/config.yml` is created the first time the server starts. Restart the server after you change it. If an older file is missing a setting, the default is used.

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
rohirrim:
  damage-bonus-cap: 0.60
mearas:
  chance: 0.02
  shadowfax-chance: 0.20
adornments:
  drop-on-death: true
  resource-pack: true
```

### Morgul wound

These settings control the [Morgul wound](morgul-wound.md).

| Setting | Default | What it does |
|---|---|---|
| `enabled` | `true` | Set to `false` to turn wounds off entirely |
| `chance` | `0.10` | Chance per melee hit from an eligible mob (0.10 is 10%) |
| `servant-chance` | `0.50` | Chance per melee hit from a Servant of the Enemy or a Ringwraith |
| `min-level` | `30` | With LevelledMobs: the lowest mob level that can wound |
| `stage-minutes` | `3` | Online minutes before the wound worsens |

**[LevelledMobs](https://modrinth.com/plugin/levelledmobs)** is optional. When it is installed, ArdanBorn reads the level it stores on each mob. If mobs on your server rarely reach level 30, as is common with weighted-random levels, lower `min-level` to suit.

### Race items

These settings control how strong race item and armor effects are. The numbers on this wiki are at power 1.0, and item tooltips don't show numbers.

| Setting | Default | What it does |
|---|---|---|
| `power` | `1.0` | From 0.5 to 3.0. Scales bonus damage, damage reduction, damage dealt, healing and chances. At 2.0, +25% damage becomes +50%. Try 1.5 if mobs are tough, such as with high LevelledMobs levels |
| `overrides` | none | A power for single items or armor effects, instead of the one above, e.g. `{sting: 1.5}` |
| `disabled` | none | Items and armor effects that do nothing, e.g. `[sting, fellowship]` |

- A single reduction never goes above 50%, and a single chance never goes above 60%.
- Power does not change cooldowns, ranges, speed, durations, potion effects or Soft Landing.
- An id is the name of the item or effect in lower case, with underscores and no accents or apostrophes, such as `axe_of_azaghal`, `durins_wealth`. The Mithril Coat is `mithril`. A typo is reported in the console at startup.
- What a piece does just by being its base item (walking on powder snow, the spyglass, the turtle shell) can't be disabled.

### Rohirrim

| Setting | Default | What it does |
|---|---|---|
| `damage-bonus-cap` | `0.60` | The most that Charge of the Mark, Spear-hardy, the [Spear of the Mark](banner-of-the-mark.md#spear-of-the-mark) and Death! can add to melee damage together (0.60 is 60%). Item power is applied before the cap |

### Mearas

These settings control the [Mearas](banner-of-the-mark.md#the-mearas).

| Setting | Default | What it does |
|---|---|---|
| `chance` | `0.02` | Chance that an eligible foal or wild horse is one of the Mearas (0.02 is 2%). Each horse is rolled once |
| `shadowfax-chance` | `0.20` | Chance that a new Meara is Shadowfax, while there is none in the world |

### Adornments

These settings are for [adornments](adornments.md). Their effects also follow the `race-items` settings above, under ids such as `sams_pack`.

| Setting | Default | What it does |
|---|---|---|
| `drop-on-death` | `true` | Whether worn adornments drop when a player dies, as armor does. The `keep_inventory` game rule still applies |
| `resource-pack` | `true` | Offers players the resource pack of the adornments screen, along with the Origins-Reborn pack |

The pack is sent only if Origins-Reborn sends its own (`resource-pack: enabled` in its config).

## Shadowfax

`plugins/ArdanBorn/shadowfax.yml` records the living [Shadowfax](banner-of-the-mark.md#shadowfax). Don't edit it while the server is running. If it is deleted, another Shadowfax can appear.

## Rings of Power

`plugins/ArdanBorn/rings.yml` tracks the unique [Rings of Power](rings-of-power.md#unique-rings). Don't edit it while the server is running.

Item-clearing plugins such as ClearLag, and scheduled `/kill @e[type=item]` commands, are safe to use. If they remove a unique ring, it is put back where it was.

## Loot refills

[Rings of Power](rings-of-power.md#finding-a-ring) are found in chests, and a chest stays empty once it has been looted. If you want rings to keep appearing, turn on Paper's loot refills in `config/paper-world-defaults.yml`:

```yaml
lootables:
  auto-replenish: true
```

Looted chests then refill between `refresh-min` and `refresh-max` after being looted (12 hours to 2 days by default), and `restrict-player-reloot` stops one player farming the same chest. Refilled chests can hold rings too, and unique rings still can't be duplicated.
