package dev.grapeofwrath.ardanborn.mounts;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/// The Mearas: rare white horses, bred by the Rohirrim or found wild in the plains; and Shadowfax, their
/// lord, of whom there is one in the world.
///
/// Shadowfax's place is taken once he is tamed, or at birth if he is bred. A wild one doesn't take it, or
/// one nobody ever found would keep him from the world. When the place is taken, any other untamed
/// Shadowfax becomes an ordinary Meara as soon as it is loaded.
public final class Mearas implements Listener {
    /// On a Meara: `meara` or `shadowfax`.
    private static final NamespacedKey KIND = Keys.key("meara");
    /// On a horse whose one roll has been made.
    private static final NamespacedKey ROLLED = Keys.key("meara_rolled");
    private static final String MEARA = "meara";
    private static final String SHADOWFAX = "shadowfax";

    /// The best a vanilla horse can have.
    private static final double BEST_SPEED = 0.3375;
    private static final double BEST_JUMP = 1.0;
    private static final double CALL_RANGE = 128;
    private static final long CALL_COOLDOWN_MS = 10_000;
    private static final Set<SpawnReason> NOT_WILD = EnumSet.of(SpawnReason.SPAWNER_EGG, SpawnReason.COMMAND,
            SpawnReason.CUSTOM, SpawnReason.BREEDING);

    private static @Nullable Mearas instance;

    private final Plugin plugin;
    private final File file;
    private final double chance;
    private final double shadowfaxChance;
    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();

    // The living, claimed Shadowfax; null when there is none.
    private @Nullable UUID shadowfax;
    private @Nullable UUID owner;
    private @Nullable UUID world;
    private double x;
    private double y;
    private double z;

