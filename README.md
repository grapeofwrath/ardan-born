# ArdanBorn

Paper 26.3 plugin for a Tolkien-themed server:

- **Origins**: an [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) addon with ancestries (`origin` layer), classes (`class` layer) and sizes (`size` layer). Merged from [origins-arda](https://github.com/grapeofwrath/origins-arda).
- **Custom armor sets**: craft a set template, then combine it with a diamond or netherite armor piece at a smithing table. The first set is the **Arkenstone**.

## Install

1. Install [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) 2.10.11 or newer.
2. Put `ArdanBorn-*.jar` in `plugins/` and start the server.

On startup ArdanBorn:
- copies its origins into `plugins/ArdanBorn/origins/`. Existing files are never overwritten, and missing ones are restored.
- adds layer defaults to `plugins/Origins-Reborn/config.yml` (only keys that are absent):
  - `origin` > `class` > `size` order
  - `size` defaults to `size_normal`

Optional cleanup: in `plugins/Origins-Reborn/origins/`, delete the stock origins you don't want, or set `"unchoosable": "true"` on them. To hide one of ArdanBorn's origins, set `"unchoosable": "true"` in its file. Don't delete it, because deleted files come back on restart.

`naugrim` (`fortune_increaser`) and `size_small` (`small_body`) use powers from [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy), so install that too.

Everything uses the `ardanborn` namespace: ability keys (`ardanborn:delvers_instinct`), item data and recipe keys.

## Arkenstone armor

Crafting the Arkenstone:

```
amethyst shard | echo shard | amethyst shard
gold ingot     | diamond    | gold ingot
amethyst shard | echo shard | amethyst shard
```

Making a piece: put the Arkenstone, a **diamond or netherite** helmet/chestplate/leggings/boots, and nothing else into a smithing table.
- Each set therefore comes in two variants, diamond and netherite. Effects can be stronger on netherite.
- It is still the same vanilla armor, so it can be enchanted, trimmed and repaired. A diamond piece can be netherite-upgraded into the netherite variant.

| Piece | Effect |
|---|---|
| Boots | **Snowtread**: walk on powder snow without sinking (sneak to sink in), immune to freezing |
| Helmet, chestplate, leggings, full set | not designed yet |

## Commands

| Command | Permission | Description |
|---|---|---|
| `/ardanborn` | none | Shows the plugin version |
| `/ardanborn give <players> <item> [amount]` | `ardanborn.give` (op) | Gives custom items. Tab-complete `item`: a set template (`arkenstone`) or a set piece (`arkenstone_diamond_boots`, `arkenstone_netherite_helmet`, ...) |

Custom items are vanilla items marked with plugin data, so vanilla `/give` can't create them.

## Development

Requires Nix with flakes and direnv (`direnv allow`), or JDK 25.

```
./gradlew build      # build/libs/ardan-born-<version>.jar
./gradlew runServer  # local test server in ./run with Origins-Reborn
```
