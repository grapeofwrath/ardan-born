package dev.grapeofwrath.ardanborn.origins;

import com.starshootercity.abilities.types.Ability;
import com.starshootercity.abilities.types.VisibleAbility;
import dev.grapeofwrath.ardanborn.ArdanBorn;
import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.afflictions.MorgulWound;
import dev.grapeofwrath.ardanborn.armor.CustomItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * All custom Tolkien abilities for the ArdanBorn Origins-Reborn addon.
 * Every ability is a VisibleAbility, so it shows in the selection GUI.
 * Keys are ardanborn:<id>, which is what the origin JSON files reference.
 */
public final class OriginAbilities {
    private OriginAbilities() {}

    /** Abilities that need a once-a-second update implement this. */
    public interface Tickable {
        void tick(Player player);
    }

    public static List<Ability> all() {
        return List.of(
                // Ancestries
                new GiftOfMen(), new HopeUnquenched(), new UndyingBody(), new DeepLore(),
                new WoodlandStride(), new BaneOfMirkwood(), new BreathOfTheSea(), new SeasVigor(),new DelversInstinct(), new ForgeHardened(),
                new SecondBreakfast(), new FortunateFolk(), new BattleFury(), new IronStomach(),
                new Moonbound(), new ElvenWrought(), new Lembas(), new StarOfEarendil(), new SwiftMessenger(),
                new PearlsOfAlqualonde(), new TidesFury(), new AxeOfDurin(),
                new ToughAsOldTreeRoots(), new WrathOfTheNoldor(), new HealerOfImladris(), new LeafWalker(),
                // Classes
                new FirstStrike(), new HardenedVeteran(), new Cleave(), new HuntPrey(), new Wayfarer(),
                new ShieldWall(), new HandsOfTheHealer(), new LightOfTheStaff(), new FriendOfBeasts(),
                new MendingCraft(), new Backstab(), new QuietSteps(), new Unseen(), new SongsOfPower(), new SilverTongue(),
                new HealingPresence(), new CallOfHuan(), new GreenThumb(), new SmithsMark()
        );
    }

    // ------------------------------------------------------------------ helpers

    public abstract static class Base implements VisibleAbility {
        private final Key key;
        private final String title;
        private final String description;

        protected Base(String id, String title, String description) {
            this.key = Key.key(Keys.NAMESPACE, id);
            this.title = title;
            this.description = description;
        }

        @Override
        public Key getKey() {
            return key;
        }

        @Override
        public String title() {
            return title;
        }

        @Override
        public String description() {
            return description;
        }

        /// Use our text as written. Origins-Reborn would otherwise cache it in its translations.yml
        /// the first time it sees a power, so later edits here would never reach existing servers.
        @Override
        public boolean useTranslator() {
            return false;
        }
    }

    /** Simple per-player cooldown in milliseconds. */
    public static final class Cooldown {
        private final Map<UUID, Long> until = new HashMap<>();

        public boolean tryUse(Player p, long ms) {
            long now = System.currentTimeMillis();
            Long end = until.get(p.getUniqueId());
            if (end != null && end > now) {
                long left = (end - now + 999) / 1000;
                p.sendActionBar(Component.text("Ready in " + left + "s", NamedTextColor.GRAY));
                return false;
            }
            until.put(p.getUniqueId(), now + ms);
            return true;
        }

        /** Same as tryUse but silent (for passive triggers). */
        public boolean tryUseQuiet(Player p, long ms) {
            long now = System.currentTimeMillis();
            Long end = until.get(p.getUniqueId());
            if (end != null && end > now) return false;
            until.put(p.getUniqueId(), now + ms);
            return true;
        }

        /** Makes it ready again for this player. */
        public void reset(Player p) {
            until.remove(p.getUniqueId());
        }
    }

    static void effect(LivingEntity target, PotionEffectType type, int ticks, int amplifier) {
        target.addPotionEffect(new PotionEffect(type, ticks, amplifier, true, false, true));
    }

    static boolean holdsShield(Player p) {
        return p.getInventory().getItemInMainHand().getType() == Material.SHIELD
                || p.getInventory().getItemInOffHand().getType() == Material.SHIELD;
    }

    /** The undead, servants of the Shadow. */
    public static final Set<EntityType> UNDEAD = EnumSet.of(
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK, EntityType.DROWNED,
            EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.ZOMBIE_HORSE,
            EntityType.SKELETON, EntityType.STRAY, EntityType.BOGGED, EntityType.WITHER_SKELETON,
            EntityType.SKELETON_HORSE, EntityType.WITHER, EntityType.PHANTOM);

    /** The creatures of the Nether that aren't undead. */
    public static final Set<EntityType> NETHER = EnumSet.of(
            EntityType.BLAZE, EntityType.GHAST, EntityType.MAGMA_CUBE,
            EntityType.HOGLIN, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE);

    public static boolean isMelee(EntityDamageByEntityEvent e) {
        return e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK;
    }

    /** Forests, taigas, jungles and groves: the Sindar's home ground. */
    public static boolean inWoodland(Player p) {
        String biome = p.getLocation().getBlock().getBiome().getKey().getKey();
        return biome.contains("forest") || biome.contains("taiga") || biome.contains("jungle")
                || biome.contains("grove") || biome.contains("woods");
    }

    /** Deep, unlit places: below sea level and out of the sky's light. */
    public static boolean inTheDeep(Player p) {
        return p.getLocation().getBlockY() < 50 && p.getLocation().getBlock().getLightFromSky() < 4;
    }

    /** The Overworld's night, as the Beornings' Moonbound counts it. */
    public static boolean isNight(World world) {
        if (world.getEnvironment() != World.Environment.NORMAL) return false;
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }

    static boolean isRightClick(Action a) {
        return a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK;
    }

    // ================================================================ ANCESTRIES

    /** Edain: the Gift of Men. */
    public static final class GiftOfMen extends Base implements Listener {
        public GiftOfMen() {
            super("gift_of_men", "Gift of Men",
                    "Swift are Men to learn, having little time for it: from every deed and labor you draw 25% more experience.");
        }

        @EventHandler
        public void onXp(PlayerExpChangeEvent e) {
            runForAbility(e.getPlayer(), p -> {
                int amount = e.getAmount();
                if (amount > 0) e.setAmount((int) Math.ceil(amount * 1.25));
            });
        }
    }

    /** Edain / Peredhil: hope in the darkest hour. */
    public static final class HopeUnquenched extends Base implements Listener {
        private static final Cooldown cooldown = new Cooldown();

        public HopeUnquenched() {
            super("hope_unquenched", "Hope Unquenched",
                    "When a blow has left you below 30% of your health, a strength unlooked-for awakes, and you are granted Resistance and Regeneration for 8 seconds; but not again until 90 seconds are gone.");
        }

