---
title: "Powers"
entries: true
---
Every power an ArdanBorn origin can have, in two lists, each A–Z: [ArdanBorn's own](#ardanborn-powers), and [the ones it borrows](#origins-reborn-and-addon-powers) from Origins-Reborn and its addons. See [Installation](installation.md) for which addons to install.

## ArdanBorn powers

### Axe of Durin

Dwarves fight best with the axe. Your axe blows deal 20% more damage and briefly slow your foe.

- ×1.2 damage on melee hits with any axe.
- The target gets Slowness I for 2 seconds.

`ardanborn:axe_of_durin` · from ArdanBorn · used by [Naugrim (Dwarf)](ancestries.md#naugrim-dwarf)

### Backstab

Melee hits from behind your target deal 35% more damage, or 50% more if it hasn't noticed you.

- ×1.35 damage on melee hits when you face the same way as your target, within about 60°: in other words, from behind.
- ×1.5 instead if the target is a mob that isn't targeting you: it hasn't noticed you, or it's chasing someone else.
- Critical-hit particles show when it lands.
- Pairs with [Unseen](#unseen): sneak up on a mob that hasn't seen you for the bigger bonus.

`ardanborn:backstab` · from ArdanBorn · used by [Burglar](classes.md#burglar)

### Bane of Mirkwood

Elven archers of the forest realms. Your arrows deal 20% more damage while you're in a forest, taiga, jungle or grove, and 50% more to spiders and cave spiders.

- Applies to arrows, spectral arrows and tridents you throw.
- ×1.5 against spiders and cave spiders.
- ×1.2 while you are standing in a woodland biome (the same list as [Woodland Stride](#woodland-stride)).
- The two stack: ×1.8 against a spider in the forest.

`ardanborn:bane_of_mirkwood` · from ArdanBorn · used by [Sindar (Wood Elf)](ancestries.md#sindar-wood-elf)

### Battle-fury

Each kill fills you with fury: Speed for 8 seconds and Strength for 5 seconds.

- Each kill (any mob or player) gives Speed I for 8 seconds and Strength I for 5 seconds.
- Kills in a row refresh the timers.

`ardanborn:bloodlust` · from ArdanBorn · used by [Uruk-hai (Orc)](ancestries.md#uruk-hai-orc)

### Breath of the Sea

The Teleri are born of the shore. You can breathe underwater indefinitely.

- Your air never goes down, so you can stay underwater as long as you like. The bubble bar never even starts to drop.

`ardanborn:falmari_lungs` · from ArdanBorn · used by [Teleri (Sea Elf)](ancestries.md#teleri-sea-elf)

### Call of Huan

Right-click with a bone to call Huan, the great hound of Valinor, to your side for 60 seconds. 3 minute cooldown.

- Right-click with a bone (in the air or at a block) to call **Huan**, the hound of Valinor. The bone isn't used up.
- Huan is a tamed wolf that follows and fights for you, half again the size of a wolf, with 30 hearts, 4 hearts of bite damage (double a wolf's), 20% more speed and half knockback resistance.
- He stays 60 seconds, then fades away. 3-minute cooldown from when you call him, shown on your action bar.
- He leaves early if you log out, die or call him again. He's never saved with the world, so a restart can't leave him behind.
- He can't be fed, bred or dyed; right-click him with an empty hand to make him sit or stand. He drops nothing if killed.
- Taming ordinary wolves with bones still works as normal.

`ardanborn:call_of_huan` · from ArdanBorn · used by [Beastfriend](classes.md#beastfriend)

### Cleave

Sword and axe blows carry through: a hostile mob next to your target takes half the damage.

- Sword and axe hits splash half the damage dealt onto one other hostile mob next to the target (within 2.5 blocks).
- Only hostile mobs are hit, never players, pets or villagers.

`ardanborn:cleave` · from ArdanBorn · used by [Warrior](classes.md#warrior)

### Deep Lore

The Noldor are masters of craft and lore. Enchanting costs you one fewer experience level.

- Each enchanting-table option costs 1 level less, but never less than its slot number (1, 2 or 3 levels).
- Only the level cost changes. The lapis cost and the enchantments you get are the same.

`ardanborn:eldar_lore` · from ArdanBorn · used by [Noldor (High Elf)](ancestries.md#noldor-high-elf)

### Delver's Instinct

In deep, unlit tunnels (below sea level, out of the sky's light) you gain Haste and can see by the touch of stone.

- Active below Y 50 when the sky's light where you stand is below 4: inside caves and tunnels, not in open ravines.
- Haste I, and Night Vision that lasts 16 seconds after you leave so it doesn't flicker.

`ardanborn:delvers_instinct` · from ArdanBorn · used by [Naugrim (Dwarf)](ancestries.md#naugrim-dwarf)

### Elven-wrought

Elven craft endures. Your tools, weapons and armor have a 40% chance to take no wear when used.

- Each time an item would lose durability, 40% chance it doesn't.
- Covers tools, weapons, armor, shields, elytra and fishing rods.
- Stacks with Unbreaking.

`ardanborn:elven_wrought` · from ArdanBorn · used by [Noldor (High Elf)](ancestries.md#noldor-high-elf)

### First Strike

Your opening blow after 3 seconds without attacking deals 50% more damage.

- Your first melee hit after 3 seconds without attacking deals ×1.5 damage.
- Every melee hit that lands restarts the 3-second timer.

`ardanborn:first_strike` · from ArdanBorn · used by [Warrior](classes.md#warrior)

### Forge-hardened

Long years at the forge have toughened you. You take 40% less damage from fire, lava and hot floors.

- ×0.6 damage from fire, burning, lava and magma blocks.
- Doesn't stop you catching fire; Fire Resistance still works on top.

`ardanborn:forge_hardened` · from ArdanBorn · used by [Naugrim (Dwarf)](ancestries.md#naugrim-dwarf)

### Fortunate Folk

Luck follows Hobbits about. You have a permanent Luck effect, improving fishing and loot.

- Permanent Luck I, refreshed every second.
- Luck improves fishing treasure and some chest loot.

`ardanborn:fortunate_folk` · from ArdanBorn · used by [Hobbit (Halfling)](ancestries.md#hobbit-halfling)

### Friend of Beasts

Wild beasts such as wolves, bees and bears won't pick you as a target unless you strike first. Your tamed animals take half damage and heal 1 heart every 10 seconds while near you.

- Wolves, bees, polar bears, llamas, trader llamas, pandas and goats won't target you on their own.
- If you hit one, it can fight back as normal.
- Every animal you've tamed (wolves, cats, parrots, horses, donkeys, mules, llamas, camels…) takes ×0.5 damage from any source while you're online, wherever it is. Void and `/kill` damage aren't reduced.
- Every 10 seconds, your tamed animals within 16 blocks heal 1 heart, with a few heart particles. Pets at full health are skipped.
- [Huan](#call-of-huan) counts as your pet, so he gets both.

`ardanborn:gentle_friend` · from ArdanBorn · used by [Beastfriend](classes.md#beastfriend)

### Gift of Men

Men are quick to learn and adapt. You gain 25% more experience from all sources.

- Every experience orb or reward is multiplied by 1.25, rounded up, so even 1-point orbs give 2.
- Works on all sources: mobs, ore, smelting, breeding, fishing and trading.

`ardanborn:gift_of_men` · from ArdanBorn · used by [Edain (Man)](ancestries.md#edain-man)

### Green Thumb

Living things flourish around you. Crops, berries and saplings within 5 blocks grow much faster.

- Every second, each crop within 5 blocks of you (2 blocks up or down) has a 5% chance to grow a stage: wheat, carrots, potatoes, beetroots, nether wart, sweet berries, cocoa, torchflowers, and melon and pumpkin stems. Wheat goes from seed to ripe in about 2–3 minutes.
- Each sapling or mangrove propagule in range has a 1% chance per second to grow as if bone-mealed.
- Pitcher plants aren't affected.
- Crops only grow while you're nearby; they still need light and farmland as usual to grow on their own.

`ardanborn:green_thumb` · from ArdanBorn · used by [Herbalist](classes.md#herbalist)

### Hands of the Healer

Sneak and right-click an ally with an empty hand to grant Regeneration II and cure Poison and Wither, and hold back a Morgul wound (15s cooldown; a wound can be held back once every 10 minutes). Do the same in the air to channel healing light: you and players within 8 blocks heal 3 hearts, and the undead nearby are seared (60s).

- **Heal an ally:** sneak and right-click a player with an empty main hand. They get Regeneration II for 8 seconds and are cured of Poison and Wither. 15-second cooldown.
- **Holding back a wound:** if they carry a [Morgul wound](morgul-wound.md), your touch restarts its timer, so it takes a full stage longer to worsen. It doesn't cure the wound; athelas or miruvor are still needed. Each wounded player can be held back once every 10 minutes, by any Healer.
- **Channel:** sneak and right-click the air with an empty hand. You and every player within 8 blocks instantly heal 3 hearts, and undead within 8 blocks take 3 hearts of magic damage. 60-second cooldown.
- Channel's healing counts as magic, so a Peredhil's [Healer of Imladris](#healer-of-imladris) makes it heal 4.5 hearts for them. It doesn't cure Poison or Wither; the ally touch does.
- The two cooldowns are separate, and your action bar shows the time left.

`ardanborn:kings_hands` · from ArdanBorn · used by [Healer](classes.md#healer)

### Hardened Veteran

Long years of war have toughened you. You have 2 extra hearts and are harder to knock back.

- +2 max hearts (4 health), always. The new hearts start empty and fill as you heal.
- +25% knockback resistance, so hits push you back less. Stacks with netherite armor's own resistance.

`ardanborn:hardened_veteran` · from ArdanBorn · used by [Warrior](classes.md#warrior)

### Healer of Imladris

Like Elrond, you have the healer's gift. Healing potions, Regeneration and golden apples heal you 50% more.

- ×1.5 healing from Instant Health and from Regeneration, whatever the source: potions, golden apples, beacons, [Star of Eärendil](#star-of-eärendil), or a Healer's [Hands of the Healer](#hands-of-the-healer) and [Healing Presence](#healing-presence).
- Natural regeneration from a full hunger bar isn't boosted.

`ardanborn:healer_of_imladris` · from ArdanBorn · used by [Peredhil (Half-elf)](ancestries.md#peredhil-half-elf)

### Healing Presence

Your presence mends those around you. Every 10 seconds, you and every player within 6 blocks heal 1 heart.

- Every 10 seconds, you and every player within 6 blocks heal 1 heart, with a few heart particles.
- Players already at full health are skipped, so the pulse is never wasted on them.
- Counts as magic healing, so a Peredhil's [Healer of Imladris](#healer-of-imladris) makes it heal 1.5 hearts for them.
- Several Healers stack: each one pulses on their own timer.

`ardanborn:healing_presence` · from ArdanBorn · used by [Healer](classes.md#healer)

### Hope Unquenched

When a blow would leave you below 30% health, you gain Resistance and Regeneration for 8 seconds. Once every 90 seconds.

- Triggers on any damage that leaves you alive but at or below 30% of your max health. A killing blow doesn't trigger it.
- Grants Resistance I and Regeneration I for 8 seconds, with a golden flash and "Hope unquenched!" on your action bar.
- 90-second cooldown, which starts when it triggers.

`ardanborn:last_stand` · from ArdanBorn · used by [Edain (Man)](ancestries.md#edain-man)

### Hunt Prey

The last creature your arrow struck becomes your prey for 60 seconds: it glows, and you deal 20% more damage to it from any source.

- The last mob your arrow hit is your **prey** for 60 seconds: it glows (visible through walls), and you deal ×1.2 damage to it from any source, arrows and melee alike.
- A new arrow hit on another mob moves the mark there; the old prey stops glowing.
- The arrow that marks a mob gets the bonus only if that mob was already your prey.
- Players are never marked.

`ardanborn:keen_tracker` · from ArdanBorn · used by [Ranger](classes.md#ranger)

### Iron Stomach

Spoiled and foul food cannot harm you. Rotten flesh, spider eyes, poisonous potatoes and the like give no bad effects.

- Hunger, Poison and Nausea **from food** never apply: rotten flesh, spider eyes, poisonous potatoes, raw chicken, pufferfish, suspicious stew.
- Effects from potions or mobs still apply.

`ardanborn:iron_stomach` · from ArdanBorn · used by [Uruk-hai (Orc)](ancestries.md#uruk-hai-orc)

### Leaf-walker

The Silvan Elves run along the treetops. You take no fall damage landing on leaves, and on leaves you gain Speed and Jump Boost.

- Landing on any kind of leaves (including azalea and mangrove leaves) causes no fall damage, from any height.
- While standing on leaves you get Speed II and Jump Boost II, refreshed each second and lasting 2 seconds after you step off.
- Leaves are still solid, as for everyone; the power is about running *on* the canopy.

`ardanborn:leaf_walker` · from ArdanBorn · used by [Sindar (Wood Elf)](ancestries.md#sindar-wood-elf)

### Lembas

The waybread of the Elves: bread fills you completely and briefly heals you. Once a minute.

- Eating bread fills your hunger and saturation completely and gives Regeneration I for 5 seconds.
- Once a minute. Bread eaten while it's on cooldown works as normal bread.

`ardanborn:lembas` · from ArdanBorn · used by [Sindar (Wood Elf)](ancestries.md#sindar-wood-elf)

### Light of the Staff

Right-click with a Blaze Rod as your staff to drive back the shadow: hostile creatures within 6 blocks take 3 hearts of damage and are thrown back and revealed, and the undead take double and are set alight. 15 second cooldown.

- Right-click with a blaze rod. Every hostile mob within 6 blocks takes 3 hearts of magic damage, is knocked away from you and glows for 5 seconds.
- Undead among them take 6 hearts instead and are set on fire for 4 seconds.
- The damage is magic, so it ignores armor and melee bonuses such as [Wrath of the Noldor](#wrath-of-the-noldor) don't apply. Kills still count as yours.
- 15-second cooldown, shown on your action bar.

`ardanborn:wizard_staff` · from ArdanBorn · used by [Loremaster](classes.md#loremaster)

### Mending Craft

Your own good work endures. Your held item and worn armor slowly repair themselves (1 durability every 10 seconds).

- Every 10 seconds, the item in your main hand and each worn armor piece regain 1 durability.
- Stacks with Mending.

`ardanborn:master_smith` · from ArdanBorn · used by [Smith](classes.md#smith)

### Moonbound

By night the skin-changer's strength rises: Strength I. Under a gibbous moon you also gain Speed, and under a full moon you gain Strength II, Speed and Resistance.

- Night only (time 13000–23000), only in the Overworld; the sky needn't be visible.
- Full moon: Strength II, Speed I and Resistance I.
- Waxing or waning gibbous: Strength I and Speed I.
- Any other phase, including the new moon: Strength I.
- The moon phase is the in-game one; check it in the sky or with `/time query day` (day mod 8: 0 = full).

`ardanborn:moonbound` · from ArdanBorn · used by [Beorning (Skin-changer)](ancestries.md#beorning-skin-changer)

### Pearls of Alqualondë

The sea gives up its treasures to the Teleri. While you hold a fishing rod you have Luck, and each catch has a 25% chance to bring up a second.

- Luck I while a fishing rod is in either hand, which improves the treasure you catch.
- Each successful catch has a 25% chance to bring up a copy of the item.
- Stacks with the Luck of the Sea enchantment.

`ardanborn:pearls_of_alqualonde` · from ArdanBorn · used by [Teleri (Sea Elf)](ancestries.md#teleri-sea-elf)

### Quiet Steps

You move a little faster while sneaking.

- Speed I while sneaking, which offsets part of the sneaking slowdown.

`ardanborn:quiet_steps` · from ArdanBorn · used by [Burglar](classes.md#burglar)

### Sea's Vigor

The sea strengthens its children. While you stand in water or rain you have 2 extra hearts.

- +2 max hearts (4 health) while you are in water or standing in rain, checked every second.
- The new hearts start empty and fill as you heal. When you leave the water or the rain stops, they're removed, along with any health in them.
- Stacks with [Tide's Fury](#tides-fury), which uses the same water-or-rain condition.

`ardanborn:seas_vigor` · from ArdanBorn · used by [Teleri (Sea Elf)](ancestries.md#teleri-sea-elf)

### Second Breakfast

Hobbits know how to enjoy a meal. Eating food restores 1 extra hunger point and a little extra saturation.

- Each time you eat, +1 hunger point and +2 saturation on top of what the food gives, capped at full.

`ardanborn:second_breakfast` · from ArdanBorn · used by [Hobbit (Halfling)](ancestries.md#hobbit-halfling)

### Shield Wall

While you hold a shield you take 15% less damage, and allies within 5 blocks take 10% less.

- ×0.85 damage to you while a shield is in either hand.
- Players within 5 blocks of you take ×0.9 damage while you hold a shield. Several Champions nearby don't stack.
- Void and `/kill` damage are never reduced.

`ardanborn:shield_wall` · from ArdanBorn · used by [Defender](classes.md#defender)

### Silver Tongue

Villagers and wandering traders warm to you. Near them you gain Hero of the Village for cheaper trades.

- Hero of the Village I while a villager or wandering trader is within 8 blocks, so their prices drop.
- Refreshed every second; it fades 3 seconds after you walk away.

`ardanborn:silver_tongue` · from ArdanBorn · used by [Minstrel](classes.md#minstrel)

### Smith's Mark

Only you can inscribe Cirth runes onto weapons and tools. Twice each full moon, a tool or weapon you forge under its open sky gains an enchantment beyond its usual maximum, by up to 5 levels.

- **Cirth runes:** only a Smith can inscribe runes onto weapons and tools at a smithing table. Anyone can craft the rune-stones and bring them to you. See the [Cirth Runes](cirth-runes.md) page.
- **Under a full moon:** a tool or weapon crafted at night during a full moon, in the Overworld, while you stand under open sky (nothing at all overhead), is **enchanted beyond the usual maximum**, twice per full moon.
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

`ardanborn:smiths_mark` · from ArdanBorn · used by [Smith](classes.md#smith)

### Songs of Power

Sound a Goat Horn to play the song its tune carries: rest, valor, seeking or the walking song from common horns, and courage, gathering, hope or Lúthien's lullaby from a screaming goat's. Songs reach players within 24 blocks. 90 second cooldown shared by all songs.

- Use a goat horn. The song depends on the horn's tune. "Allies" means you and every player within 24 blocks.
- One 90-second cooldown covers every song, shown on your action bar. The song's name shows when you play it.
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

`ardanborn:songs_of_power` · from ArdanBorn · used by [Minstrel](classes.md#minstrel)

### Star of Eärendil

Your father sails the night sky as the evening star. Beneath it you slowly heal and see clearly.

- At night in the Overworld, while you stand under open sky: Regeneration I and Night Vision.
- A solid roof or a leaf canopy overhead turns it off; glass lets the sky through.
- Stacks with [Healer of Imladris](#healer-of-imladris), which makes the regeneration heal 50% more.

`ardanborn:star_of_earendil` · from ArdanBorn · used by [Peredhil (Half-elf)](ancestries.md#peredhil-half-elf)

### Swift Messenger

Like Glorfindel riding for the Ford: horses, donkeys and mules you ride run faster and take no fall damage.

- The horse, donkey, mule, camel or llama you ride gets Speed I.
- It takes no fall damage while you ride it.

`ardanborn:swift_messenger` · from ArdanBorn · used by [Peredhil (Half-elf)](ancestries.md#peredhil-half-elf)

### Tide's Fury

The strength of the sea is in you. While you stand in water or rain, your melee and trident hits deal 25% more damage.

- ×1.25 damage on melee hits and on tridents you throw, while you are in water or rain.
- Stacks with the [Spear of the Havens](pearl-of-osse.md#spear-of-the-havens)' Teleri bonus.

`ardanborn:tides_fury` · from ArdanBorn · used by [Teleri (Sea Elf)](ancestries.md#teleri-sea-elf)

### Tough as Old Tree-roots

Hobbits are hard to break, as Frodo bore the Ring. You take 30% less damage from Wither, Slowness lasts half as long on you, and Darkness cannot touch you.

- ×0.7 damage from the Wither effect (wither skeletons, wither roses, the Wither's skulls' effect).
- Slowness from potions, arrows and mobs lasts half as long. Slowness from other ArdanBorn or Origins powers isn't shortened.
- Darkness, from Wardens and sculk shriekers, never applies.

`ardanborn:tree_roots` · from ArdanBorn · used by [Hobbit (Halfling)](ancestries.md#hobbit-halfling)

### Undying Body

The Eldar do not sicken as Men do. You are immune to Poison and Hunger effects.

- Poison and Hunger are blocked from every source: food, potions, cave spiders, husks, witches.
- Other harmful effects such as Wither, Weakness and Slowness still apply.

`ardanborn:eldar_body` · from ArdanBorn · used by [Noldor (High Elf)](ancestries.md#noldor-high-elf), [Peredhil (Half-elf)](ancestries.md#peredhil-half-elf)

### Unseen

While you sneak, hostile creatures more than 6 blocks away don't notice you.

- While you sneak, hostile mobs more than 6 blocks away can't pick you as a target.
- Within 6 blocks they notice you as normal, and a mob you hit can always fight back.
- It stops mobs from noticing you; one that's already chasing you may keep chasing after you start sneaking.

`ardanborn:unseen` · from ArdanBorn · used by [Burglar](classes.md#burglar)

### Wayfarer

Long roads are your home. A quarter of the time, you lose no hunger when you would have.

- Each time your hunger bar would drop, 25% chance it doesn't.
- Saturation is used as normal; this saves hunger points.

`ardanborn:wayfarer` · from ArdanBorn · used by [Ranger](classes.md#ranger)

### Woodland Stride

You move with the speed of the wood. Gain Speed while in forests, taigas, jungles and groves.

- Speed I while you stand in any biome whose name includes *forest*, *taiga*, *jungle*, *grove* or *woods*: birch forests, dark forests, old growth taigas, bamboo jungles, cherry groves and so on.
- Checked every second; the effect lingers 3 seconds after you leave.
- The same biomes count for [Bane of Mirkwood](#bane-of-mirkwood).

`ardanborn:woodland_stride` · from ArdanBorn · used by [Sindar (Wood Elf)](ancestries.md#sindar-wood-elf)

### Wrath of the Noldor

The Noldor have warred on Morgoth since the First Age. Your melee hits deal 25% more damage to the undead and the creatures of the Nether.

- ×1.25 melee damage against the undead: zombies and their variants, skeletons and their variants, the Wither, phantoms, zoglins, zombified piglins and undead horses.
- Also against the creatures of the Nether: blazes, ghasts, magma cubes, hoglins, piglins and piglin brutes.
- Arrows and other projectiles don't get the bonus.

`ardanborn:wrath_of_the_noldor` · from ArdanBorn · used by [Noldor (High Elf)](ancestries.md#noldor-high-elf)

## Origins-Reborn and addon powers

### Acrobatics

You never take fall damage, no matter from which height you fall.

- No fall damage from any height, including after [Gift of the Winds](#gift-of-the-winds) or a long glide that ends on the ground.
- Landing on a magma block still burns.

`origins:fall_immunity` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Great Eagle](ancestries.md#great-eagle)

### Aerial Combatant

You deal substantially more damage while in Elytra flight.

- ×2 damage while you are gliding.
- Stacks with [Aeglos](feather-of-thorondor.md#aeglos)' bonus against glowing foes.

`origins:aerial_combatant` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Great Eagle](ancestries.md#great-eagle)

### Aqua Affinity

You may break blocks underwater as others do on land.

- Mine underwater at normal speed, as with the Aqua Affinity helmet enchantment, without needing the helmet.

`origins:aqua_affinity` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Teleri (Sea Elf)](ancestries.md#teleri-sea-elf)

### Bearer

Other players can ride on your back.

- Other players can right-click you to ride on your back.
- Carry a friend while gliding as an Eagle.

`moborigins:rideable_creature` · from [Origins-Mobs](https://modrinth.com/plugin/origins-mobs) · used by [Great Eagle](ancestries.md#great-eagle)

### Bee-friend

Bees won't turn on you when you collect honey.

- Bees don't get angry when you take honey or honeycomb from their nest or hive, even without a campfire.

`moborigins:queen_bee` · from [Origins-Mobs](https://modrinth.com/plugin/origins-mobs) · used by [Beorning (Skin-changer)](ancestries.md#beorning-skin-changer)

### Careful Miner

Your care and mastery in the art of extracting minerals results in a much higher yield from ores than other creatures.

- Ores you mine drop as if your tool had 2 more levels of Fortune.

`fantasyorigins:fortune_increaser` · from [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy) · used by [Naugrim (Dwarf)](ancestries.md#naugrim-dwarf)

### Endless Quiver

Arrows you shoot are never used up.

- Arrows you shoot aren't used up, as with the Infinity enchantment.
- Works with tipped and spectral arrows.

`monsterorigins:infinite_arrows` · from [Origins-Monsters](https://modrinth.com/plugin/origins-monsters) · used by [Ranger](classes.md#ranger)

### Gift of the Winds

Every 30 seconds, you are able to launch about 20 blocks up into the air.

- **Sneak while gliding** to launch straight up, about 20 blocks.
- 30-second cooldown.
- Use it to gain height without rockets. It won't fire while you're standing on the ground.

`origins:launch_into_air` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Great Eagle](ancestries.md#great-eagle)

### Healing Focus

When standing still you focus your energy to regenerate health.

- Stand still for a moment to gain Regeneration I. Moving even slightly ends it.

`magicorigins:regeneration_when_still` · from [Origins-Magic](https://modrinth.com/plugin/origins-magic) · used by [Loremaster](classes.md#loremaster)

### Hoarder

You have access to an additional 9 slots of inventory, which keep the items on death.

- 9 extra inventory slots. By default you open them by right-clicking your chestplate slot.
- Items in them are kept when you die.

`origins:shulker_inventory` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Hobbit (Halfling)](ancestries.md#hobbit-halfling)

### Long Reach

You can reach blocks and entities further away than others can.

- +1.5 blocks of reach for both blocks and entities.

`origins:extra_reach` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Edain (Man)](ancestries.md#edain-man)

### Mighty Frame

Your great frame gives you twice the health of others.

- +10 max hearts (20 health), for 20 hearts in all.

`fantasyorigins:double_health` · from [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy) · used by [Beorning (Skin-changer)](ancestries.md#beorning-skin-changer)

### Nocturnal

You can slightly see in the dark when not in water.

- Night Vision whenever you're not in water.
- Drinking milk doesn't remove it for long; the power re-applies it.

`origins:cat_vision` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Noldor (High Elf)](ancestries.md#noldor-high-elf), [Uruk-hai (Orc)](ancestries.md#uruk-hai-orc)

### Potion Master

Potions you drink and throw are much stronger.

- Potions you drink or throw are one level stronger and last twice as long.

`magicorigins:potion_master` · from [Origins-Magic](https://modrinth.com/plugin/origins-magic) · used by [Herbalist](classes.md#herbalist)

### Small Body

You're only one block tall, half the height of most folk.

- Your hitbox and model are half size, about 1 block tall.
- You fit through 1-block gaps.

`fantasyorigins:small_body` · from [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy) · used by [Small Size](sizes.md#small-size)

### Soft-footed

You move so softly that Sculk Sensors never notice you walking or eating.

- Your walking, landing, eating and drinking don't trigger sculk sensors or shriekers.

`magicorigins:silent` · from [Origins-Magic](https://modrinth.com/plugin/origins-magic) · used by [Hobbit (Halfling)](ancestries.md#hobbit-halfling), [Sindar (Wood Elf)](ancestries.md#sindar-wood-elf)

### Spoils of Victory

Whenever you kill something, you take in some of its vitality as extra health, up to 30 extra hearts.

- Each kill adds extra max health, more for stronger creatures.
- Up to 30 extra hearts (60 health).

`magicorigins:kill_boost` · from [Origins-Magic](https://modrinth.com/plugin/origins-magic) · used by [Beorning (Skin-changer)](ancestries.md#beorning-skin-changer)

### Strong Ankles

You are able to jump higher by jumping while sprinting.

- Jump Boost II while sprinting.

`origins:sprint_jump` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Edain (Man)](ancestries.md#edain-man)

### Strong Arms

You are strong enough to break natural stones without using a pickaxe.

- Break natural stone without a pickaxe and still get its drop: stone, deepslate, tuff, granite, diorite, andesite, sandstones, blackstone and netherrack.

`origins:strong_arms` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Uruk-hai (Orc)](ancestries.md#uruk-hai-orc)

### Tailwind

You are a little bit quicker on foot than others.

- +20% walking speed, always.

`origins:tailwind` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Peredhil (Half-elf)](ancestries.md#peredhil-half-elf)

### Thick Skin

Your hide is tough enough that arrows do much less damage to you.

- Damage from arrows and other projectiles is divided by 4.

`fantasyorigins:strong_skin` · from [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy) · used by [Defender](classes.md#defender)

### Winged

You have wings of your own and can glide without equipping an Elytra.

- Glide without wearing an elytra: jump, then press jump again in mid-air.
- Firework rockets boost you as with a real elytra.
- You can still wear a chestplate.

`origins:elytra` · from [Origins-Reborn](https://modrinth.com/plugin/origins-reborn) · used by [Great Eagle](ancestries.md#great-eagle)

