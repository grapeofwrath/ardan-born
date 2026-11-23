package dev.grapeofwrath.ardanborn.items.rings;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.weapons.ToolUpgrade;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/// The Rings of Power as items: making them, finding them in loot, and keeping the unique rings (the Three
/// and the One) unique.
///
/// Each unique ring has a generation, stored on the item and in `rings.yml`. While a ring of the current
/// generation exists, it can't be found again. If it's lost (it leaves the world without being picked up,
/// or nobody carries it for [#ABANDONED_MS]), the generation goes up and the ring can be found again; any
/// older copy that turns up later crumbles. So there's never more than one.
///
/// What the rings do when worn is in [RingBearer].
public final class Rings implements Listener {
    private static final NamespacedKey RING = Keys.key("ring");
    private static final NamespacedKey GENERATION = Keys.key("ring_generation");
    private static final String ID_PREFIX = "ring_";
    private static final long ABANDONED_MS = 7L * 24 * 60 * 60 * 1000;
    private static final double COMMON_CHANCE = 0.03;
    private static final double UNIQUE_CHANCE = 0.005;
    /// Ways a dropped ring can be removed that it shrugs off: item-clearing plugins, `/kill`, and the like.
    /// It is put straight back. (Falling out of the world loses it; being picked up or unloaded is normal.)
    private static final Set<EntityRemoveEvent.Cause> PUT_BACK = EnumSet.of(
            EntityRemoveEvent.Cause.PLUGIN, EntityRemoveEvent.Cause.DISCARD, EntityRemoveEvent.Cause.DEATH,
            EntityRemoveEvent.Cause.DESPAWN, EntityRemoveEvent.Cause.EXPLODE, EntityRemoveEvent.Cause.HIT);

    private static Plugin plugin;

    private static File dataFile;
    private static final YamlConfiguration DATA = new YamlConfiguration();

