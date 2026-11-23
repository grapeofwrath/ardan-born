---
title: "Classes"
---
Your **class** is the calling you follow. It is chosen second, in the `class` layer, and combines with any ancestry.

| | Impact | Powers |
|---|---|---|
| [Warrior](#warrior) | Medium | 3 |
| [Ranger](#ranger) | Medium | 3 |
| [Burglar](#burglar) | Low | 3 |
| [Minstrel](#minstrel) | Medium | 2 |
| [Defender](#defender) | Medium | 2 |
| [Healer](#healer) | Low | 2 |
| [Loremaster](#loremaster) | Medium | 2 |
| [Beastfriend](#beastfriend) | Low | 2 |
| [Herbalist](#herbalist) | Low | 2 |
| [Smith](#smith) | Low | 2 |

## Warrior

A veteran soldier of the free peoples. Your first blow lands hard, your blade carries through to the next foe, and long years of war have made you tough.

**Impact:** Medium · **Icon:** `diamond_sword` · **File:** `fighter.json`

| Power | What it does | From |
|---|---|---|
| [First Strike](powers.md#first-strike) | Your opening blow after 3 seconds without attacking deals 50% more damage. | ArdanBorn |
| [Cleave](powers.md#cleave) | Sword and axe blows carry through: a hostile mob next to your target takes half the damage. | ArdanBorn |
| [Hardened Veteran](powers.md#hardened-veteran) | Long years of war have toughened you. You have 2 extra hearts and are harder to knock back. | ArdanBorn |

## Ranger

A Dunadan-style wanderer of the wild lands. Your arrows fly true and reveal your prey, and the long road does not weary you.

**Impact:** Medium · **Icon:** `bow` · **File:** `ranger.json`

| Power | What it does | From |
|---|---|---|
| [Endless Quiver](powers.md#endless-quiver) | Arrows you shoot are never used up. | [Origins-Monsters](https://modrinth.com/plugin/origins-monsters) |
| [Hunt Prey](powers.md#hunt-prey) | The last creature your arrow struck becomes your prey for 60 seconds: it glows, and you deal 20% more damage to it from any source. | ArdanBorn |
| [Wayfarer](powers.md#wayfarer) | Long roads are your home. A quarter of the time, you lose no hunger when you would have. | ArdanBorn |

## Burglar

A quiet, quick-fingered scout. Strike the unwary from behind for extra harm, and slip along on soft feet where foes far off never see you.

**Impact:** Low · **Icon:** `shears` · **File:** `rogue.json`

| Power | What it does | From |
|---|---|---|
| [Backstab](powers.md#backstab) | Melee hits from behind your target deal 35% more damage, or 50% more if it hasn't noticed you. | ArdanBorn |
| [Quiet Steps](powers.md#quiet-steps) | You move a little faster while sneaking. | ArdanBorn |
| [Unseen](powers.md#unseen) | While you sneak, hostile creatures more than 6 blocks away don't notice you. | ArdanBorn |

## Minstrel

Song was the first power in the world. Sound a horn to lift your companions, and talk your way to better prices.

**Impact:** Medium · **Icon:** `goat_horn` · **File:** `bard.json`

| Power | What it does | From |
|---|---|---|
| [Songs of Power](powers.md#songs-of-power) | Sound a Goat Horn to play the song its tune carries: rest, valor, seeking or the walking song from common horns, and courage, gathering, hope or Lúthien's lullaby from a screaming goat's. Songs reach players within 24 blocks. 90 second cooldown shared by all songs. | ArdanBorn |
| [Silver Tongue](powers.md#silver-tongue) | Villagers and wandering traders warm to you. Near them you gain Hero of the Village for cheaper trades. | ArdanBorn |

## Defender

A shieldbearer sworn to protect. Hold a shield and you, and the friends beside you, are harder to harm.

**Impact:** Medium · **Icon:** `shield` · **File:** `champion.json`

| Power | What it does | From |
|---|---|---|
| [Shield Wall](powers.md#shield-wall) | While you hold a shield you take 15% less damage, and allies within 5 blocks take 10% less. | ArdanBorn |
| [Thick Skin](powers.md#thick-skin) | Your hide is tough enough that arrows do much less damage to you. | [Origins-Fantasy](https://modrinth.com/plugin/origins-fantasy) |

## Healer

The hands of the king are the hands of a healer. Cure the poisoned and wounded with a touch, and mend all who stand near you.

**Impact:** Low · **Icon:** `glistering_melon_slice` · **File:** `cleric.json`

| Power | What it does | From |
|---|---|---|
| [Hands of the Healer](powers.md#hands-of-the-healer) | Sneak and right-click an ally with an empty hand to grant Regeneration II and cure Poison and Wither, and hold back a Morgul wound (15s cooldown; a wound can be held back once every 10 minutes). Do the same in the air to channel healing light: you and players within 8 blocks heal 3 hearts, and the undead nearby are seared (60s). | ArdanBorn |
| [Healing Presence](powers.md#healing-presence) | Your presence mends those around you. Every 10 seconds, you and every player within 6 blocks heal 1 heart. | ArdanBorn |

## Loremaster

A student of the deep lore, bearing a staff of light. Smite the shadow and hold it at bay and recover your strength through stillness.

**Impact:** Medium · **Icon:** `blaze_rod` · **File:** `wizard.json`

| Power | What it does | From |
|---|---|---|
| [Light of the Staff](powers.md#light-of-the-staff) | Right-click with a Blaze Rod as your staff to drive back the shadow: hostile creatures within 6 blocks take 3 hearts of damage and are thrown back and revealed, and the undead take double and are set alight. 15 second cooldown. | ArdanBorn |
| [Healing Focus](powers.md#healing-focus) | When standing still you focus your energy to regenerate health. | [Origins-Magic](https://modrinth.com/plugin/origins-magic) |

## Beastfriend

Like Beren and Luthien, a friend to wild things. Beasts do not trouble you, the animals in your care are hardy, and Huan the hound of Valinor answers your call.

**Impact:** Low · **Icon:** `bone` · **File:** `druid.json`

| Power | What it does | From |
|---|---|---|
| [Friend of Beasts](powers.md#friend-of-beasts) | Wild beasts such as wolves, bees and bears won't pick you as a target unless you strike first. Your tamed animals take half damage and heal 1 heart every 10 seconds while near you. | ArdanBorn |
| [Call of Huan](powers.md#call-of-huan) | Right-click with a bone to call Huan, the great hound of Valinor, to your side for 60 seconds. 3 minute cooldown. | ArdanBorn |

## Herbalist

A gatherer of herbs and brewer of draughts. Your potions are stronger, and green things grow quickly in your care.

**Impact:** Low · **Icon:** `brewing_stand` · **File:** `alchemist.json`

| Power | What it does | From |
|---|---|---|
| [Potion Master](powers.md#potion-master) | Potions you drink and throw are much stronger. | [Origins-Magic](https://modrinth.com/plugin/origins-magic) |
| [Green Thumb](powers.md#green-thumb) | Living things flourish around you. Crops, berries and saplings within 5 blocks grow much faster. | ArdanBorn |

## Smith

A smith in the tradition of Aule and Feanor. Your own work mends itself, and everything you forge is made to last.

**Impact:** Low · **Icon:** `anvil` · **File:** `inventor.json`

| Power | What it does | From |
|---|---|---|
| [Mending Craft](powers.md#mending-craft) | Your own good work endures. Your held item and worn armor slowly repair themselves (1 durability every 10 seconds). | ArdanBorn |
| [Smith's Mark](powers.md#smiths-mark) | Only you can inscribe Cirth runes onto weapons and tools. Twice each full moon, a tool or weapon you forge under its open sky gains an enchantment beyond its usual maximum, by up to 5 levels. | ArdanBorn |
