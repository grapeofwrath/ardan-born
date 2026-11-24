# Power details

Rules that don't fit in a power's one-line effect (`power-effects.tsv`). `generate.pl` prints each section under
the effect on the Powers page. Leave a power out if the effect says it all. Keep numbers in step with the code
(`OriginAbilities.java`) or, for borrowed powers, with Origins-Reborn's `ability-config.yml` defaults.

Each section starts with `## <power key>`; everything until the next `## ` is copied as Markdown.

## ardanborn:backstab
- "Behind" means facing the same way as the target, within about 60°.
- A mob chasing someone else counts as not targeting you.
- Critical-hit particles show when it lands.

## ardanborn:bane_of_mirkwood
- The two bonuses stack: ×1.8 against a spider in woodland.

## ardanborn:call_of_huan
- A tamed wolf, 1.5× size: 30 hearts, 4 hearts of bite damage, +20% speed, 50% knockback resistance.
- Leaves early if you log out, die or call him again. Not saved with the world.
- Can't be fed, bred or dyed. Right-click with an empty hand to sit or stand. Drops nothing.
- Bones still tame ordinary wolves. The cooldown shows on your action bar.

## ardanborn:delvers_instinct
- Night Vision lasts 16 seconds after you leave.

## ardanborn:friend_of_beasts
- Beasts: wolves, bees, polar bears, llamas, trader llamas, pandas, goats.
- Half damage covers every animal you've tamed, from any source, at any distance, while you're online. Not void or `/kill`.
- [Huan](#call-of-huan) counts.

## ardanborn:gift_of_men
- Sources include mobs, ore, smelting, breeding, fishing and trading.

## ardanborn:green_thumb
- Crops: wheat, carrots, potatoes, beetroots, nether wart, sweet berries, cocoa, torchflowers, melon and pumpkin stems. Not pitcher plants.
- Wheat takes about 2-3 minutes from seed to ripe.

## ardanborn:hands_of_the_healer
- **Right-click a player**, not sneaking: Regeneration II for 8 seconds, cures Poison and Wither, and holds back a [Morgul wound](morgul-wound.md#treatment). 15-second cooldown.
- Holding back restarts the wound's timer; it doesn't cure it. Once every 10 minutes per wounded player, by any Healer.
- **Sneak and right-click**, at anything: you and players within 8 blocks heal 3 hearts, and undead within 8 blocks take 3 hearts of magic damage. Doesn't cure Poison or Wither. 60-second cooldown.
- [Healer of Imladris](#healer-of-imladris) raises the heal to 4.5 hearts for a Peredhil.
- `custom_model_data` for resource packs: `staff_of_rest`. The anvil adds it, as does the first use of a staff that lacks it. Naming the stick anything else takes it off.

## ardanborn:healer_of_imladris
- Any source: beacons, [Star of Eärendil](#star-of-eärendil), [Staff of Rest](#staff-of-rest), [Healing Presence](#healing-presence).
- Not natural regeneration from hunger.

## ardanborn:healing_presence
- [Healer of Imladris](#healer-of-imladris) raises it to 1.5 hearts for a Peredhil.

## ardanborn:hunt_prey
- A hit on another mob moves the mark.
- The marking arrow gets no bonus unless that mob was already marked.
- Never marks players.

## ardanborn:leaf_walker
- Any leaves, including azalea and mangrove. You can't pass through them.

## ardanborn:light_of_the_staff
- Ignores armor, and gets no melee bonuses. Kills count as yours.
- The cooldown shows on your action bar.

## ardanborn:moonbound
- The sky needn't be visible.
- Moon phase: `/time query day`, divided by 8; a remainder of 0 is full.

## ardanborn:smiths_mark
- **Under a full moon:** craft at night in the Overworld, with nothing at all overhead.
  - Tools and weapons are pickaxes, axes, shovels, hoes, swords, spears, maces, bows, crossbows, tridents and fishing rods. Armor isn't blessed.
  - Every moonlit enchantment starts one level above its vanilla maximum and goes up to **5 levels above it**: Sharpness VI-X, Fortune IV-VIII, Unbreaking IV-VIII, Knockback III-VII, Fire Aspect III-VII. Lower levels are likelier; for Fortune, IV is 5 times as likely as VIII.
  - Quick Charge and Lure stop at V.
  - The roll shows in the result slot before you take the item, and taking the ingredients out and putting them back gives the same roll. Each item you take rolls a new one.
  - **Twice per full moon.** Each moonlit item you take counts, and your action bar shows how many are left. The count is tracked on your character, so logging out or a restart doesn't reset it, and it starts over at the next full moon. After the second, crafting is ordinary until then.
  - Moonlit items must be taken one click at a time; shift-click is refused for them.
  - Check the moon in the sky, or with `/time query day` (full moon when the day divided by 8 leaves 0).
  - Crafter blocks never bless an item.

**How many enchantments**

| Enchantments | Chance |
|---|---|
| 1 | 89% |
| 2 | 10% |
| 3 | 1% |

Each extra one is drawn from what's left, skipping any that clash with what the item already has (Sharpness, Smite and Bane of Arthropods exclude each other, as do Riptide and Loyalty, and Density and Breach).

**Which enchantment**

| Rarity | Weight | Enchantments |
|---|---|---|
| Common | 10 | Efficiency, Sharpness, Power, Quick Charge, Impaling, Density, Lunge, Lure |
| Uncommon | 5 | Unbreaking, Smite, Bane of Arthropods, Knockback, Punch, Piercing, Loyalty, Breach, Luck of the Sea |
| Rare | 2 | Fire Aspect, Sweeping Edge, Riptide, Wind Burst |
| Very rare | 1 | Fortune, Looting |

Never drawn: Silk Touch, Mending, Infinity, Flame, Multishot, Channeling. Only those that fit the item count, so the odds depend on the item. For a diamond pickaxe the choices are Efficiency (10), Unbreaking (5) and Fortune (1): 63% Efficiency, 31% Unbreaking, 6% Fortune.

## ardanborn:songs_of_power
- "Allies" below means you and every player within 24 blocks.
- Range is measured from you. Custom horns such as the Horn of Gondor don't play songs.

**Common songs** (horns from goats ramming trees and stone)

| Horn | Song | Effect |
|---|---|---|
| Ponder | Song of Rest | Allies: Regeneration I for 12s |
| Sing | Song of Valor | Allies: Speed I for 20s and Regeneration I for 8s |
| Seek | Song of Seeking | Monsters within 32 blocks glow for 10s |
| Feel | The Walking Song | Allies: +3 hunger and Speed I for 30s |

**Greater songs** (horns from screaming goats)

| Horn | Song | Effect |
|---|---|---|
| Admire | Song of Courage | Allies: Strength I for 15s and Resistance I for 8s |
| Call | Gathering Call | Allies: Speed II and Jump Boost I for 15s |
| Yearn | Song of Hope | Allies: Poison, Wither, Weakness, Slowness, Mining Fatigue, Hunger, Blindness and Darkness are cleared, then Regeneration I for 5s |
| Dream | Lullaby of Lúthien | Monsters within 10 blocks: Slowness IV for 5s, and they forget who they were chasing |

## ardanborn:tough_as_old_tree_roots
- Slowness from other ArdanBorn or Origins powers isn't shortened.

## ardanborn:unseen
- A mob you hit can fight back, and one already chasing you may keep chasing.

## ardanborn:woodland_stride
- Woodland: any biome with *forest*, *taiga*, *jungle*, *grove* or *woods* in its name.

## ardanborn:wrath_of_the_noldor
- Creatures of the Nether: blazes, ghasts, magma cubes, hoglins, piglins, piglin brutes.

## ardanborn:charge_of_the_mark
- Riding means a horse, donkey, mule, camel or llama. Galloping is 4 blocks per second or more.
- A hit that knocks a monster back starts the 3 seconds again. Bosses aren't knocked back.
- Adds to Spear-hardy, the [Spear of the Mark](banner-of-the-mark.md#spear-of-the-mark) and Death!, up to [60%](configuration.md#rohirrim) together.

## ardanborn:breeder_of_the_mark
- Each quality is the better of the foal's own and its parents' average.
- Works on mules, but only horses can be Mearas.

## ardanborn:spear_hardy
- Adds to [Charge of the Mark](#charge-of-the-mark) and the Rohirrim's items, up to [60%](configuration.md#rohirrim) together.

## ardanborn:bane_of_the_dunlendings
- Illagers: pillagers, vindicators, evokers and illusioners. Not ravagers, vexes or witches.
- Not part of the 60% cap; multiplies with it.
