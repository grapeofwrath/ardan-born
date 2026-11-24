# Playtest tools

A loopback-only test server and a real Minecraft client on a hidden display, so Claude can join and test the plugin. Needs Prism Launcher with a 26.3 instance (named `26.3`, or set `PRISM_BASE`) and a logged-in account that owns the game.

```
nix develop .#playtest
tools/playtest/server                      # build, start, wait until ready
tools/playtest/client                      # join as ClaudeTester
tools/playtest/rcon 'give ClaudeTester stick'
tools/playtest/input key e                 # any xdotool arguments
tools/playtest/shot /tmp/shot.png
tools/playtest/stop
```

- The server runs in `run-test/` (git-ignored; it holds a random RCON password) in offline mode. It listens only on 127.0.0.1. Delete `run-test/` for a fresh world.
- The client is a separate Prism instance, `ardan-test`. `run/` and your own instance are not touched.
- Aim with `rcon 'execute as ClaudeTester at @s run tp @s ~ ~ ~ <yaw> <pitch>'`; click with `input click 1` (left) or `input click 3` (right).
- Screen is 854x480, so screenshot pixels are mouse coordinates.
- First join: the resource pack prompt waits under the origin menu. Click Confirm (425, 250) to reach it, then Proceed (272, 298); the menu returns with its proper look.
- For healing tests: `rcon 'gamerule natural_health_regeneration false'`. Escape with no menu open pauses the game, and clicks do nothing until it is closed.

## Tests

With the server and client running, in the playtest shell:

```
perl tools/playtest/tests/items.pl
```

| Script | Covers |
|---|---|
| `items.pl` | Every item by command, every recipe, recipe and fuel guards |
| `smithing.pl` | Every smithing upgrade at a real table, rune inscription |
| `armor.pl` | Armor piece and full-set effects (about 20 minutes) |
| `weapons.pl` | Weapon bonuses, barding, Cleave |
| `powers.pl` | Ancestry and class powers, food |
| `artifacts.pl` | Artifacts used by right-click, the Arkenstone pickaxe |
| `rings.pl` | Rings of Power, betrayal, the Morgul wound |
| `adornments.pl` | The adornments screen, wearing, death, effects and loot (about 15 minutes) |

- Run one at a time, and leave the client alone meanwhile: they move its mouse. Each ends with the counts and a list of failures.
- They erase the tester's inventory and kill nearby mobs, and raise the tester's health to 400 so that hits can be measured.
- `/damage ... by` raises the same events as a real hit, but some damage types then have the cause `CUSTOM`. Where the cause matters, use the real thing (TNT, magma, a thrown potion).
- Known miss: `powers.pl` fails its Beorning Honey-fed check, because their health powers clash with the raised health. At normal health it heals 6 as it should.
- Chance effects are checked within 3.5 standard deviations, so a rare failure can be luck. Run it again.
- Edited origins in `run-test/plugins/ArdanBorn/origins/` are never overwritten: delete that folder to test changed origin files.
