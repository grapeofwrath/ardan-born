# Power details

Extra detail for the docs site's Powers page: exact numbers, how a power is triggered, and how it combines with
other powers and items. `tools/docs/generate.pl` prints each section under the power's description.

Say only what the description (shown right above) does not: levels, limits, exceptions, what stacks. Leave a section
empty if there is nothing to add.

Each section starts with `## <power key>`; everything until the next `## ` is copied as Markdown. Keep the
numbers in step with the code (`OriginAbilities.java`) or, for borrowed powers, with Origins-Reborn's
`ability-config.yml` defaults.

## ardanborn:gift_of_men
- Each orb or reward is rounded up, so even 1-point orbs give 2.
- Sources include mobs, ore, smelting, breeding, fishing and trading.

## ardanborn:hope_unquenched
- Any damage that leaves you alive at or below 30% counts. A killing blow doesn't trigger it.
- Resistance I and Regeneration I, with a golden flash and "Hope unquenched!" on your action bar.
- The cooldown starts when it triggers.

## ardanborn:undying_body
- Every source is blocked: food, potions, cave spiders, husks, witches.
- Other harmful effects such as Wither, Weakness and Slowness still apply.

## ardanborn:deep_lore
- An option never costs less than its slot number (1, 2 or 3 levels).
- The lapis cost and the enchantments you get are the same.