    public static void registerAll(Plugin plugin) {
        Rings.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "rings.yml");
        if (dataFile.exists()) {
            try {
                DATA.load(dataFile);
            } catch (Exception e) {
                plugin.getLogger().severe("Could not read rings.yml: " + e.getMessage());
            }
        }
        Rings rings = new Rings();
        Bukkit.getPluginManager().registerEvents(rings, plugin);
        // Once a minute: note who carries the unique rings, crumble stale copies, release abandoned rings.
        Bukkit.getScheduler().runTaskTimer(plugin, rings::checkCarried, 1200L, 1200L);
        RingBearer bearer = new RingBearer();
        Bukkit.getPluginManager().registerEvents(bearer, plugin);
        bearer.start(plugin);
    }

    // ------------------------------------------------------------------ items

    /// A new ring. Unique rings get the current generation and are recorded as existing.
    public static ItemStack create(RingType type) {
        ItemStack item = ItemStack.of(type.look());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(type.displayName(), type.color()));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData().addString(ID_PREFIX + type.id()).build());
        ItemLore.Builder lore = ItemLore.lore()
                .addLine(Component.text(type.flavor(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty());
        for (String line : type.powers()) lore.addLine(ToolUpgrade.lore(line, NamedTextColor.BLUE));
        lore.addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Hold in your off-hand and", NamedTextColor.DARK_PURPLE))
                .addLine(ToolUpgrade.lore("right-click to put it on.", NamedTextColor.DARK_PURPLE))
                .addLine(ToolUpgrade.lore("It builds Shadow while worn.", NamedTextColor.DARK_PURPLE));
        item.setData(DataComponentTypes.LORE, lore.build());
        item.editPersistentDataContainer(pdc -> {
            pdc.set(RING, PersistentDataType.STRING, type.id());
            if (type.unique()) pdc.set(GENERATION, PersistentDataType.INTEGER, generation(type));
        });
        CustomItems.tag(item, ID_PREFIX + type.id());
        if (type.unique()) {
            DATA.set(type.id() + ".exists", true);
            DATA.set(type.id() + ".last-held", System.currentTimeMillis());
            save();
        }
        return item;
    }

    /** The ring an item is, or null. A stale copy of a unique ring still counts here; see [#isStale]. */
    public static @Nullable RingType typeOf(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        return RingType.byId(item.getPersistentDataContainer().get(RING, PersistentDataType.STRING));
    }

    /// A copy of a unique ring from an older generation: the ring was lost and can be found again, so this
    /// one must go.
    public static boolean isStale(@Nullable ItemStack item) {
        RingType type = typeOf(item);
        if (type == null || !type.unique()) return false;
        Integer gen = item.getPersistentDataContainer().get(GENERATION, PersistentDataType.INTEGER);
        return gen == null || gen != generation(type);
    }

    /** `/ardanborn give` ids: `ring_<type>`. Giving a unique ring makes it the current one. */
    public static Map<String, Supplier<ItemStack>> giveableItems() {
        Map<String, Supplier<ItemStack>> items = new LinkedHashMap<>();
        for (RingType type : RingType.values()) items.put(ID_PREFIX + type.id(), () -> create(type));
        return items;
    }

    // ------------------------------------------------------------------ unique-ring records

    private static int generation(RingType type) {
        return DATA.getInt(type.id() + ".generation", 1);
    }

    private static boolean exists(RingType type) {
        return DATA.getBoolean(type.id() + ".exists", false);
    }

    /// The ring is gone: it can be found again, and any copy still around is now stale.
    public static void release(RingType type) {
        if (!type.unique()) return;
        DATA.set(type.id() + ".generation", generation(type) + 1);
        DATA.set(type.id() + ".exists", false);
        save();
    }

    private static void save() {
        if (dataFile == null) return;
        try {
            DATA.save(dataFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[ArdanBorn] Could not save rings.yml: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ finding rings

    @EventHandler(ignoreCancelled = true)
    public void onLoot(LootGenerateEvent event) {
        String table = event.getLootTable().getKey().getKey();
        if (!table.startsWith("chests/ancient_city") && !table.startsWith("chests/stronghold")
                && !table.startsWith("chests/bastion")) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (RingType type : RingType.values()) {
            if (type.unique()) {
                if (exists(type) || random.nextDouble() >= UNIQUE_CHANCE) continue;
            } else if (random.nextDouble() >= COMMON_CHANCE) {
                continue;
            }
            event.getLoot().add(create(type));
        }
    }

    // ------------------------------------------------------------------ keeping unique rings safe

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        RingType type = typeOf(stack);
        if (type == null || !type.unique()) return;
        if (isStale(stack)) {
            event.setCancelled(true);
            return;
        }
        event.getEntity().setUnlimitedLifetime(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDespawn(ItemDespawnEvent event) {
        RingType type = typeOf(event.getEntity().getItemStack());
        if (type != null && type.unique()) event.setCancelled(true);
    }

    /// Unique rings can't burn, blow up or be crushed, with one exception: the One Ring in the Nether's
    /// lava, where it was made. That is Mount Doom.
    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;
        RingType type = typeOf(item.getItemStack());
        if (type == null || !type.unique()) return;
        if (type == RingType.ONE && event.getCause() == EntityDamageEvent.DamageCause.LAVA
                && item.getWorld().getEnvironment() == World.Environment.NETHER
                && !isStale(item.getItemStack())) {
            event.setCancelled(true);
            mountDoom(item);
            return;
        }
        event.setCancelled(true);
        item.setFireTicks(0);
    }

    private static void mountDoom(Item item) {
        item.getWorld().spawnParticle(Particle.LAVA, item.getLocation(), 40, 0.5, 0.5, 0.5, 0);
        item.getWorld().playSound(item.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.6f);
        // Released first, so the removal below isn't counted as losing the ring.
        release(RingType.ONE);
        item.remove();
        Bukkit.broadcast(Component.text("The One Ring is unmade in the fire. The Shadow passes.",
                RingType.ONE.color()));
        for (Player player : Bukkit.getOnlinePlayers()) RingBearer.setShadow(player, 0);
        if (item.getThrower() != null) {
            Player destroyer = Bukkit.getPlayer(item.getThrower());
            if (destroyer != null) destroyer.giveExp(1395); // levels 0 → 30
        }
    }

    @EventHandler
    public void onRemove(EntityRemoveEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;
        ItemStack stack = item.getItemStack();
        RingType type = typeOf(stack);
        if (type == null || !type.unique() || isStale(stack)) return;
        if (event.getCause() == EntityRemoveEvent.Cause.OUT_OF_WORLD) {
            release(type);
            return;
        }
        // An emptied stack was taken in somewhere (a hopper, a pickup), so there's nothing to put back.
        if (!PUT_BACK.contains(event.getCause()) || stack.isEmpty()) return;
        ItemStack copy = stack.clone();
        Location at = item.getLocation();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (isStale(copy) || at.getWorld() == null) return;
            Item back = at.getWorld().dropItem(at, copy);
            back.setVelocity(new Vector());
            back.setUnlimitedLifetime(true);
            back.setInvulnerable(true);
            back.setThrower(item.getThrower());
        });
    }

    /// Admin: send a unique ring back into the loot pool. Any copy still around crumbles when next seen.
    public static boolean releaseByCommand(RingType type) {
        if (!type.unique()) return false;
        release(type);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (crumbleStale(player.getInventory())) crumbleMessage(player);
        }
        for (World world : Bukkit.getWorlds()) {
            for (Item item : world.getEntitiesByClass(Item.class)) {
                if (isStale(item.getItemStack())) item.remove();
            }
        }
        return true;
    }

    /// Stale copies crumble on sight: when picked up, when their container opens, when their holder joins.
    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!isStale(event.getItem().getItemStack())) return;
        event.setCancelled(true);
        event.getItem().remove();
        if (event.getEntity() instanceof Player player) crumbleMessage(player);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (crumbleStale(event.getInventory()) && event.getPlayer() instanceof Player player) crumbleMessage(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (crumbleStale(player.getInventory()) | crumbleStale(player.getEnderChest())) crumbleMessage(player);
    }

    /** Removes stale rings from an inventory; true if any were removed. */
    static boolean crumbleStale(Inventory inventory) {
        boolean any = false;
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (isStale(contents[i])) {
                inventory.setItem(i, null);
                any = true;
            }
        }
        return any;
    }

    static void crumbleMessage(Player player) {
        player.sendMessage(Component.text("The ring slips away into shadow…", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, true));
    }

    private void checkCarried() {
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (crumbleStale(player.getInventory())) crumbleMessage(player);
            for (ItemStack stack : player.getInventory().getContents()) {
                RingType type = typeOf(stack);
                if (type != null && type.unique()) DATA.set(type.id() + ".last-held", now);
            }
        }
        boolean changed = false;
        for (RingType type : RingType.values()) {
            if (!type.unique() || !exists(type)) continue;
            long lastHeld = DATA.getLong(type.id() + ".last-held", now);
            if (now - lastHeld > ABANDONED_MS) {
                release(type);
                changed = true;
            }
        }
        if (!changed) save();
    }
}
