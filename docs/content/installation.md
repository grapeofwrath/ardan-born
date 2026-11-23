---
title: "Installation"
---
For server owners. ArdanBorn runs on **Paper 26.3**.

## Steps

1. Install [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) 2.10.11 or newer. This is required.
2. Install the four optional addons below.
3. Put `ArdanBorn-*.jar` in `plugins/` and start the server.

## Optional addons

Some origins use powers from these Origins-Reborn addons. If an addon is missing, those origins still load and can be picked, but that addon's powers do nothing, and nothing is logged about it. Install all four for the full experience.

| Addon | Tested version | Used by |
|---|---|---|
| [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy) | 1.1.14 | Beorning, Defender, Naugrim, Sindar, Small Size |
| [Origins-Magic](https://modrinth.com/plugin/origins-magic) | 1.0.18 | Beorning, Herbalist, Hobbit, Loremaster, Sindar |
| [Origins-Mobs](https://modrinth.com/plugin/origins-mobs) | 1.0.41 | Beastfriend, Beorning, Great Eagle, Healer, Herbalist |
| [Origins-Monsters](https://modrinth.com/plugin/origins-monsters) | 1.2.3 | Ranger |

[LevelledMobs](https://modrinth.com/plugin/levelledmobs) is also optional. If it's installed, mob levels decide which undead can inflict a [Morgul wound](morgul-wound.md); see [Settings](#settings).

## What happens on startup

- ArdanBorn copies its origins into `plugins/ArdanBorn/origins/`. Existing files are never overwritten, and deleted ones come back.
- It adds layer defaults to `plugins/Origins-Reborn/config.yml`, but only keys that aren't there yet:
  - layers are chosen in the order `origin` → `class` → `size`
  - `size` defaults to `size_normal`
- It rewords some powers from Origins-Reborn and its addons in `plugins/Origins-Reborn/translations.yml` (for example, Spectral becomes Soft-footed). It only changes entries that are missing or still have the original wording, so your own edits stay. The new wording also shows on stock origins that share those powers.

**Updating:** because existing origin files are never overwritten, delete an origin's file from `plugins/ArdanBorn/origins/` to get its new version after an update.

## Settings

`plugins/ArdanBorn/config.yml` is created on first start. Restart the server after changing it.

```yaml
morgul-wound:
  enabled: true
  chance: 0.10        # per melee hit from an eligible mob
  servant-chance: 0.50 # per melee hit from a Servant of the Enemy or a Ringwraith
  min-level: 30       # LevelledMobs level needed to wound
  stage-minutes: 3    # online minutes before the wound worsens
```

See [Morgul Wound](morgul-wound.md#for-server-owners) for what these do. A setting missing from an older `config.yml` uses its default; you don't need to add it.

`plugins/ArdanBorn/rings.yml` tracks the unique [Rings of Power](rings-of-power.md#unique-rings). Don't edit it while the server is running.

**Item-clearing plugins** (ClearLag and the like) and scheduled `/kill @e[type=item]` commands are fine to keep. A unique ring they remove is put straight back where it was. Whitelisting the rings in those plugins saves the round trip, but isn't needed.

## Loot refills

The [Rings of Power](rings-of-power.md) are found in ancient city, stronghold and bastion chests. Chests that have been looted stay empty unless you turn on Paper's loot refills in `config/paper-world-defaults.yml`:

```yaml
lootables:
  auto-replenish: true
```

Looted chests then refill between `refresh-min` and `refresh-max` after being looted (12 hours to 2 days by default). This is a Paper feature; no plugin is needed.

## Hiding origins

- **Origins-Reborn's stock origins:** delete the ones you don't want from `plugins/Origins-Reborn/origins/`, or set `"unchoosable": "true"` in them.
- **ArdanBorn's origins:** set `"unchoosable": "true"` in the file. Don't delete the file, because it comes back on restart.

## Namespace

Everything uses the `ardanborn` namespace: power keys such as `ardanborn:delvers_instinct`, item data, and recipe keys.
