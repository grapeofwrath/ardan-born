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
