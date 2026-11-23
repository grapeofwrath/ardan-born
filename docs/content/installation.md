---
title: "Installation"
---
Requires **Paper 26.3**.

## Steps

1. Install [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) 2.10.11 or newer (required).
2. Install the optional addons below.
3. Put `ArdanBorn-*.jar` in `plugins/` and start the server.
4. Optional: change the [settings](configuration.md).

## Optional addons

Some origins use powers from these addons. If one is missing, those origins can still be picked, but its powers do nothing and nothing is logged.

| Addon | Tested version | Used by |
|---|---|---|
| [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy) | 1.1.14 | Beorning, Defender, Naugrim, Sindar, Small Size |
| [Origins-Magic](https://modrinth.com/plugin/origins-magic) | 1.0.18 | Beorning, Herbalist, Hobbit, Loremaster, Sindar |
| [Origins-Mobs](https://modrinth.com/plugin/origins-mobs) | 1.0.41 | Beastfriend, Beorning, Great Eagle, Healer, Herbalist |
| [Origins-Monsters](https://modrinth.com/plugin/origins-monsters) | 1.2.3 | Ranger |

[LevelledMobs](https://modrinth.com/plugin/levelledmobs) is also optional. With it, mob levels decide which undead can inflict a [Morgul wound](configuration.md#morgul-wound).

## What happens on startup

- ArdanBorn copies its origins into `plugins/ArdanBorn/origins/`. Existing files are never overwritten, and deleted ones come back.
- It adds missing layer defaults to `plugins/Origins-Reborn/config.yml`:
  - layer order: `origin` → `class` → `size`
  - `size` defaults to `normal_size`
- It rewords some powers from Origins-Reborn and its addons in `plugins/Origins-Reborn/translations.yml` (e.g. Spectral becomes Soft-footed). Entries you have edited are left alone. Stock origins that share those powers show the new wording too.

**Updating:** to get the new version of an origin, delete its file from `plugins/ArdanBorn/origins/` and restart. This discards any edits in it.

## Hiding origins

- **Origins-Reborn's stock origins:** delete the ones you don't want from `plugins/Origins-Reborn/origins/`, or set `"unchoosable": "true"` in them.
- **ArdanBorn's origins:** set `"unchoosable": "true"` in the file. A deleted file comes back on restart.

## Namespace

`ardanborn`, for power keys (e.g. `ardanborn:delvers_instinct`), item data and recipe keys.
