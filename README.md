# ArdanBorn

Paper 26.3 plugin for a Tolkien-themed server:

- **Origins** for [Origins-Reborn](https://modrinth.com/plugin/origins-reborn): ancestries, classes and sizes. Merged from [origins-arda](https://github.com/grapeofwrath/origins-arda).
- **Custom armor sets** made at a smithing table, starting with the **Arkenstone**.

📖 **[Wiki](https://github.com/grapeofwrath/ardan-born/wiki)**: every origin and power, the armor, commands, and full install notes.

## Install

1. Install [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) 2.10.11 or newer.
2. Recommended: install [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy), [Origins-Magic](https://modrinth.com/plugin/origins-magic), [Origins-Mobs](https://modrinth.com/plugin/origins-mobs) and [Origins-Monsters](https://modrinth.com/plugin/origins-monsters). Some origins use their powers; without them those powers silently do nothing.
3. Put `ArdanBorn-*.jar` in `plugins/` and start the server.

See [Installation](https://github.com/grapeofwrath/ardan-born/wiki/Installation) on the wiki for tested versions, what ArdanBorn changes on startup, and how to hide origins.

## Development

Requires Nix with flakes and direnv (`direnv allow`), or JDK 25.

```
./gradlew build      # build/libs/ardan-born-<version>.jar
./gradlew runServer  # local test server in ./run with Origins-Reborn and the four addons
```

The wiki's origin and power pages are generated from the code; see [`tools/wiki`](tools/wiki/README.md).