    private Mearas(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "shadowfax.yml");
        this.chance = Math.clamp(plugin.getConfig().getDouble("mearas.chance", 0.02), 0, 1);
        this.shadowfaxChance = Math.clamp(plugin.getConfig().getDouble("mearas.shadowfax-chance", 0.2), 0, 1);
    }

    public static void register(Plugin plugin) {
        instance = new Mearas(plugin);
        instance.load();
        Bukkit.getPluginManager().registerEvents(instance, plugin);
        // Keep track of where he is, for when his chunk is unloaded.
        Bukkit.getScheduler().runTaskTimer(plugin, instance::locate, 100L, 100L);
    }

    /// Called on plugin disable.
    public static void shutdown() {
        if (instance == null) return;
        instance.locate();
        if (instance.shadowfax != null) instance.save();
        instance = null;
    }

    // ------------------------------------------------------------------ the record of Shadowfax

    private void load() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        try {
            if (!yaml.contains("horse")) return;
            UUID horse = UUID.fromString(yaml.getString("horse", ""));
            UUID master = UUID.fromString(yaml.getString("owner", ""));
            world = UUID.fromString(yaml.getString("world", ""));
            x = yaml.getDouble("x");
            y = yaml.getDouble("y");
            z = yaml.getDouble("z");
            owner = master;
            shadowfax = horse;
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Could not read " + file.getName() + ": " + e.getMessage());
        }
    }

    private void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        if (shadowfax != null && owner != null && world != null) {
            yaml.set("horse", shadowfax.toString());
            yaml.set("owner", owner.toString());
            yaml.set("world", world.toString());
            yaml.set("x", x);
            yaml.set("y", y);
            yaml.set("z", z);
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save " + file.getName() + ": " + e.getMessage());
        }
    }

    private void locate() {
        if (shadowfax != null && Bukkit.getEntity(shadowfax) instanceof Horse horse) remember(horse.getLocation());
    }

    private void remember(Location at) {
        world = at.getWorld().getUID();
        x = at.getX();
        y = at.getY();
        z = at.getZ();
    }

    private void claim(Horse horse, Player master) {
        shadowfax = horse.getUniqueId();
        owner = master.getUniqueId();
        remember(horse.getLocation());
        save();
        for (World each : Bukkit.getWorlds()) {
            for (Horse other : each.getEntitiesByClass(Horse.class)) humble(other);
        }
    }

    /// An untamed Shadowfax who came too late is an ordinary Meara.
    private void humble(Horse horse) {
        if (shadowfax == null || horse.getUniqueId().equals(shadowfax) || horse.isTamed()) return;
        if (SHADOWFAX.equals(kind(horse))) make(horse, MEARA);
    }

    // ------------------------------------------------------------------ the making of Mearas

    private static @Nullable String kind(Entity entity) {
        return entity.getPersistentDataContainer().get(KIND, PersistentDataType.STRING);
    }

    /// Makes the horse's one roll. True if it became Shadowfax.
    private boolean roll(Horse horse) {
        horse.getPersistentDataContainer().set(ROLLED, PersistentDataType.BOOLEAN, true);
        if (ThreadLocalRandom.current().nextDouble() >= chance) return false;
        boolean lord = shadowfax == null && ThreadLocalRandom.current().nextDouble() < shadowfaxChance;
        make(horse, lord ? SHADOWFAX : MEARA);
        return lord;
    }

    private static void make(Horse horse, String kind) {
        boolean lord = kind.equals(SHADOWFAX);
        horse.getPersistentDataContainer().set(KIND, PersistentDataType.STRING, kind);
        horse.setColor(Horse.Color.WHITE);
        horse.setStyle(Horse.Style.NONE);
        horse.customName(Component.text(lord ? "Shadowfax" : "Meara", NamedTextColor.GOLD));
        base(horse, Attribute.MAX_HEALTH, lord ? 60 : 40);
        base(horse, Attribute.MOVEMENT_SPEED, BEST_SPEED * (lord ? 1.25 : 1.1));
        base(horse, Attribute.JUMP_STRENGTH, BEST_JUMP * (lord ? 1.1 : 1));
        base(horse, Attribute.SCALE, lord ? 1.15 : 1);
        horse.setHealth(lord ? 60 : 40);
    }

    private static void base(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance found = entity.getAttribute(attribute);
        if (found != null) found.setBaseValue(value);
    }

    /// After Breeder of the Mark has set the foal's qualities, which a Meara's replace.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getEntity() instanceof Horse foal)) return;
        if (!(event.getBreeder() instanceof Player breeder) || !OriginAbilities.BreederOfTheMark.has(breeder)) {
            foal.getPersistentDataContainer().set(ROLLED, PersistentDataType.BOOLEAN, true);
            return;
        }
        if (roll(foal)) {
            foal.setTamed(true);
            foal.setOwner(breeder);
            claim(foal, breeder);
        }
    }

    @EventHandler
    public void onAdd(EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof Horse horse)) return;
        humble(horse);
        if (horse.isTamed() || horse.getPersistentDataContainer().has(ROLLED)) return;
        if (NOT_WILD.contains(horse.getEntitySpawnReason())) return;
        Biome biome = horse.getLocation().getBlock().getBiome();
        if (biome == Biome.PLAINS || biome == Biome.SUNFLOWER_PLAINS) roll(horse);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        if (!(event.getEntity() instanceof Horse horse) || kind(horse) == null) return;
        if (event.getOwner() instanceof Player player && OriginCheck.is(player, OriginCheck.ROHIRRIM)) return;
        event.setCancelled(true);
        if (event.getOwner() instanceof Player player) {
            player.sendActionBar(Component.text("The Mearas suffer none to tame them save the Rohirrim.", NamedTextColor.GOLD));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTamed(EntityTameEvent event) {
        if (!(event.getEntity() instanceof Horse horse) || !SHADOWFAX.equals(kind(horse))) return;
        if (event.getOwner() instanceof Player player && shadowfax == null) claim(horse, player);
        else humble(horse);
    }

    /// He is gone only if he is nowhere a tick later: passing between worlds is not death.
    @EventHandler
    public void onRemove(EntityRemoveEvent event) {
        UUID id = event.getEntity().getUniqueId();
        if (!id.equals(shadowfax)) return;
        if (event.getCause() == EntityRemoveEvent.Cause.UNLOAD) {
            remember(event.getEntity().getLocation());
            save();
            return;
        }
        if (!plugin.isEnabled()) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!id.equals(shadowfax) || Bukkit.getEntity(id) != null) return;
            shadowfax = null;
            owner = null;
            save();
        });
    }

    // ------------------------------------------------------------------ the calling of Shadowfax

    @EventHandler(ignoreCancelled = true)
    public void onCall(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (shadowfax == null || !player.isSneaking() || !player.getUniqueId().equals(owner)) return;
        locate();
        if (!player.getWorld().getUID().equals(world)) return;
        Location last = new Location(player.getWorld(), x, y, z);
        if (last.distanceSquared(player.getLocation()) > CALL_RANGE * CALL_RANGE) return;
        Horse near = find();
        if (near != null && !canCome(near)) return;
        if (!cooldown.tryUseQuiet(player, CALL_COOLDOWN_MS)) return;
        event.setCancelled(true);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_AMBIENT, 1f, 1.2f);
        if (near != null) come(near, player);
        else fetch(player, last);
    }

    private @Nullable Horse find() {
        return shadowfax != null && Bukkit.getEntity(shadowfax) instanceof Horse horse && horse.isValid() ? horse : null;
    }

    private static boolean canCome(Horse horse) {
        return horse.isAdult() && horse.getPassengers().isEmpty();
    }

    private static void come(Horse horse, Player player) {
        horse.setLeashHolder(null);
        horse.teleport(player.getLocation());
        player.sendActionBar(Component.text("Shadowfax comes.", NamedTextColor.GOLD));
    }

    /// Loads the chunk he was last in, and waits up to 3 seconds for him.
    private void fetch(Player player, Location last) {
        player.getWorld().getChunkAtAsync(last).thenAccept(chunk -> Bukkit.getScheduler().runTask(plugin, () -> wait(player, chunk)));
    }

    private void wait(Player player, Chunk chunk) {
        chunk.addPluginChunkTicket(plugin);
        new BukkitRunnable() {
            private int left = 12;

            @Override
            public void run() {
                Horse horse = find();
                if (horse == null && --left > 0 && player.isOnline()) return;
                cancel();
                if (horse != null && player.isOnline() && canCome(horse)
                        && horse.getWorld() == player.getWorld()) come(horse, player);
                chunk.removePluginChunkTicket(plugin);
            }
        }.runTaskTimer(plugin, 1L, 5L);
    }
}