## ardanborn:woodland_stride
- Speed I, in any biome whose name includes *forest*, *taiga*, *jungle*, *grove* or *woods*: birch forests, dark forests, old growth taigas, bamboo jungles, cherry groves and so on.
- The effect lingers 3 seconds after you leave.
- The same biomes count for [Bane of Mirkwood](#bane-of-mirkwood).

## ardanborn:bane_of_mirkwood
- Spectral arrows and tridents you throw count too.
- The woodland biomes are the same list as [Woodland Stride](#woodland-stride).
- The two bonuses stack: ×1.8 against a spider in the forest.

## ardanborn:leaf_walker
- Any kind of leaves counts, including azalea and mangrove, and any height of fall.
- Speed II and Jump Boost II, lasting 2 seconds after you step off.
- Leaves are still solid, as for everyone; the power is about running *on* the canopy.

## ardanborn:lembas
- Saturation is filled too, and the healing is Regeneration I for 5 seconds.
- Bread eaten while it's on cooldown works as normal bread.

## ardanborn:breath_of_the_sea

## ardanborn:seas_vigor
- The new hearts start empty and fill as you heal. When you leave the water or the rain stops, they're removed, along with any health in them.
- Stacks with [Tide's Fury](#tides-fury), which uses the same water-or-rain condition.

## ardanborn:pearls_of_alqualonde
- Luck I, with the rod in either hand.
- The second catch is a copy of the first.
- Stacks with the Luck of the Sea enchantment.

## ardanborn:tides_fury
- Tridents count when thrown too.
- Stacks with the [Spear of the Havens](pearl-of-osse.md#spear-of-the-havens)' Teleri bonus.

## ardanborn:delvers_instinct
- Active below Y 50 when the sky's light where you stand is below 4, so not in open ravines.
- Haste I, and Night Vision that lasts 16 seconds after you leave so it doesn't flicker.

## ardanborn:forge_hardened
- Hot floors are magma blocks. Burning counts as fire.
- Doesn't stop you catching fire; Fire Resistance still works on top.

## ardanborn:axe_of_durin
- The slow is Slowness I for 2 seconds.

## ardanborn:second_breakfast
- The extra saturation is +2. Both are capped at full.

## ardanborn:fortunate_folk
- Luck I. It improves fishing treasure and some chest loot.

## ardanborn:tough_as_old_tree_roots
- The Wither reduction covers the Wither effect: wither skeletons, wither roses, the Wither's skulls' effect.
- Slowness from potions, arrows and mobs is shortened, but not from other ArdanBorn or Origins powers.

## ardanborn:battle_fury
- Speed I and Strength I. Killing players counts too.
- Kills in a row refresh the timers.

## ardanborn:iron_stomach
- Blocks Hunger, Poison and Nausea from food, including raw chicken, pufferfish and suspicious stew.
- Effects from potions or mobs still apply.

## ardanborn:moonbound
- Night is time 13000–23000, only in the Overworld; the sky needn't be visible.
- Waxing and waning gibbous moons both count. Speed I and Resistance I.
- The new moon still gives Strength I.
- The moon phase is the in-game one; check it in the sky or with `/time query day` (day mod 8: 0 = full).

## ardanborn:elven_wrought
- Shields, elytra and fishing rods count too.
- Stacks with Unbreaking.

## ardanborn:wrath_of_the_noldor
- Undead: zombies and their variants, skeletons and their variants, the Wither, phantoms, zoglins, zombified piglins and undead horses.
- Creatures of the Nether: blazes, ghasts, magma cubes, hoglins, piglins and piglin brutes.
- Arrows and other projectiles don't get the bonus.

## ardanborn:star_of_earendil
- Regeneration I and Night Vision, at night in the Overworld.
- A solid roof or a leaf canopy overhead turns it off; glass lets the sky through.
- Stacks with [Healer of Imladris](#healer-of-imladris), which makes the regeneration heal 50% more.

## ardanborn:healer_of_imladris
- Works on Instant Health and Regeneration from any source, including beacons, [Star of Eärendil](#star-of-eärendil), and a Healer's [Hands of the Healer](#hands-of-the-healer) and [Healing Presence](#healing-presence).
- Natural regeneration from a full hunger bar isn't boosted.

## ardanborn:swift_messenger
- Speed I. Works on camels and llamas too.

## ardanborn:first_strike
- Every melee hit that lands restarts the 3-second timer.

## ardanborn:hardened_veteran
- The new hearts start empty and fill as you heal.
- +25% knockback resistance, which stacks with netherite armor's own.

## ardanborn:cleave
- Reaches one mob within 2.5 blocks of the target.
- Players, pets and villagers are never hit.

## ardanborn:hunt_prey
- Only mobs can be prey, never players. The glow shows through walls.
- A new arrow hit on another mob moves the mark there; the old prey stops glowing.
- The arrow that marks a mob gets the bonus only if that mob was already your prey.

## ardanborn:wayfarer
- Saturation is used as normal; this saves hunger points.

## ardanborn:shield_wall
- The shield can be in either hand.
- Several Defenders nearby don't stack.
- Void and `/kill` damage are never reduced.

## ardanborn:hands_of_the_healer
- **Heal an ally:** Regeneration II lasts 8 seconds. Your main hand must be empty.
- **Holding back a wound:** your touch restarts a [Morgul wound](morgul-wound.md)'s timer, so it takes a full stage longer to worsen. It doesn't cure the wound; athelas or miruvor are still needed. The 10-minute limit is per wounded player, whichever Healer it was.
- **Channel:** undead within 8 blocks take 3 hearts of magic damage. It doesn't cure Poison or Wither.
- Channel's healing counts as magic, so a Peredhil's [Healer of Imladris](#healer-of-imladris) makes it heal 4.5 hearts for them.
- The two cooldowns are separate, and your action bar shows the time left.

## ardanborn:light_of_the_staff
- The glow lasts 5 seconds, and the undead burn for 4.
- The damage is magic, so it ignores armor and melee bonuses such as [Wrath of the Noldor](#wrath-of-the-noldor) don't apply. Kills still count as yours.
- The cooldown shows on your action bar.

## ardanborn:friend_of_beasts
- The beasts are wolves, bees, polar bears, llamas, trader llamas, pandas and goats.
- The damage reduction covers every animal you've tamed (wolves, cats, parrots, horses, donkeys, mules, llamas, camels…), from any source, while you're online, wherever it is. Void and `/kill` damage aren't reduced.
- The healing reaches 16 blocks. Pets at full health are skipped.
- [Huan](#call-of-huan) counts as your pet, so he gets both.

## ardanborn:mending_craft
- Only the item in your main hand counts as held. Each armor piece is repaired.
- Stacks with Mending.

## ardanborn:backstab
- "From behind" means you're facing the same way as your target, within about 60°.
- The larger bonus is only against mobs that aren't targeting you, including one that's chasing someone else.
- Critical-hit particles show when it lands.
- Pairs with [Unseen](#unseen).

## ardanborn:quiet_steps
- Speed I, which offsets part of the sneaking slowdown.

## ardanborn:unseen
- A mob you hit can always fight back.
- It stops mobs from noticing you; one that's already chasing you may keep chasing after you start sneaking.

## ardanborn:songs_of_power
- "Allies" below means you and every player within 24 blocks.
- The cooldown shows on your action bar, and the song's name shows when you play it.
- Hearing the horn isn't required; range is measured from you. Custom horns such as the Horn of Gondor don't play songs.

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

## ardanborn:silver_tongue
- Hero of the Village I, while one is within 8 blocks. It fades 3 seconds after you walk away.

## origins:aerial_combatant
- ×2 damage.
- Stacks with [Aeglos](feather-of-thorondor.md#aeglos)' bonus against glowing foes.

## origins:aqua_affinity
- Works like the helmet enchantment, without needing the helmet.

## origins:cat_vision
- The effect is Night Vision. Drinking milk doesn't remove it for long; the power re-applies it.

## origins:elytra
- Jump, then press jump again in mid-air.
- Firework rockets boost you as with a real elytra.
- You can still wear a chestplate.

## origins:launch_into_air
- **Sneak while gliding** to launch straight up. It won't fire while you're standing on the ground.
- Use it to gain height without rockets.

## origins:extra_reach
- +1.5 blocks of reach for both blocks and entities.

## origins:fall_immunity
- Includes falls after [Gift of the Winds](#gift-of-the-winds) or a long glide that ends on the ground.
- Landing on a magma block still burns.

## origins:shulker_inventory
- By default you open them by right-clicking your chestplate slot.

## origins:sprint_jump
- Jump Boost II while sprinting.

## origins:strong_arms
- You still get the block's drop. Works on stone, deepslate, tuff, granite, diorite, andesite, sandstones, blackstone and netherrack.

## origins:tailwind
- +20% walking speed.

## fantasyorigins:double_health
- +10 max hearts, for 20 in all.

## fantasyorigins:fortune_increaser
- As if your tool had 2 more levels of Fortune.

## fantasyorigins:small_body
- You fit through 1-block gaps.

## fantasyorigins:strong_skin
- Damage from arrows and other projectiles is divided by 4.

## magicorigins:kill_boost
- Stronger creatures give more.

## magicorigins:potion_master
- One level stronger, and they last twice as long.

## magicorigins:regeneration_when_still
- Regeneration I, after a moment. Moving even slightly ends it.

## magicorigins:silent
- Covers landing and drinking too, and sculk shriekers as well as sensors.

## moborigins:queen_bee
- Includes honeycomb, from a nest or hive, even without a campfire.

## moborigins:rideable_creature
- They right-click you to climb on.
- You can carry a friend while gliding as an Eagle.

## monsterorigins:infinite_arrows
- Works with tipped and spectral arrows.

## ardanborn:healing_presence
- Players already at full health are skipped, so the pulse is never wasted on them.
- Counts as magic healing, so a Peredhil's [Healer of Imladris](#healer-of-imladris) makes it heal 1.5 hearts for them.
- Several Healers stack: each one pulses on their own timer.

## ardanborn:call_of_huan
- Click in the air or at a block. The bone isn't used up, and taming ordinary wolves with bones still works as normal.
- Huan is a tamed wolf that follows and fights for you, half again the size of a wolf, with 30 hearts, 4 hearts of bite damage (double a wolf's), 20% more speed and half knockback resistance.
- The cooldown starts when you call him and shows on your action bar.
- He leaves early if you log out, die or call him again. He's never saved with the world, so a restart can't leave him behind.
- He can't be fed, bred or dyed; right-click him with an empty hand to make him sit or stand. He drops nothing if killed.

## ardanborn:green_thumb
- Every second, each crop within 5 blocks of you (2 blocks up or down) has a 5% chance to grow a stage: wheat, carrots, potatoes, beetroots, nether wart, sweet berries, cocoa, torchflowers, and melon and pumpkin stems. Wheat goes from seed to ripe in about 2–3 minutes.
- Each sapling or mangrove propagule in range has a 1% chance per second to grow as if bone-mealed.
- Pitcher plants aren't affected.
- Crops only grow while you're nearby; they still need light and farmland as usual to grow on their own.

## ardanborn:smiths_mark
- **Cirth runes:** anyone can craft the rune-stones and bring them to you. See the [Cirth Runes](cirth-runes.md) page.
- **Under a full moon:** craft at night in the Overworld, with nothing at all overhead.
  - Tools and weapons are pickaxes, axes, shovels, hoes, swords, spears, maces, bows, crossbows, tridents and fishing rods. Armor isn't blessed.
  - Every moonlit enchantment starts one level above its vanilla maximum and goes up to **5 levels above it**: Sharpness VI–X, Fortune IV–VIII, Unbreaking IV–VIII, Knockback III–VII, Fire Aspect III–VII. Lower levels are likelier; for Fortune, IV is 5 times as likely as VIII.
  - Quick Charge and Lure stop at V, because higher levels break crossbows and fishing.
  - The roll shows in the result slot before you take the item, and taking the ingredients out and putting them back gives the same roll. Each item you take rolls a new one.
  - **Twice per full moon.** Each moonlit item you take counts, and your action bar shows how many are left. The count is tracked on your character, so logging out or a restart doesn't reset it, and it starts over at the next full moon. After the second, crafting is ordinary until then.
  - Moonlit items must be taken one click at a time; shift-click is refused for them so it can't forge several at once (the preview would show the blessing on each).
  - Check the moon in the sky, or with `/time query day` (full moon when the day divided by 8 leaves 0).
  - Crafter blocks never bless an item, since no player is crafting.

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

Only enchantments with levels are in the draw, so there's no Silk Touch, Mending, Infinity, Flame, Multishot or Channeling. Only those that fit the item count, so the odds depend on the item. For a diamond pickaxe the choices are Efficiency (10), Unbreaking (5) and Fortune (1): 63% Efficiency, 31% Unbreaking, 6% Fortune.
