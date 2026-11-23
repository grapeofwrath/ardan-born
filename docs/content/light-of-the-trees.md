---
title: "Light of the Trees"
---
*A glimmer of Laurelin and Telperion.* The Noldor's template, made to suit the [Noldor](ancestries.md#noldor-high-elf). It makes the **Phial of Galadriel**.

Like every race item, anyone can use it; the Noldor get extra.

## Crafting the Light of the Trees

Shaped recipe in a crafting table. It also shows in the recipe book.

| | | |
|:-:|:-:|:-:|
| Glowstone Dust | Glow Berries | Glowstone Dust |
| Gold Ingot | Diamond | Gold Ingot |
| Glowstone Dust | Glow Berries | Glowstone Dust |

The Light of the Trees looks like glinting glowstone dust. It can't be used in any other recipe.

## Phial of Galadriel

*A light to you in dark places.* At a **smithing table**, put the Light of the Trees with a **glass bottle** (and nothing in the third slot). The Phial doesn't stack, and it never fills with water.

| Who | Effect |
|---|---|
| Anyone | **Raise it:** right-click with the Phial in your main hand. Monsters within 10 blocks glow for 10 seconds, spiders and cave spiders take 2 hearts of magic damage and are thrown back, and you're cured of Darkness and Blindness. 60-second cooldown. |
| Anyone | **Light the way:** held in your **off-hand**, it lights the space around you (light level 15). |
| Noldor | Range 16, every player in range is also cured of Darkness and Blindness, and the cooldown is 40 seconds. |

### The off-hand light

- The light follows you, updating as you move from block to block.
- Everyone within 48 blocks sees it.
- It's only shown to players' game clients. The world itself never changes, so no light blocks are ever left behind, even after a crash or restart.
- It shines from your head's block, or your feet if your head is in something (such as water). If both are blocked, there's no light until you move.
- Right-clicking with something else in your main hand never raises an off-hand Phial; only a Phial in the main hand can be raised.

A resource pack can give these items their own look via `custom_model_data` strings `light_of_the_trees` and `phial_of_galadriel`.
