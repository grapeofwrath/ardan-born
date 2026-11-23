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
