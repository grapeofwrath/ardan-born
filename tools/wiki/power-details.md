# Power details

Extra detail for the wiki's Powers page: exact numbers, how a power is triggered, and how it combines with
other powers and items. `tools/wiki/generate.pl` prints each section under the power's description.

Each section starts with `## <power key>`; everything until the next `## ` is copied as Markdown. Keep the
numbers in step with the code (`OriginAbilities.java`) or, for borrowed powers, with Origins-Reborn's
`ability-config.yml` defaults.

## ardanborn:gift_of_men
- Every experience orb or reward is multiplied by 1.25, rounded up, so even 1-point orbs give 2.
- Works on all sources: mobs, ore, smelting, breeding, fishing and trading.

## ardanborn:last_stand
- Triggers on any damage that leaves you alive but at or below 30% of your max health. A killing blow doesn't trigger it.
- Grants Resistance I and Regeneration I for 8 seconds, with a golden flash and "Hope unquenched!" on your action bar.
- 90-second cooldown, which starts when it triggers.

## ardanborn:eldar_body
- Poison and Hunger are blocked from every source: food, potions, cave spiders, husks, witches.
- Other harmful effects such as Wither, Weakness and Slowness still apply.

## ardanborn:eldar_lore
- Each enchanting-table option costs 1 level less, but never less than its slot number (1, 2 or 3 levels).
- Only the level cost changes. The lapis cost and the enchantments you get are the same.