        /** Makes Hope Unquenched ready again (the Horn of Gondor, blown by one of the Edain). */
        public static void renew(Player player) {
            cooldown.reset(player);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onDamage(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Player victim)) return;
            runForAbility(victim, p -> {
                var attr = p.getAttribute(Attribute.MAX_HEALTH);
                double max = attr == null ? 20.0 : attr.getValue();
                double after = p.getHealth() - e.getFinalDamage();
                if (after <= 0 || after > max * 0.3) return;
                if (!cooldown.tryUseQuiet(p, 90_000)) return;
                effect(p, PotionEffectType.RESISTANCE, 160, 0);
                effect(p, PotionEffectType.REGENERATION, 160, 0);
                p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation().add(0, 1, 0), 20, 0.4, 0.6, 0.4, 0.02);
                p.sendActionBar(Component.text("This too shall pass!", NamedTextColor.GOLD));
            });
        }
    }

    /** Elves: no sickness. */
    public static final class UndyingBody extends Base implements Listener {
        public UndyingBody() {
            super("undying_body", "Undying Body",
                    "The Eldar do not sicken as Men do, and neither Poison nor Hunger has any power upon you.");
        }

        @EventHandler
        public void onEffect(EntityPotionEffectEvent e) {
            if (!(e.getEntity() instanceof Player player)) return;
            PotionEffect added = e.getNewEffect();
            if (added == null) return;
            PotionEffectType type = added.getType();
            if (type != PotionEffectType.POISON && type != PotionEffectType.HUNGER) return;
            runForAbility(player, p -> e.setCancelled(true));
        }
    }

    /** Noldor: lore and craft. */
    public static final class DeepLore extends Base implements Listener {
        public DeepLore() {
            super("deep_lore", "Deep Lore",
                    "Long did the Noldor study, and great is their lore: to enchant costs you one experience level fewer.");
        }

        @EventHandler
        public void onEnchant(EnchantItemEvent e) {
            runForAbility(e.getEnchanter(), p -> {
                int minimum = e.whichButton() + 1;
                e.setExpLevelCost(Math.max(minimum, e.getExpLevelCost() - 1));
            });
        }
    }

    /** Sindar: at home beneath the trees. */
    public static final class WoodlandStride extends Base implements Tickable {
        public WoodlandStride() {
            super("woodland_stride", "Woodland Stride",
                    "In forests, taigas, jungles and groves the wood itself hastens your feet, and you are given Speed.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                if (inWoodland(p)) effect(p, PotionEffectType.SPEED, 60, 0);
            });
        }
    }

    /** Sindar: hunters of the spiders of Mirkwood. */
    public static final class BaneOfMirkwood extends Base implements Listener {
        public BaneOfMirkwood() {
            super("bane_of_mirkwood", "Bane of Mirkwood",
                    "Seldom do the archers of the wood fail of their mark. In a forest, taiga, jungle or grove your arrows deal 20% more damage; and to spiders and cave spiders they deal 50% more.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onArrow(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof AbstractArrow arrow)) return;
            if (!(arrow.getShooter() instanceof Player shooter)) return;
            runForAbility(shooter, p -> {
                double multiplier = 1.0;
                // Cave spiders are spiders too.
                if (e.getEntity() instanceof Spider) multiplier *= 1.5;
                if (inWoodland(p)) multiplier *= 1.2;
                e.setDamage(e.getDamage() * multiplier);
            });
        }
    }

    /** Teleri: breath of the sea. */
    public static final class BreathOfTheSea extends Base implements Listener {
        public BreathOfTheSea() {
            super("breath_of_the_sea", "Breath of the Sea",
                    "The Teleri were born beside the shore, and water is to you as air: you may breathe beneath it without end.");
        }

        /// Air never runs down, so the bubbles never even start to drop.
        @EventHandler(ignoreCancelled = true)
        public void onAirChange(EntityAirChangeEvent e) {
            if (!(e.getEntity() instanceof Player player)) return;
            if (e.getAmount() >= player.getRemainingAir()) return;
            runForAbility(player, p -> e.setCancelled(true));
        }
    }

    /** Teleri: the sea strengthens its children. */
    public static final class SeasVigor extends Base implements Tickable {
        private static final NamespacedKey MODIFIER = Keys.key("seas_vigor");
        private static final double BONUS = 4.0; // 2 hearts

        public SeasVigor() {
            super("seas_vigor", "Sea's Vigor",
                    "The sea gives strength to its children. While you stand in water or rain you have 2 hearts more.");
        }

        /// Runs for every player, so the hearts also go away when someone stops being Teleri. The modifier
        /// is transient: it is never saved with the player.
        @Override
        public void tick(Player player) {
            AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
            if (health == null) return;
            boolean wanted = hasAbility(player) && player.isInWaterOrRain();
            boolean present = health.getModifier(MODIFIER) != null;
            if (wanted && !present) {
                health.addTransientModifier(
                        new AttributeModifier(MODIFIER, BONUS, AttributeModifier.Operation.ADD_NUMBER));
            } else if (!wanted && present) {
                health.removeModifier(MODIFIER);
            }
        }
    }

    /** Dwarves: at home in the deep places. */
    public static final class DelversInstinct extends Base implements Tickable {
        public DelversInstinct() {
            super("delvers_instinct", "Delver's Instinct",
                    "In deep and unlit tunnels, below the level of the sea and out of the sky's light, your hands are given Haste, and you see by the touch of stone.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                if (!inTheDeep(p)) return;
                effect(p, PotionEffectType.HASTE, 60, 0);
                effect(p, PotionEffectType.NIGHT_VISION, 320, 0);
            });
        }
    }

    /** Dwarves and smiths: hardened against fire. Shared by Naugrim and Smith. */
    public static final class ForgeHardened extends Base implements Listener {
        private static final Set<EntityDamageEvent.DamageCause> FIRE_CAUSES = EnumSet.of(
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.LAVA,
                EntityDamageEvent.DamageCause.HOT_FLOOR);

        public ForgeHardened() {
            super("forge_hardened", "Forge-hardened",
                    "Long years beside the forge have made you hard, and fire, lava and hot floors deal you 40% less damage.");
        }

        @EventHandler
        public void onDamage(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Player victim)) return;
            if (!FIRE_CAUSES.contains(e.getCause())) return;
            runForAbility(victim, p -> e.setDamage(e.getDamage() * 0.6));
        }
    }

    /** Hobbits: food is a joy. */
    public static final class SecondBreakfast extends Base implements Listener {
        public SecondBreakfast() {
            super("second_breakfast", "Second Breakfast",
                    "None know better than Hobbits how to honor a meal. Food restores to you 1 hunger point more, and a little more saturation.");
        }

        @EventHandler
        public void onEat(PlayerItemConsumeEvent e) {
            if (!e.getItem().getType().isEdible()) return;
            runForAbility(e.getPlayer(), p -> {
                p.setFoodLevel(Math.min(20, p.getFoodLevel() + 1));
                p.setSaturation(Math.min(p.getFoodLevel(), p.getSaturation() + 2f));
            });
        }
    }

    /** Hobbits: good fortune. */
    public static final class FortunateFolk extends Base implements Tickable {
        public FortunateFolk() {
            super("fortunate_folk", "Fortunate Folk",
                    "Fortune goes ever beside the Hobbits. You have Luck without end, and your fishing and your findings are the better for it.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> effect(p, PotionEffectType.LUCK, 100, 0));
        }
    }

    /** Uruk-hai: fury of battle. */
    public static final class BattleFury extends Base implements Listener {
        private static final long FURY_MS = 8_000;
        /// When each player's fury last began, so items (the Scimitar of Isengard) can tell it's active.
        private static final Map<UUID, Long> furyFrom = new HashMap<>();

        public BattleFury() {
            super("battle_fury", "Battle-fury",
                    "With each slaying a fury comes upon you, and you have Speed for 8 seconds and Strength for 5 seconds.");
        }

        /** Whether the player killed something with Battle-fury in the last 8 seconds. */
        public static boolean active(Player player) {
            Long from = furyFrom.get(player.getUniqueId());
            return from != null && System.currentTimeMillis() - from < FURY_MS;
        }

        @EventHandler
        public void onKill(EntityDeathEvent e) {
            Player killer = e.getEntity().getKiller();
            if (killer == null) return;
            runForAbility(killer, p -> {
                effect(p, PotionEffectType.SPEED, 160, 0);
                effect(p, PotionEffectType.STRENGTH, 100, 0);
                furyFrom.put(p.getUniqueId(), System.currentTimeMillis());
            });
        }
    }

    /** Uruk-hai: nothing spoils. */
    public static final class IronStomach extends Base implements Listener {
        public IronStomach() {
            super("iron_stomach", "Iron Stomach",
                    "No foul meat can harm you. Rotten flesh, spider eyes, poisonous potatoes and the like bring upon you no ill effects.");
        }

        @EventHandler
        public void onEffect(EntityPotionEffectEvent e) {
            if (!(e.getEntity() instanceof Player player)) return;
            if (e.getCause() != EntityPotionEffectEvent.Cause.FOOD) return;
            PotionEffect added = e.getNewEffect();
            if (added == null) return;
            PotionEffectType type = added.getType();
            if (type == PotionEffectType.HUNGER || type == PotionEffectType.POISON || type == PotionEffectType.NAUSEA) {
                runForAbility(player, p -> e.setCancelled(true));
            }
        }
    }

    /** Beorning: strongest under the moon. */
    public static final class Moonbound extends Base implements Tickable {
        public Moonbound() {
            super("moonbound", "Moonbound",
                    "By night the strength of the skin-changer awakes, and you have Strength I. When the moon is gibbous you have Speed also; and when it is full, Strength II, Speed and Resistance.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                World world = p.getWorld();
                if (!isNight(world)) return;
                // 0 = full moon, 1 and 7 = gibbous, 4 = new moon
                int phase = (int) ((world.getFullTime() / 24000L) % 8L);
                switch (phase) {
                    case 0 -> {
                        effect(p, PotionEffectType.STRENGTH, 60, 1);
                        effect(p, PotionEffectType.SPEED, 60, 0);
                        effect(p, PotionEffectType.RESISTANCE, 60, 0);
                    }
                    case 1, 7 -> {
                        effect(p, PotionEffectType.STRENGTH, 60, 0);
                        effect(p, PotionEffectType.SPEED, 60, 0);
                    }
                    default -> effect(p, PotionEffectType.STRENGTH, 60, 0);
                }
            });
        }
    }

    /** Noldor: the work of their hands endures. */
    public static final class ElvenWrought extends Base implements Listener {
        public ElvenWrought() {
            super("elven_wrought", "Elven-wrought",
                    "What the Elves have made endures. Your tools, weapons and armor have a 40% chance to take no wear when they are used.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onWear(PlayerItemDamageEvent e) {
            runForAbility(e.getPlayer(), p -> {
                if (ThreadLocalRandom.current().nextDouble() < 0.4) e.setCancelled(true);
            });
        }
    }

    /** Sindar: the waybread of the Elves. */
    public static final class Lembas extends Base implements Listener {
        private final Cooldown cooldown = new Cooldown();

        public Lembas() {
            super("lembas", "Lembas",
                    "In your hands bread becomes the waybread of the Elves: it fills you wholly, and for a little while it heals you. Once in a minute only has it this virtue.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onEat(PlayerItemConsumeEvent e) {
            if (e.getItem().getType() != Material.BREAD) return;
            runForAbility(e.getPlayer(), p -> {
                if (!cooldown.tryUseQuiet(p, 60_000)) return;
                // Vanilla adds the bread's food after this event, so top up on the next tick.
                p.getScheduler().run(JavaPlugin.getPlugin(ArdanBorn.class), _ -> {
                    p.setFoodLevel(20);
                    p.setSaturation(20f);
                }, null);
                effect(p, PotionEffectType.REGENERATION, 100, 0);
                p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation().add(0, 1, 0), 8, 0.3, 0.4, 0.3, 0);
            });
        }
    }

    /** Peredhil: Eärendil, their father, sails the sky as the evening star. */
    public static final class StarOfEarendil extends Base implements Tickable {
        public StarOfEarendil() {
            super("star_of_earendil", "Star of Eärendil",
                    "Your father sails the night sky, and his star is the hope of many. Beneath it you are slowly healed, and the dark is clear to you.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                World world = p.getWorld();
                if (world.getEnvironment() != World.Environment.NORMAL) return;
                long time = world.getTime();
                if (time < 13000 || time > 23000) return; // night only
                if (p.getEyeLocation().getBlock().getLightFromSky() < 15) return; // open sky only
                effect(p, PotionEffectType.REGENERATION, 60, 0);
                effect(p, PotionEffectType.NIGHT_VISION, 320, 0);
            });
        }
    }

    /** Peredhil: Glorfindel's ride to the Ford of Bruinen. */
    public static final class SwiftMessenger extends Base implements Listener, Tickable {
        public SwiftMessenger() {
            super("swift_messenger", "Swift Messenger",
                    "So rode Glorfindel to the Ford: the horses, donkeys and mules that bear you run the faster, and falls do them no hurt.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                if (p.getVehicle() instanceof AbstractHorse mount) effect(mount, PotionEffectType.SPEED, 60, 0);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onFall(EntityDamageEvent e) {
            if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
            if (!(e.getEntity() instanceof AbstractHorse mount)) return;
            for (Entity rider : mount.getPassengers()) {
                if (rider instanceof Player player && hasAbility(player)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    /** Teleri: the sea gives up its treasures. */
    public static final class PearlsOfAlqualonde extends Base implements Listener, Tickable {
        public PearlsOfAlqualonde() {
            super("pearls_of_alqualonde", "Pearls of Alqualondë",
                    "To the Teleri the sea yields up its treasures. While you hold a fishing rod you have Luck, and with each catch there is a 25% chance that a second comes with it.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onFish(PlayerFishEvent e) {
            if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
            if (!(e.getCaught() instanceof Item caught)) return;
            runForAbility(e.getPlayer(), p -> {
                if (ThreadLocalRandom.current().nextDouble() >= 0.25) return;
                Item extra = caught.getWorld().dropItem(caught.getLocation(), caught.getItemStack().clone());
                extra.setVelocity(caught.getVelocity());
            });
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                if (p.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD
                        || p.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD) {
                    effect(p, PotionEffectType.LUCK, 60, 0);
                }
            });
        }
    }

    /** Teleri: the wrath of the sea, as at the Kinslaying of Alqualondë. */
    public static final class TidesFury extends Base implements Listener {
        public TidesFury() {
            super("tides_fury", "Tide's Fury",
                    "The wrath of the sea is in your arm. While you stand in water or rain, your melee and trident hits deal 25% more damage.");
        }

        @EventHandler
        public void onHit(EntityDamageByEntityEvent e) {
            Player attacker = null;
            if (e.getDamager() instanceof Player p && isMelee(e)) attacker = p;
            else if (e.getDamager() instanceof Trident t && t.getShooter() instanceof Player p) attacker = p;
            if (attacker == null || !attacker.isInWaterOrRain()) return;
            runForAbility(attacker, p -> e.setDamage(e.getDamage() * 1.25));
        }
    }

    /** Naugrim: the axe is the dwarves' weapon. */
    public static final class AxeOfDurin extends Base implements Listener {
        public AxeOfDurin() {
            super("axe_of_durin", "Axe of Durin",
                    "The axe was ever dear to Durin's folk. In your hand it strikes 20% the harder, and those it bites go slowly for a while.");
        }

        @EventHandler
        public void onHit(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player damager) || !isMelee(e)) return;
            if (!damager.getInventory().getItemInMainHand().getType().name().endsWith("_AXE")) return;
            runForAbility(damager, p -> {
                e.setDamage(e.getDamage() * 1.2);
                if (e.getEntity() instanceof LivingEntity victim) effect(victim, PotionEffectType.SLOWNESS, 40, 0);
            });
        }
    }

    /** Hobbits: there is more to them than meets the eye. */
    public static final class ToughAsOldTreeRoots extends Base implements Listener {
        public ToughAsOldTreeRoots() {
            super("tough_as_old_tree_roots", "Tough as Old Tree-roots",
                    "As Frodo bore the Ring, so do you endure. The Wither deals you 30% less damage, Slowness holds you but half as long, and Darkness cannot touch you.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onWither(EntityDamageEvent e) {
            if (e.getCause() != EntityDamageEvent.DamageCause.WITHER) return;
            if (!(e.getEntity() instanceof Player player)) return;
            runForAbility(player, p -> e.setDamage(e.getDamage() * 0.7));
        }

        @EventHandler(ignoreCancelled = true)
        public void onEffect(EntityPotionEffectEvent e) {
            if (!(e.getEntity() instanceof Player player)) return;
            PotionEffect added = e.getNewEffect();
            if (added == null) return;
            if (added.getType() == PotionEffectType.DARKNESS) {
                runForAbility(player, p -> e.setCancelled(true));
                return;
            }
            // Our own re-added, halved effect comes back with cause PLUGIN, so it isn't halved again.
            if (added.getType() != PotionEffectType.SLOWNESS || added.isInfinite()
                    || e.getCause() == EntityPotionEffectEvent.Cause.PLUGIN) return;
            runForAbility(player, p -> {
                e.setCancelled(true);
                PotionEffect halved = added.withDuration(Math.max(1, added.getDuration() / 2));
                p.getScheduler().run(JavaPlugin.getPlugin(ArdanBorn.class), _ -> p.addPotionEffect(halved), null);
            });
        }
    }

    /** Noldor: the long war against Morgoth. */
    public static final class WrathOfTheNoldor extends Base implements Listener {
        public WrathOfTheNoldor() {
            super("wrath_of_the_noldor", "Wrath of the Noldor",
                    "Since the First Age the Noldor have made war upon Morgoth. To the undead and the creatures of the Nether your melee hits deal 25% more damage.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onHit(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player damager) || !isMelee(e)) return;
            EntityType type = e.getEntity().getType();
            if (!UNDEAD.contains(type) && !NETHER.contains(type)) return;
            runForAbility(damager, p -> e.setDamage(e.getDamage() * 1.25));
        }
    }

    /** Peredhil: Elrond, the healer of Imladris. */
    public static final class HealerOfImladris extends Base implements Listener {
        public HealerOfImladris() {
            super("healer_of_imladris", "Healer of Imladris",
                    "The gift of Elrond is yours also. By healing potions, Regeneration and golden apples you are healed 50% more.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onHeal(EntityRegainHealthEvent e) {
            if (!(e.getEntity() instanceof Player player)) return;
            EntityRegainHealthEvent.RegainReason reason = e.getRegainReason();
            if (reason != EntityRegainHealthEvent.RegainReason.MAGIC
                    && reason != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) return;
            runForAbility(player, p -> e.setAmount(e.getAmount() * 1.5));
        }
    }

    /** Sindar: the Silvan Elves of the treetops. */
    public static final class LeafWalker extends Base implements Listener, Tickable {
        public LeafWalker() {
            super("leaf_walker", "Leaf-walker",
                    "Upon the treetops the Silvan Elves run as upon a road. To land upon leaves does you no hurt, however far the fall, and while you stand on them you have Speed and Jump Boost.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onFall(EntityDamageEvent e) {
            if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
            if (!(e.getEntity() instanceof Player player) || !onLeaves(player)) return;
            runForAbility(player, p -> e.setCancelled(true));
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                if (!onLeaves(p)) return;
                effect(p, PotionEffectType.SPEED, 40, 1);
                effect(p, PotionEffectType.JUMP_BOOST, 40, 1);
            });
        }

        private static boolean onLeaves(Player p) {
            return Tag.LEAVES.isTagged(p.getLocation().subtract(0, 0.1, 0).getBlock().getType());
        }
    }

    // ================================================================== CLASSES

    /** Warrior. */
    public static final class FirstStrike extends Base implements Listener {
        private final Map<UUID, Long> lastAttack = new HashMap<>();

        public FirstStrike() {
            super("first_strike", "First Strike",
                    "When for 3 seconds you have struck no blow, the next deals 50% more damage.");
        }

        @EventHandler
        public void onHit(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player damager) || !isMelee(e)) return;
            runForAbility(damager, p -> {
                long now = System.currentTimeMillis();
                Long last = lastAttack.put(p.getUniqueId(), now);
                if (last == null || now - last >= 3000) {
                    e.setDamage(e.getDamage() * 1.5);
                }
            });
        }
    }

    /** Warrior: years of battle have toughened you. */
    public static final class HardenedVeteran extends Base implements Tickable {
        private static final NamespacedKey HEALTH = Keys.key("hardened_veteran_health");
        private static final NamespacedKey KNOCKBACK = Keys.key("hardened_veteran_knockback");
        private static final double BONUS_HEALTH = 4.0; // 2 hearts
        private static final double BONUS_KNOCKBACK = 0.25;

        public HardenedVeteran() {
            super("hardened_veteran", "Hardened Veteran",
                    "Many wars have you seen, and they have made you hard. You have 2 hearts more, and are not lightly knocked back.");
        }

        /// Runs for every player, so the bonus goes away when someone stops being a Warrior. The modifiers
        /// are transient: they are never saved with the player.
        @Override
        public void tick(Player player) {
            boolean wanted = hasAbility(player);
            apply(player.getAttribute(Attribute.MAX_HEALTH), HEALTH, BONUS_HEALTH, wanted);
            apply(player.getAttribute(Attribute.KNOCKBACK_RESISTANCE), KNOCKBACK, BONUS_KNOCKBACK, wanted);
        }

        private static void apply(@Nullable AttributeInstance attr, NamespacedKey key, double amount, boolean wanted) {
            if (attr == null) return;
            boolean present = attr.getModifier(key) != null;
            if (wanted && !present) {
                attr.addTransientModifier(new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER));
            } else if (!wanted && present) {
                attr.removeModifier(key);
            }
        }
    }

    /** Warrior. */
    public static final class Cleave extends Base implements Listener {
        private static boolean cleaving = false;

        public Cleave() {
            super("cleave", "Cleave",
                    "The stroke of your sword or axe does not end with one foe: a hostile mob beside your target takes half the damage.");
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onHit(EntityDamageByEntityEvent e) {
            if (cleaving) return;
            if (!(e.getDamager() instanceof Player damager) || !isMelee(e)) return;
            if (!(e.getEntity() instanceof LivingEntity victim)) return;
            runForAbility(damager, p -> {
                String weapon = p.getInventory().getItemInMainHand().getType().name();
                if (!weapon.endsWith("_SWORD") && !weapon.endsWith("_AXE")) return;
                double splash = e.getFinalDamage() * 0.5;
                for (Entity near : victim.getNearbyEntities(2.5, 1.5, 2.5)) {
                    if (near instanceof Monster other && other != victim) {
                        cleaving = true;
                        try {
                            other.damage(splash, p);
                        } finally {
                            cleaving = false;
                        }
                        break;
                    }
                }
            });
        }
    }

    /** Ranger. */
    public static final class HuntPrey extends Base implements Listener {
        private static final long PREY_MS = 60_000;
        private static final int PREY_TICKS = 1200;
        private static final double PREY_BONUS = 1.2;

        private record Prey(UUID target, long until) {}

        private final Map<UUID, Prey> prey = new HashMap<>();

        public HuntPrey() {
            super("hunt_prey", "Hunt Prey",
                    "The creature that your arrow last struck becomes your prey for 60 seconds. It glows, and whatever hurt you deal it is 20% greater.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onHit(EntityDamageByEntityEvent e) {
            if (!(e.getEntity() instanceof LivingEntity victim) || victim instanceof Player) return;
            Player hunter = e.getDamager() instanceof Player p ? p
                    : e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p ? p : null;
            if (hunter == null) return;
            runForAbility(hunter, p -> {
                Prey current = prey.get(p.getUniqueId());
                long now = System.currentTimeMillis();
                if (current != null && current.target().equals(victim.getUniqueId()) && now < current.until()) {
                    e.setDamage(e.getDamage() * PREY_BONUS);
                }
                if (!(e.getDamager() instanceof Arrow)) return;
                // An arrow marks (or re-marks) the prey.
                if (current != null && !current.target().equals(victim.getUniqueId())
                        && Bukkit.getEntity(current.target()) instanceof LivingEntity old) {
                    old.removePotionEffect(PotionEffectType.GLOWING);
                }
                prey.put(p.getUniqueId(), new Prey(victim.getUniqueId(), now + PREY_MS));
                effect(victim, PotionEffectType.GLOWING, PREY_TICKS, 0);
            });
        }
    }

    /** Ranger. */
    public static final class Wayfarer extends Base implements Listener {
        public Wayfarer() {
            super("wayfarer", "Wayfarer",
                    "The long road is home to you. A quarter of the time, when hunger should grow in you, it does not.");
        }

        @EventHandler
        public void onFood(FoodLevelChangeEvent e) {
            if (!(e.getEntity() instanceof Player player)) return;
            if (e.getFoodLevel() >= player.getFoodLevel()) return;
            runForAbility(player, p -> {
                if (ThreadLocalRandom.current().nextDouble() < 0.25) e.setCancelled(true);
            });
        }
    }

    /** Defender. */
    public static final class ShieldWall extends Base implements Listener {
        private static final Set<EntityDamageEvent.DamageCause> IGNORED = EnumSet.of(
                EntityDamageEvent.DamageCause.VOID,
                EntityDamageEvent.DamageCause.SUICIDE,
                EntityDamageEvent.DamageCause.KILL,
                EntityDamageEvent.DamageCause.CUSTOM);

        public ShieldWall() {
            super("shield_wall", "Shield Wall",
                    "While you hold a shield you take 15% less damage, and allies within 5 blocks of you take 10% less.");
        }

        @EventHandler
        public void onDamage(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Player victim)) return;
            if (IGNORED.contains(e.getCause())) return;
            if (hasAbility(victim) && holdsShield(victim)) {
                e.setDamage(e.getDamage() * 0.85);
                return;
            }
            for (Entity near : victim.getNearbyEntities(5, 3, 5)) {
                if (near instanceof Player defender && hasAbility(defender) && holdsShield(defender)) {
                    e.setDamage(e.getDamage() * 0.9);
                    return;
                }
            }
        }
    }

    /** Healer: "the hands of the king are the hands of a healer". */
    public static final class HandsOfTheHealer extends Base implements Listener {
        private final Cooldown others = new Cooldown();
        private final Cooldown channel = new Cooldown();
        private static final double CHANNEL_RANGE = 8;
        private static final double CHANNEL_AMOUNT = 6.0; // 3 hearts

        public HandsOfTheHealer() {
            super("hands_of_the_healer", "Hands of the Healer",
                    "Sneak and right-click an ally with an empty hand, and you grant Regeneration II, cure Poison and Wither, and hold back a Morgul wound (15 seconds must pass ere you do so again, and 10 minutes for the same wound). Do likewise to the air, and you and players within 8 blocks are healed 3 hearts, and the undead nearby are seared (60 seconds).");
        }

        private void heal(Player healer, Player target) {
            target.removePotionEffect(PotionEffectType.POISON);
            target.removePotionEffect(PotionEffectType.WITHER);
            if (MorgulWound.isWounded(target) && !MorgulWound.holdBack(target)) {
                healer.sendActionBar(Component.text("The wound will not yet heed your hands.", NamedTextColor.GRAY));
            }
            effect(target, PotionEffectType.REGENERATION, 160, 1);
            target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 1.8, 0), 6, 0.3, 0.3, 0.3, 0);
        }

        @EventHandler
        public void onTouch(PlayerInteractEntityEvent e) {
            if (e.getHand() != EquipmentSlot.HAND) return;
            if (!(e.getRightClicked() instanceof Player target)) return;
            runForAbility(e.getPlayer(), p -> {
                if (!p.isSneaking() || !p.getInventory().getItemInMainHand().getType().isAir()) return;
                if (!others.tryUse(p, 15_000)) return;
                heal(p, target);
            });
        }

        @EventHandler
        public void onSelf(PlayerInteractEvent e) {
            if (e.getHand() != EquipmentSlot.HAND) return;
            if (e.getAction() != Action.RIGHT_CLICK_AIR) return;
            runForAbility(e.getPlayer(), p -> {
                if (!p.isSneaking() || !p.getInventory().getItemInMainHand().getType().isAir()) return;
                if (!channel.tryUse(p, 60_000)) return;
                channel(p);
            });
        }

        /// Channel: an instant burst of healing for you and the players around you, and searing light for
        /// the undead. Heals through the regain event, so Healer of Imladris boosts it.
        private static void channel(Player p) {
            World world = p.getWorld();
            world.spawnParticle(Particle.END_ROD, p.getLocation().add(0, 1, 0), 50, 3, 0.6, 3, 0.02);
            world.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, 1.2f);
            mend(p);
            DamageSource sear = DamageSource.builder(DamageType.INDIRECT_MAGIC)
                    .withCausingEntity(p).withDirectEntity(p).build();
            for (Entity near : p.getNearbyEntities(CHANNEL_RANGE, 4, CHANNEL_RANGE)) {
                if (near instanceof Player ally) {
                    mend(ally);
                } else if (near instanceof LivingEntity living && UNDEAD.contains(living.getType())) {
                    living.damage(6.0, sear);
                }
            }
        }

        private static void mend(Player target) {
            if (target.isDead()) return;
            target.heal(CHANNEL_AMOUNT, EntityRegainHealthEvent.RegainReason.MAGIC);
            target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 1.8, 0), 3, 0.3, 0.2, 0.3, 0);
        }
    }

    /** Loremaster: Gandalf's light. */
    public static final class LightOfTheStaff extends Base implements Listener {
        private final Cooldown cooldown = new Cooldown();

        public LightOfTheStaff() {
            super("light_of_the_staff", "Light of the Staff",
                    "A Blaze Rod is your staff: right-click, and the shadow is driven back. Hostile creatures within 6 blocks take 3 hearts of damage and are thrown back and revealed; the undead take twice as much and are set alight. It may be done once in 15 seconds.");
        }

        @EventHandler
        public void onCast(PlayerInteractEvent e) {
            if (e.getHand() != EquipmentSlot.HAND || !isRightClick(e.getAction())) return;
            if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;
            runForAbility(e.getPlayer(), p -> {
                if (!cooldown.tryUse(p, 15_000)) return;
                World world = p.getWorld();
                world.spawnParticle(Particle.END_ROD, p.getLocation().add(0, 1, 0), 60, 2.5, 0.8, 2.5, 0.05);
                world.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.4f);
                for (Entity near : p.getNearbyEntities(6, 3, 6)) {
                    if (!(near instanceof Monster monster)) continue;
                    boolean undead = UNDEAD.contains(monster.getType());
                    // Magic, not a melee hit, so melee powers (Cleave, Wrath of the Noldor...) don't apply.
                    monster.damage(undead ? 12.0 : 6.0, DamageSource.builder(DamageType.INDIRECT_MAGIC)
                            .withCausingEntity(p).withDirectEntity(p).build());
                    Vector away = monster.getLocation().toVector().subtract(p.getLocation().toVector()).setY(0);
                    if (away.lengthSquared() > 0) {
                        monster.setVelocity(away.normalize().multiply(1.2).setY(0.35));
                    }
                    effect(monster, PotionEffectType.GLOWING, 100, 0);
                    if (undead) monster.setFireTicks(80);
                }
            });
        }
    }

    /** Beastfriend: Beren, Huan and the Eagles. */
    public static final class FriendOfBeasts extends Base implements Listener, Tickable {
        private static final Set<EntityType> BEASTS = EnumSet.of(
                EntityType.WOLF, EntityType.BEE, EntityType.POLAR_BEAR, EntityType.LLAMA,
                EntityType.TRADER_LLAMA, EntityType.PANDA, EntityType.GOAT);
        private static final long PULSE_MS = 10_000;
        private static final double HEAL = 2.0; // 1 heart
        private static final double PET_RANGE = 16.0;
        private final Map<UUID, Long> lastPulse = new HashMap<>();

        public FriendOfBeasts() {
            super("friend_of_beasts", "Friend of Beasts",
                    "Wolves, bees, bears and their like do not set upon you unless you strike first. The animals you have tamed take half damage, and while they are near you they heal 1 heart every 10 seconds.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onPetDamage(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Tameable pet) || !pet.isTamed()) return;
            if (ShieldWall.IGNORED.contains(e.getCause())) return;
            if (!(pet.getOwner() instanceof Player owner)) return;
            runForAbility(owner, p -> e.setDamage(e.getDamage() * 0.5));
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                long now = System.currentTimeMillis();
                Long last = lastPulse.get(p.getUniqueId());
                if (last != null && now - last < PULSE_MS) return;
                lastPulse.put(p.getUniqueId(), now);
                for (Entity near : p.getNearbyEntities(PET_RANGE, 8, PET_RANGE)) {
                    if (!(near instanceof Tameable pet) || !pet.isTamed() || pet.isDead()) continue;
                    if (!p.getUniqueId().equals(pet.getOwnerUniqueId())) continue;
                    var attr = pet.getAttribute(Attribute.MAX_HEALTH);
                    double max = attr == null ? pet.getHealth() : attr.getValue();
                    if (pet.getHealth() >= max) continue;
                    pet.heal(HEAL, EntityRegainHealthEvent.RegainReason.MAGIC);
                    pet.getWorld().spawnParticle(Particle.HEART, pet.getLocation().add(0, pet.getHeight() + 0.3, 0),
                            2, 0.3, 0.2, 0.3, 0);
                }
            });
        }

        @EventHandler
        public void onTarget(EntityTargetLivingEntityEvent e) {
            if (!BEASTS.contains(e.getEntityType())) return;
            if (!(e.getTarget() instanceof Player player)) return;
            EntityTargetEvent.TargetReason reason = e.getReason();
            if (reason == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY
                    || reason == EntityTargetEvent.TargetReason.CUSTOM) return;
            runForAbility(player, p -> e.setCancelled(true));
        }
    }

    /** Smith: self-mending craft. */
    public static final class MendingCraft extends Base implements Tickable {
        private static final EquipmentSlot[] SLOTS = {
                EquipmentSlot.HAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        private final Map<UUID, Long> lastRepair = new HashMap<>();

        public MendingCraft() {
            super("mending_craft", "Mending Craft",
                    "The good work of your hands endures. The item you hold and the armor you wear mend themselves, by 1 durability every 10 seconds.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                long now = System.currentTimeMillis();
                Long last = lastRepair.get(p.getUniqueId());
                if (last != null && now - last < 10_000) return;
                lastRepair.put(p.getUniqueId(), now);
                for (EquipmentSlot slot : SLOTS) {
                    ItemStack item = p.getInventory().getItem(slot);
                    if (item == null || item.getType().isAir()) continue;
                    if (item.getItemMeta() instanceof Damageable meta && meta.hasDamage()) {
                        meta.setDamage(meta.getDamage() - 1);
                        item.setItemMeta(meta);
                        p.getInventory().setItem(slot, item);
                    }
                }
            });
        }
    }

    /** Burglar. */
    public static final class Backstab extends Base implements Listener {
        public Backstab() {
            super("backstab", "Backstab",
                    "From behind, your melee hits deal 35% more damage; and if your foe has not perceived you, 50% more.");
        }

        @EventHandler
        public void onHit(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player damager) || !isMelee(e)) return;
            if (!(e.getEntity() instanceof LivingEntity victim)) return;
            runForAbility(damager, p -> {
                Vector a = p.getLocation().getDirection().setY(0);
                Vector v = victim.getLocation().getDirection().setY(0);
                if (a.lengthSquared() == 0 || v.lengthSquared() == 0) return;
                if (a.normalize().dot(v.normalize()) > 0.5) {
                    boolean unaware = victim instanceof Mob mob && mob.getTarget() != p;
                    e.setDamage(e.getDamage() * (unaware ? 1.5 : 1.35));
                    victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 12, 0.3, 0.4, 0.3, 0.1);
                }
            });
        }
    }

    /** Burglar. */
    public static final class QuietSteps extends Base implements Listener, Tickable {
        public QuietSteps() {
            super("quiet_steps", "Quiet Steps",
                    "Even when you sneak there is some haste in your feet, and you go a little faster.");
        }

        @EventHandler
        public void onSneak(PlayerToggleSneakEvent e) {
            if (!e.isSneaking()) return;
            runForAbility(e.getPlayer(), p -> effect(p, PotionEffectType.SPEED, 40, 0));
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                if (p.isSneaking()) effect(p, PotionEffectType.SPEED, 40, 0);
            });
        }
    }

    /** Burglar: Bilbo slipping past the trolls, the spiders and Smaug. */
    public static final class Unseen extends Base implements Listener {
        private static final double RANGE = 6.0;

        public Unseen() {
            super("unseen", "Unseen",
                    "While you sneak, no hostile creature further off than 6 blocks shall mark your passing.");
        }

        @EventHandler(ignoreCancelled = true)
        public void onTarget(EntityTargetLivingEntityEvent e) {
            if (!(e.getEntity() instanceof Monster monster)) return;
            if (!(e.getTarget() instanceof Player player) || !player.isSneaking()) return;
            EntityTargetEvent.TargetReason reason = e.getReason();
            if (reason == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY
                    || reason == EntityTargetEvent.TargetReason.CUSTOM) return;
            if (!monster.getWorld().equals(player.getWorld())) return;
            if (monster.getLocation().distanceSquared(player.getLocation()) <= RANGE * RANGE) return;
            runForAbility(player, p -> e.setCancelled(true));
        }
    }

    /** Minstrel: the Music of the Ainur. */
    public static final class SongsOfPower extends Base implements Listener {
        private static final long COOLDOWN_MS = 90_000;
        private static final double ALLY_RANGE = 24;
        /** Harmful effects the Song of Hope lifts. */
        private static final List<PotionEffectType> HOPE_CLEARS = List.of(
                PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.WEAKNESS,
                PotionEffectType.SLOWNESS, PotionEffectType.MINING_FATIGUE, PotionEffectType.HUNGER,
                PotionEffectType.BLINDNESS, PotionEffectType.DARKNESS);
        private final Cooldown cooldown = new Cooldown();

        public SongsOfPower() {
            super("songs_of_power", "Songs of Power",
                    "Sound a Goat Horn, and the song that lies in its tune is heard by players within 24 blocks: from common horns rest, valor, seeking or the walking song; and from those of a screaming goat courage, gathering, hope or Lúthien's lullaby. After any song 90 seconds must pass before another.");
        }

        @EventHandler
        public void onHorn(PlayerInteractEvent e) {
            if (!isRightClick(e.getAction())) return;
            ItemStack horn = e.getItem();
            if (horn == null || horn.getType() != Material.GOAT_HORN) return;
            // Custom horns (the Horn of Gondor) have their own power.
            if (CustomItems.isCustom(horn)) return;
            MusicInstrument instrument = horn.getData(DataComponentTypes.INSTRUMENT);
            if (instrument == null) return;
            String tune = tuneOf(instrument);
            // A tune from a datapack or a future version: leave it to vanilla.
            if (tune == null) return;
            runForAbility(e.getPlayer(), p -> {
                if (!cooldown.tryUse(p, COOLDOWN_MS)) return;
                String song = play(p, tune);
                if (song != null) p.sendActionBar(Component.text("♪ " + song, NamedTextColor.GOLD));
            });
        }

        /// The vanilla tune's name, e.g. "ponder_goat_horn", or null for any other instrument. Compares with the
        /// constants because `MusicInstrument`'s key methods are deprecated for removal.
        private static @Nullable String tuneOf(MusicInstrument instrument) {
            if (instrument.equals(MusicInstrument.PONDER_GOAT_HORN)) return "ponder_goat_horn";
            if (instrument.equals(MusicInstrument.SING_GOAT_HORN)) return "sing_goat_horn";
            if (instrument.equals(MusicInstrument.SEEK_GOAT_HORN)) return "seek_goat_horn";
            if (instrument.equals(MusicInstrument.FEEL_GOAT_HORN)) return "feel_goat_horn";
            if (instrument.equals(MusicInstrument.ADMIRE_GOAT_HORN)) return "admire_goat_horn";
            if (instrument.equals(MusicInstrument.CALL_GOAT_HORN)) return "call_goat_horn";
            if (instrument.equals(MusicInstrument.YEARN_GOAT_HORN)) return "yearn_goat_horn";
            if (instrument.equals(MusicInstrument.DREAM_GOAT_HORN)) return "dream_goat_horn";
            return null;
        }

        /** Plays the song for a horn's tune and returns its name. */
        private static @Nullable String play(Player p, String tune) {
            List<Player> allies = alliesOf(p);
            switch (tune) {
                case "ponder_goat_horn" -> {
                    allies.forEach(a -> effect(a, PotionEffectType.REGENERATION, 240, 0));
                    return "Song of Rest";
                }
                case "sing_goat_horn" -> {
                    allies.forEach(a -> {
                        effect(a, PotionEffectType.SPEED, 400, 0);
                        effect(a, PotionEffectType.REGENERATION, 160, 0);
                    });
                    return "Song of Valor";
                }
                case "seek_goat_horn" -> {
                    for (Entity near : p.getNearbyEntities(32, 16, 32)) {
                        if (near instanceof Monster m) effect(m, PotionEffectType.GLOWING, 200, 0);
                    }
                    return "Song of Seeking";
                }
                case "feel_goat_horn" -> {
                    allies.forEach(a -> {
                        a.setFoodLevel(Math.min(20, a.getFoodLevel() + 3));
                        effect(a, PotionEffectType.SPEED, 600, 0);
                    });
                    return "The Walking Song";
                }
                case "admire_goat_horn" -> {
                    allies.forEach(a -> {
                        effect(a, PotionEffectType.STRENGTH, 300, 0);
                        effect(a, PotionEffectType.RESISTANCE, 160, 0);
                    });
                    return "Song of Courage";
                }
                case "call_goat_horn" -> {
                    allies.forEach(a -> {
                        effect(a, PotionEffectType.SPEED, 300, 1);
                        effect(a, PotionEffectType.JUMP_BOOST, 300, 0);
                    });
                    return "Gathering Call";
                }
                case "yearn_goat_horn" -> {
                    allies.forEach(a -> {
                        HOPE_CLEARS.forEach(a::removePotionEffect);
                        effect(a, PotionEffectType.REGENERATION, 100, 0);
                    });
                    return "Song of Hope";
                }
                case "dream_goat_horn" -> {
                    for (Entity near : p.getNearbyEntities(10, 5, 10)) {
                        if (!(near instanceof Monster m)) continue;
                        effect(m, PotionEffectType.SLOWNESS, 100, 3);
                        m.setTarget(null);
                    }
                    return "Lullaby of Lúthien";
                }
                default -> {
                    return null;
                }
            }
        }

        /** You and every player within range. */
        private static List<Player> alliesOf(Player p) {
            List<Player> allies = new ArrayList<>();
            allies.add(p);
            for (Entity near : p.getNearbyEntities(ALLY_RANGE, ALLY_RANGE, ALLY_RANGE)) {
                if (near instanceof Player ally) allies.add(ally);
            }
            return allies;
        }
    }

    /** Minstrel. */
    public static final class SilverTongue extends Base implements Tickable {
        public SilverTongue() {
            super("silver_tongue", "Silver Tongue",
                    "Villagers and wandering traders warm to your speech. Near them you are as the Hero of the Village, and their trades are cheaper.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                for (Entity near : p.getNearbyEntities(8, 4, 8)) {
                    if (near instanceof Villager || near instanceof WanderingTrader) {
                        effect(p, PotionEffectType.HERO_OF_THE_VILLAGE, 60, 0);
                        return;
                    }
                }
            });
        }
    }

    /** Healer: a presence that mends. */
    public static final class HealingPresence extends Base implements Tickable {
        private static final long PULSE_MS = 10_000;
        private static final double AMOUNT = 2.0; // 1 heart
        private final Map<UUID, Long> lastPulse = new HashMap<>();

        public HealingPresence() {
            super("healing_presence", "Healing Presence",
                    "Those who are near you are made whole. Every 10 seconds, you and all players within 6 blocks heal 1 heart.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                long now = System.currentTimeMillis();
                Long last = lastPulse.get(p.getUniqueId());
                if (last != null && now - last < PULSE_MS) return;
                lastPulse.put(p.getUniqueId(), now);
                mend(p);
                for (Entity near : p.getNearbyEntities(6, 3, 6)) {
                    if (near instanceof Player ally) mend(ally);
                }
            });
        }

        /// Heals through the regain event, so other powers (e.g. Healer of Imladris) apply.
        private static void mend(Player target) {
            if (target.isDead()) return;
            var attr = target.getAttribute(Attribute.MAX_HEALTH);
            double max = attr == null ? 20.0 : attr.getValue();
            if (target.getHealth() >= max) return;
            target.heal(AMOUNT, EntityRegainHealthEvent.RegainReason.MAGIC);
            target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0, 1.8, 0), 2, 0.3, 0.2, 0.3, 0);
        }
    }

    /** Beastfriend: Huan, the hound of Valinor, who fought beside Beren and Lúthien. */
    public static final class CallOfHuan extends Base implements Listener {
        private static final NamespacedKey HUAN = Keys.key("huan");
        private static final long STAY_TICKS = 1200; // 60 seconds
        private static final long COOLDOWN_MS = 180_000;
        /// Each owner's Huan, so he can be sent away when they leave, die or call him again.
        private static final Map<UUID, Wolf> SUMMONED = new HashMap<>();
        private final Cooldown cooldown = new Cooldown();

        public CallOfHuan() {
            super("call_of_huan", "Call of Huan",
                    "Hold a bone and right-click, and Huan the hound of Valinor shall hear you. For 60 seconds he fights at your side, and after his summoning 3 minutes must pass ere he will come again.");
        }

        @EventHandler
        public void onCall(PlayerInteractEvent e) {
            if (e.getHand() != EquipmentSlot.HAND || !isRightClick(e.getAction())) return;
            if (e.getItem() == null || e.getItem().getType() != Material.BONE) return;
            runForAbility(e.getPlayer(), p -> {
                e.setCancelled(true);
                if (!cooldown.tryUse(p, COOLDOWN_MS)) return;
                dismiss(p.getUniqueId());
                Wolf huan = p.getWorld().spawn(p.getLocation(), Wolf.class, w -> {
                    w.setTamed(true);
                    w.setOwner(p);
                    w.customName(Component.text("Huan", NamedTextColor.GOLD));
                    w.setCustomNameVisible(true);
                    w.setSoundVariant(Wolf.SoundVariant.BIG);
                    setBase(w, Attribute.SCALE, 1.5);
                    setBase(w, Attribute.MAX_HEALTH, 60.0);
                    setBase(w, Attribute.ATTACK_DAMAGE, 8.0);
                    setBase(w, Attribute.KNOCKBACK_RESISTANCE, 0.5);
                    var speed = w.getAttribute(Attribute.MOVEMENT_SPEED);
                    if (speed != null) speed.setBaseValue(speed.getBaseValue() * 1.2);
                    w.setHealth(60.0);
                    // Never saved with the chunk, so a restart or unload can't leave a permanent Huan.
                    w.setPersistent(false);
                    w.setRemoveWhenFarAway(false);
                    w.getPersistentDataContainer().set(HUAN, PersistentDataType.STRING, p.getUniqueId().toString());
                });
                SUMMONED.put(p.getUniqueId(), huan);
                huan.getWorld().spawnParticle(Particle.SOUL, huan.getLocation().add(0, 0.8, 0), 30, 0.6, 0.6, 0.6, 0.02);
                huan.getWorld().playSound(huan.getLocation(), Sound.ENTITY_WOLF_BIG_GROWL, 1f, 0.8f);
                huan.getScheduler().runDelayed(JavaPlugin.getPlugin(ArdanBorn.class), _ -> depart(huan), null, STAY_TICKS);
            });
        }

        private static void setBase(Wolf wolf, Attribute attribute, double value) {
            var instance = wolf.getAttribute(attribute);
            if (instance != null) instance.setBaseValue(value);
        }

        private static boolean isHuan(Entity entity) {
            return entity instanceof Wolf && entity.getPersistentDataContainer().has(HUAN);
        }

        private static void depart(Wolf huan) {
            if (huan.isValid()) {
                huan.getWorld().spawnParticle(Particle.SOUL, huan.getLocation().add(0, 0.8, 0), 30, 0.6, 0.6, 0.6, 0.02);
                huan.remove();
            }
            SUMMONED.values().remove(huan);
        }

        private static void dismiss(UUID owner) {
            Wolf huan = SUMMONED.remove(owner);
            if (huan != null && huan.isValid()) huan.remove();
        }

        /// Called on plugin disable.
        public static void dismissAll() {
            for (Wolf huan : SUMMONED.values()) {
                if (huan.isValid()) huan.remove();
            }
            SUMMONED.clear();
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            dismiss(e.getPlayer().getUniqueId());
        }

        @EventHandler
        public void onOwnerDeath(PlayerDeathEvent e) {
            dismiss(e.getEntity().getUniqueId());
        }

        @EventHandler
        public void onHuanDeath(EntityDeathEvent e) {
            if (!isHuan(e.getEntity())) return;
            e.getDrops().clear();
            e.setDroppedExp(0);
            SUMMONED.values().remove(e.getEntity());
        }

        @EventHandler(ignoreCancelled = true)
        public void onBreed(EntityBreedEvent e) {
            if (isHuan(e.getMother()) || isHuan(e.getFather())) e.setCancelled(true);
        }

        /// No feeding, breeding or dyeing him; an empty hand still tells him to sit or stand.
        @EventHandler(ignoreCancelled = true)
        public void onInteract(PlayerInteractEntityEvent e) {
            if (!isHuan(e.getRightClicked())) return;
            if (!e.getPlayer().getInventory().getItem(e.getHand()).getType().isAir()) e.setCancelled(true);
        }
    }

    /** Herbalist: green things flourish in their care. */
    public static final class GreenThumb extends Base implements Tickable {
        private static final int RADIUS = 5;
        private static final int HEIGHT = 2;
        private static final double CROP_CHANCE = 0.05;
        private static final double SAPLING_CHANCE = 0.01;
        /// Plants that grow by age besides Tag.CROPS. The pitcher crop is two blocks tall and left alone.
        private static final Set<Material> EXTRA_PLANTS = EnumSet.of(
                Material.SWEET_BERRY_BUSH, Material.COCOA, Material.NETHER_WART);

        public GreenThumb() {
            super("green_thumb", "Green Thumb",
                    "All that grows is glad of you. Crops, berries and saplings within 5 blocks grow much the faster.");
        }

        @Override
        public void tick(Player player) {
            runForAbility(player, p -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                Block center = p.getLocation().getBlock();
                for (int x = -RADIUS; x <= RADIUS; x++) {
                    for (int y = -HEIGHT; y <= HEIGHT; y++) {
                        for (int z = -RADIUS; z <= RADIUS; z++) {
                            Block block = center.getRelative(x, y, z);
                            Material type = block.getType();
                            if (Tag.SAPLINGS.isTagged(type)) {
                                if (random.nextDouble() < SAPLING_CHANCE) block.applyBoneMeal(BlockFace.UP);
                                continue;
                            }
                            if (type == Material.PITCHER_CROP) continue;
                            if (!Tag.CROPS.isTagged(type) && !EXTRA_PLANTS.contains(type)) continue;
                            if (!(block.getBlockData() instanceof Ageable crop)) continue;
                            if (crop.getAge() >= crop.getMaximumAge() || random.nextDouble() >= CROP_CHANCE) continue;
                            crop.setAge(crop.getAge() + 1);
                            block.setBlockData(crop);
                            block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                                    block.getLocation().add(0.5, 0.5, 0.5), 2, 0.2, 0.2, 0.2, 0);
                        }
                    }
                }
            });
        }
    }

    /** Smith: work made to last, and blessed under the full moon. */
    public static final class SmithsMark extends Base implements Listener {
        /// Items that count as tools or weapons for the full-moon enchantment.
        private static final List<Tag<Material>> TOOLS_AND_WEAPONS = List.of(
                Tag.ITEMS_ENCHANTABLE_MINING, Tag.ITEMS_ENCHANTABLE_WEAPON, Tag.ITEMS_ENCHANTABLE_MELEE_WEAPON,
                Tag.ITEMS_ENCHANTABLE_SHARP_WEAPON, Tag.ITEMS_ENCHANTABLE_BOW, Tag.ITEMS_ENCHANTABLE_CROSSBOW,
                Tag.ITEMS_ENCHANTABLE_MACE, Tag.ITEMS_ENCHANTABLE_TRIDENT, Tag.ITEMS_ENCHANTABLE_LUNGE,
                Tag.ITEMS_ENCHANTABLE_FISHING);

        /// The full-moon enchantments and how likely each is: 10 common, 5 uncommon, 2 rare, 1 very rare.
        /// Only enchantments with levels are here (no Silk Touch, Mending, Infinity…), since every one is
        /// rolled above its vanilla maximum. Only those that fit the crafted item are drawn from.
        private static final Map<Enchantment, Integer> WEIGHTS = new LinkedHashMap<>();
        static {
            for (Enchantment e : List.of(Enchantment.EFFICIENCY, Enchantment.SHARPNESS, Enchantment.POWER,
                    Enchantment.QUICK_CHARGE, Enchantment.IMPALING, Enchantment.DENSITY, Enchantment.LUNGE,
                    Enchantment.LURE)) WEIGHTS.put(e, 10);
            for (Enchantment e : List.of(Enchantment.UNBREAKING, Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS,
                    Enchantment.KNOCKBACK, Enchantment.PUNCH, Enchantment.PIERCING, Enchantment.LOYALTY,
                    Enchantment.BREACH, Enchantment.LUCK_OF_THE_SEA)) WEIGHTS.put(e, 5);
            for (Enchantment e : List.of(Enchantment.FIRE_ASPECT, Enchantment.SWEEPING_EDGE, Enchantment.RIPTIDE,
                    Enchantment.WIND_BURST)) WEIGHTS.put(e, 2);
            for (Enchantment e : List.of(Enchantment.FORTUNE, Enchantment.LOOTING)) WEIGHTS.put(e, 1);
        }

        /// How many enchantments a moonlit item gets, by weight: usually one, rarely two, very rarely three.
        private static final int[] COUNT_WEIGHTS = {0, 89, 10, 1};

        /// Moonlit levels run from one above the vanilla maximum up to this many above it.
        private static final int LEVELS_ABOVE_MAX = 5;
        /// Lower tops where higher levels break the item: Quick Charge above V can't load a crossbow, and
        /// Lure that high stops fish biting.
        private static final Map<Enchantment, Integer> LEVEL_CAPS = Map.of(
                Enchantment.QUICK_CHARGE, 5, Enchantment.LURE, 5);

        /// Moonlit items a player may forge each full moon.
        private static final int PER_FULL_MOON = 2;
        /// Kept on the player, so relogging or a restart doesn't reset the count: the day of the full moon
        /// they last forged under, and how many moonlit items they've made that night. The count also seeds
        /// the roll, so the preview is the same however often the grid changes.
        private static final NamespacedKey MOON_DAY = Keys.key("moonlit_day");
        private static final NamespacedKey MOON_COUNT = Keys.key("moonlit_count");

        /// The registered instance, so other features (Cirth runes) can ask whether someone is a Smith.
        private static @Nullable SmithsMark instance;

        public SmithsMark() {
            super("smiths_mark", "Smith's Mark",
                    "You alone may inscribe the Cirth upon weapons and tools. And twice in each full moon, a tool or weapon that you forge beneath its open sky gains an enchantment beyond its usual maximum, by up to 5 levels.");
            instance = this;
        }

        /** Whether a player has Smith's Mark (the Smith class). */
        public static boolean isSmith(Player player) {
            return instance != null && instance.hasAbility(player);
        }

        private static long today(Player p) {
            return p.getWorld().getFullTime() / 24000L;
        }

        /// Moonlit items this player has forged tonight.
        private static int forgedTonight(Player p) {
            var pdc = p.getPersistentDataContainer();
            Long day = pdc.get(MOON_DAY, PersistentDataType.LONG);
            if (day == null || day != today(p)) return 0;
            return pdc.getOrDefault(MOON_COUNT, PersistentDataType.INTEGER, 0);
        }

        /// Whether an item carries a moonlit enchantment: any level above the vanilla maximum.
        private static boolean isMoonlit(@Nullable ItemStack item) {
            if (item == null || item.isEmpty()) return false;
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                if (entry.getValue() > entry.getKey().getMaxLevel()) return true;
            }
            return false;
        }

        /// Runs after RecipeGuardListener, which may have cleared the result. Changing the result slot
        /// also covers shift-click crafting, and shows in the preview.
        @EventHandler(priority = EventPriority.HIGH)
        public void onPrepare(PrepareItemCraftEvent e) {
            ItemStack result = e.getInventory().getResult();
            if (result == null || result.isEmpty()) return;
            runForAbility(e.getView().getPlayer(), p -> {
                if (!underFullMoon(p) || !isToolOrWeapon(result) || forgedTonight(p) >= PER_FULL_MOON) return;
                ItemStack marked = result.clone();
                if (addMoonlitEnchantment(p, marked)) e.getInventory().setResult(marked);
            });
        }

        /// A moonlit item must be taken one at a time: a shift-click could forge several at once and pass
        /// the nightly limit. Taking one counts towards it, and the next rolls afresh.
        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onCraft(CraftItemEvent e) {
            if (!(e.getWhoClicked() instanceof Player player)) return;
            if (!isMoonlit(e.getCurrentItem())) return;
            if (e.isShiftClick()) {
                e.setCancelled(true);
                player.sendActionBar(Component.text("The work of moonlight is taken singly.", NamedTextColor.GRAY));
                return;
            }
            int forged = forgedTonight(player) + 1;
            var pdc = player.getPersistentDataContainer();
            pdc.set(MOON_DAY, PersistentDataType.LONG, today(player));
            pdc.set(MOON_COUNT, PersistentDataType.INTEGER, forged);
            int left = PER_FULL_MOON - forged;
            player.sendActionBar(Component.text(left > 0
                    ? "The moon is upon your work. " + left + " more tonight."
                    : "The moon has no more to give this night.", NamedTextColor.AQUA));
        }

        private static boolean isToolOrWeapon(ItemStack item) {
            for (Tag<Material> tag : TOOLS_AND_WEAPONS) {
                if (tag.isTagged(item.getType())) return true;
            }
            return false;
        }

        /// Night of a full moon in the Overworld, with nothing between you and the sky.
        private static boolean underFullMoon(Player p) {
            World world = p.getWorld();
            if (world.getEnvironment() != World.Environment.NORMAL) return false;
            long time = world.getTime();
            if (time < 13000 || time > 23000) return false;
            if ((world.getFullTime() / 24000L) % 8L != 0) return false; // phase 0 = full moon
            return p.getLocation().getBlock().getLightFromSky() == 15;
        }

        private boolean addMoonlitEnchantment(Player p, ItemStack item) {
            Random random = new Random(Objects.hash(p.getUniqueId(), today(p), item.getType(), forgedTonight(p)));

            int wanted = pickWeighted(random, COUNT_WEIGHTS);
            boolean added = false;
            for (int i = 0; i < wanted; i++) {
                Enchantment chosen = drawEnchantment(random, item);
                if (chosen == null) break; // nothing else fits
                int min = chosen.getMaxLevel() + 1;
                int max = Math.min(chosen.getMaxLevel() + LEVELS_ABOVE_MAX, LEVEL_CAPS.getOrDefault(chosen, Integer.MAX_VALUE));
                // Unsafe: these levels are beyond what the game allows normally.
                item.addUnsafeEnchantment(chosen, min + pickWeighted(random, descendingWeights(max - min + 1)));
                added = true;
            }
            return added;
        }

        /// One enchantment from the table that fits the item, doesn't clash with what it has, and isn't
        /// already at a moonlit level (so an enchantment the recipe already gave can still be raised).
        private static @Nullable Enchantment drawEnchantment(Random random, ItemStack item) {
            List<Enchantment> options = new ArrayList<>();
            List<Integer> weights = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> entry : WEIGHTS.entrySet()) {
                Enchantment ench = entry.getKey();
                if (!ench.canEnchantItem(item) || item.getEnchantmentLevel(ench) > ench.getMaxLevel()) continue;
                boolean conflicts = item.getEnchantments().keySet().stream()
                        .anyMatch(other -> other != ench && ench.conflictsWith(other));
                if (conflicts) continue;
                options.add(ench);
                weights.add(entry.getValue());
            }
            if (options.isEmpty()) return null;
            return options.get(pickWeighted(random, weights.stream().mapToInt(Integer::intValue).toArray()));
        }

        /// The index of one entry, each as likely as its weight.
        private static int pickWeighted(Random random, int[] weights) {
            int total = 0;
            for (int w : weights) total += w;
            int roll = random.nextInt(total);
            for (int i = 0; i < weights.length; i++) {
                roll -= weights[i];
                if (roll < 0) return i;
            }
            return weights.length - 1;
        }

        /// Weights n, n-1 … 1, so the lowest level is likeliest. E.g. Fortune IV–VIII: IV is 5 times as
        /// likely as VIII.
        private static int[] descendingWeights(int n) {
            int[] weights = new int[n];
            for (int i = 0; i < n; i++) weights[i] = n - i;
            return weights;
        }
    }
}