## ardanborn:woodland_stride
- Speed I while you stand in any biome whose name includes *forest*, *taiga*, *jungle*, *grove* or *woods*: birch forests, dark forests, old growth taigas, bamboo jungles, cherry groves and so on.
- Checked every second; the effect lingers 3 seconds after you leave.
- The same biomes count for [Bane of Mirkwood](#bane-of-mirkwood).

## ardanborn:bane_of_mirkwood
- Applies to arrows, spectral arrows and tridents you throw.
- ×1.5 against spiders and cave spiders.
- ×1.2 while you are standing in a woodland biome (the same list as [Woodland Stride](#woodland-stride)).
- The two stack: ×1.8 against a spider in the forest.

## ardanborn:leaf_walker
- Landing on any kind of leaves (including azalea and mangrove leaves) causes no fall damage, from any height.
- While standing on leaves you get Speed II and Jump Boost II, refreshed each second and lasting 2 seconds after you step off.
- Leaves are still solid, as for everyone; the power is about running *on* the canopy.

## ardanborn:lembas
- Eating bread fills your hunger and saturation completely and gives Regeneration I for 5 seconds.
- Once a minute. Bread eaten while it's on cooldown works as normal bread.

## ardanborn:falmari_lungs
- Your air never goes down, so you can stay underwater as long as you like. The bubble bar never even starts to drop.

## ardanborn:seas_vigor
- +2 max hearts (4 health) while you are in water or standing in rain, checked every second.
- The new hearts start empty and fill as you heal. When you leave the water or the rain stops, they're removed, along with any health in them.
- Stacks with [Tide's Fury](#tides-fury), which uses the same water-or-rain condition.

## ardanborn:pearls_of_alqualonde
- Luck I while a fishing rod is in either hand, which improves the treasure you catch.
- Each successful catch has a 25% chance to bring up a copy of the item.
- Stacks with the Luck of the Sea enchantment.

## ardanborn:tides_fury
- ×1.25 damage on melee hits and on tridents you throw, while you are in water or rain.
- Stacks with the [Spear of the Havens](Pearl-of-Osse#spear-of-the-havens)' Teleri bonus.

## ardanborn:delvers_instinct
- Active below Y 50 when the sky's light where you stand is below 4: inside caves and tunnels, not in open ravines.
- Haste I, and Night Vision that lasts 16 seconds after you leave so it doesn't flicker.

## ardanborn:forge_hardened
- ×0.6 damage from fire, burning, lava and magma blocks.
- Doesn't stop you catching fire; Fire Resistance still works on top.

## ardanborn:axe_of_durin
- ×1.2 damage on melee hits with any axe.
- The target gets Slowness I for 2 seconds.

## ardanborn:second_breakfast
- Each time you eat, +1 hunger point and +2 saturation on top of what the food gives, capped at full.

## ardanborn:fortunate_folk
- Permanent Luck I, refreshed every second.
- Luck improves fishing treasure and some chest loot.

## ardanborn:tree_roots
- ×0.7 damage from the Wither effect (wither skeletons, wither roses, the Wither's skulls' effect).
- Slowness from potions, arrows and mobs lasts half as long. Slowness from other ArdanBorn or Origins powers isn't shortened.
- Darkness, from Wardens and sculk shriekers, never applies.

## ardanborn:bloodlust
- Each kill (any mob or player) gives Speed I for 8 seconds and Strength I for 5 seconds.
- Kills in a row refresh the timers.

## ardanborn:iron_stomach
- Hunger, Poison and Nausea **from food** never apply: rotten flesh, spider eyes, poisonous potatoes, raw chicken, pufferfish, suspicious stew.
- Effects from potions or mobs still apply.

## ardanborn:moonbound
- Night only (time 13000–23000), only in the Overworld; the sky needn't be visible.
- Full moon: Strength II, Speed I and Resistance I.
- Waxing or waning gibbous: Strength I and Speed I.
- Any other phase, including the new moon: Strength I.
- The moon phase is the in-game one; check it in the sky or with `/time query day` (day mod 8: 0 = full).

## ardanborn:elven_wrought
- Each time an item would lose durability, 40% chance it doesn't.
- Covers tools, weapons, armor, shields, elytra and fishing rods.
- Stacks with Unbreaking.

## ardanborn:wrath_of_the_noldor
- ×1.25 melee damage against the undead: zombies and their variants, skeletons and their variants, the Wither, phantoms, zoglins, zombified piglins and undead horses.
- Also against the creatures of the Nether: blazes, ghasts, magma cubes, hoglins, piglins and piglin brutes.
- Arrows and other projectiles don't get the bonus.

## ardanborn:star_of_earendil
- At night in the Overworld, while you stand under open sky: Regeneration I and Night Vision.
- A solid roof or a leaf canopy overhead turns it off; glass lets the sky through.
- Stacks with [Healer of Imladris](#healer-of-imladris), which makes the regeneration heal 50% more.

## ardanborn:healer_of_imladris
- ×1.5 healing from Instant Health and from Regeneration, whatever the source: potions, golden apples, beacons, [Star of Eärendil](#star-of-eärendil), or a Healer's [Hands of the Healer](#hands-of-the-healer) and [Healing Presence](#healing-presence).
- Natural regeneration from a full hunger bar isn't boosted.

## ardanborn:swift_messenger
- The horse, donkey, mule, camel or llama you ride gets Speed I.
- It takes no fall damage while you ride it.

## ardanborn:first_strike
- Your first melee hit after 5 seconds without attacking deals ×1.2 damage.
- Every melee hit that lands restarts the 5-second timer.

## ardanborn:cleave
- Sword and axe hits splash half the damage dealt onto one other hostile mob next to the target (within 2.5 blocks).
- Only hostile mobs are hit, never players, pets or villagers.

## ardanborn:keen_tracker
- Mobs hit by your arrows glow for 5 seconds, visible through walls.
- Players aren't marked.

## ardanborn:wayfarer
- Each time your hunger bar would drop, 25% chance it doesn't.
- Saturation is used as normal; this saves hunger points.

## ardanborn:shield_wall
- ×0.85 damage to you while a shield is in either hand.
- Players within 5 blocks of you take ×0.9 damage while you hold a shield. Several Champions nearby don't stack.
- Void and `/kill` damage are never reduced.

## ardanborn:kings_hands
- **Heal an ally:** sneak and right-click a player with an empty main hand. They get Regeneration II for 8 seconds and are cured of Poison and Wither. 15-second cooldown.
- **Heal yourself:** sneak and right-click the air with an empty hand. Same effect, 45-second cooldown.
- The two cooldowns are separate, and your action bar shows the time left.

## ardanborn:wizard_staff
- Right-click with a blaze rod. Every hostile mob within 6 blocks is knocked away from you and glows for 5 seconds.
- Undead among them are set on fire for 4 seconds.
- 20-second cooldown, shown on your action bar.

## ardanborn:gentle_friend
- Wolves, bees, polar bears, llamas, trader llamas, pandas and goats won't target you on their own.
- If you hit one, it can fight back as normal.

## ardanborn:master_smith
- Every 10 seconds, the item in your main hand and each worn armor piece regain 1 durability.
- Stacks with Mending.

## ardanborn:backstab
- ×1.35 damage on melee hits when you face the same way as your target, within about 60°: in other words, from behind.
- Critical-hit particles show when it lands.

## ardanborn:quiet_steps
- Speed I while sneaking, which offsets part of the sneaking slowdown.

## ardanborn:song_of_valor
- Use any goat horn. You and every player within 16 blocks get Speed I for 20 seconds and Regeneration I for 8 seconds.
- 60-second cooldown, shown on your action bar.
- Hearing the horn isn't required; range is measured from you.

## ardanborn:silver_tongue
- Hero of the Village I while a villager or wandering trader is within 8 blocks, so their prices drop.
- Refreshed every second; it fades 3 seconds after you walk away.

## origins:aerial_combatant
- ×2 damage while you are gliding.
- Stacks with [Aeglos](Feather-of-Thorondor#aeglos)' bonus against glowing foes.

## origins:aqua_affinity
- Mine underwater at normal speed, as with the Aqua Affinity helmet enchantment, without needing the helmet.

## origins:cat_vision
- Night Vision whenever you're not in water.
- Drinking milk doesn't remove it for long; the power re-applies it.

## origins:elytra
- Glide without wearing an elytra: jump, then press jump again in mid-air.
- Firework rockets boost you as with a real elytra.
- You can still wear a chestplate.

## origins:launch_into_air
- **Sneak while gliding** to launch straight up, about 20 blocks.
- 30-second cooldown.
- Use it to gain height without rockets. It won't fire while you're standing on the ground.

## origins:extra_reach
- +1.5 blocks of reach for both blocks and entities.

## origins:fall_immunity
- No fall damage from any height, including after [Gift of the Winds](#gift-of-the-winds) or a long glide that ends on the ground.
- Landing on a magma block still burns.

## origins:shulker_inventory
- 9 extra inventory slots. By default you open them by right-clicking your chestplate slot.
- Items in them are kept when you die.

## origins:sprint_jump
- Jump Boost II while sprinting.

## origins:strong_arms
- Break natural stone without a pickaxe and still get its drop: stone, deepslate, tuff, granite, diorite, andesite, sandstones, blackstone and netherrack.

## origins:tailwind
- +20% walking speed, always.

## fantasyorigins:double_health
- +10 max hearts (20 health), for 20 hearts in all.

## fantasyorigins:fortune_increaser
- Ores you mine drop as if your tool had 2 more levels of Fortune.

## fantasyorigins:small_body
- Your hitbox and model are half size, about 1 block tall.
- You fit through 1-block gaps.

## fantasyorigins:strong_skin
- Damage from arrows and other projectiles is divided by 4.

## magicorigins:kill_boost
- Each kill adds extra max health, more for stronger creatures.
- Up to 30 extra hearts (60 health).

## magicorigins:potion_master
- Potions you drink or throw are one level stronger and last twice as long.

## magicorigins:regeneration_when_still
- Stand still for a moment to gain Regeneration I. Moving even slightly ends it.

## magicorigins:silent
- Your walking, landing, eating and drinking don't trigger sculk sensors or shriekers.

## moborigins:queen_bee
- Bees don't get angry when you take honey or honeycomb from their nest or hive, even without a campfire.

## moborigins:rideable_creature
- Other players can right-click you to ride on your back.
- Carry a friend while gliding as an Eagle.

## monsterorigins:infinite_arrows
- Arrows you shoot aren't used up, as with the Infinity enchantment.
- Works with tipped and spectral arrows.

## ardanborn:healing_presence
- Every 10 seconds, you and every player within 6 blocks heal 1 heart, with a few heart particles.
- Players already at full health are skipped, so the pulse is never wasted on them.
- Counts as magic healing, so a Peredhil's [Healer of Imladris](#healer-of-imladris) makes it heal 1.5 hearts for them.
- Several Healers stack: each one pulses on their own timer.

## ardanborn:call_of_huan
- Right-click with a bone (in the air or at a block) to call **Huan**, the hound of Valinor. The bone isn't used up.
- Huan is a tamed wolf that follows and fights for you, half again the size of a wolf, with 30 hearts, 4 hearts of bite damage (double a wolf's), 20% more speed and half knockback resistance.
- He stays 60 seconds, then fades away. 3-minute cooldown from when you call him, shown on your action bar.
- He leaves early if you log out, die or call him again. He's never saved with the world, so a restart can't leave him behind.
- He can't be fed, bred or dyed; right-click him with an empty hand to make him sit or stand. He drops nothing if killed.
- Taming ordinary wolves with bones still works as normal.

## ardanborn:green_thumb
- Every second, each crop within 5 blocks of you (2 blocks up or down) has a 5% chance to grow a stage: wheat, carrots, potatoes, beetroots, nether wart, sweet berries, cocoa, torchflowers, and melon and pumpkin stems. Wheat goes from seed to ripe in about 2–3 minutes.
- Each sapling or mangrove propagule in range has a 1% chance per second to grow as if bone-mealed.
- Pitcher plants aren't affected.
- Crops only grow while you're nearby; they still need light and farmland as usual to grow on their own.

## ardanborn:smiths_mark
- **Always:** tools, weapons, armor and anything else that can take Unbreaking (shields, bows, fishing rods, shears, flint and steel…) come out of your crafting grid with **Unbreaking I**.
- **Under a full moon:** a tool or weapon crafted at night during a full moon, in the Overworld, while you stand under open sky (nothing at all overhead), is also **enchanted beyond the usual maximum**, once per full moon.
  - Tools and weapons are pickaxes, axes, shovels, hoes, swords, spears, maces, bows, crossbows, tridents and fishing rods. Armor only gets the Unbreaking.
  - Every moonlit enchantment starts one level above its vanilla maximum and goes up to **5 levels above it**: Sharpness VI–X, Fortune IV–VIII, Knockback III–VII, Fire Aspect III–VII. Lower levels are likelier; for Fortune, IV is 5 times as likely as VIII.
  - Quick Charge and Lure stop at V, because higher levels break crossbows and fishing.
  - Unbreaking can be drawn too, raising the baseline Unbreaking I to IV–VIII.
  - The roll shows in the result slot before you take the item, and taking the ingredients out and putting them back gives the same roll. Each item you take rolls a new one.
  - **Once per full moon.** Your action bar tells you when the moon has blessed your work. It's tracked on your character, so logging out or a restart doesn't reset it, and it comes back at the next full moon. After that, crafting gives only Unbreaking I until then.
  - Moonlit items must be taken one click at a time; shift-click is refused for them so it can't forge several at once (the preview would show the blessing on each).
  - Check the moon in the sky, or with `/time query day` (full moon when the day divided by 8 leaves 0).
- Unbreaking I shows in the result slot and works with shift-click crafting. Crafter blocks don't apply it, since no player is crafting.

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
