package dev.grapeofwrath.ardanborn.adornments;

import com.mojang.brigadier.Command;
import dev.grapeofwrath.ardanborn.Keys;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/// The adornments screen, and the three ways to it: `/adornments`, a button in the result slot of the
/// inventory's 2x2 crafting grid, and (without opening it) right-clicking with an adornment.
///
/// Clients can't be given more slots in the inventory screen itself.
final class AdornmentScreen implements Listener {
    /// On the button and on the items that fill unused slots. They never leave the screen.
    private static final NamespacedKey MARK = Keys.key("screen_item");
    private static final Adornment.Slot[] SLOTS = Adornment.Slot.values();
    private static final int[] COLUMNS = {2, 4, 6};
    private static final int OFF_HAND = 40;

    private final Plugin plugin;

    private AdornmentScreen(Plugin plugin) {
        this.plugin = plugin;
    }

    static void register(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new AdornmentScreen(plugin), plugin);
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(
                Commands.literal("adornments")
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player) open(player);
                            return Command.SINGLE_SUCCESS;
                        })
                        .build(), "Opens your adornments"));
    }

    private static void open(Player player) {
        player.openInventory(new View(player).getInventory());
    }

    private static boolean none(@Nullable ItemStack item) {
        return item == null || item.isEmpty();
    }

    private static boolean fits(ItemStack item, Adornment.Slot slot) {
        Adornment adornment = Adornment.of(item);
        return adornment != null && adornment.slot() == slot && item.getAmount() == 1;
    }

    private static ItemStack mark(Material material, @Nullable String packModel, @Nullable Component name) {
        ItemStack item = ItemStack.of(material);
        if (name == null) item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true));
        else item.setData(DataComponentTypes.ITEM_NAME, name);
        if (packModel != null) item.setData(DataComponentTypes.ITEM_MODEL, Keys.key(packModel));
        item.editPersistentDataContainer(pdc -> pdc.set(MARK, PersistentDataType.BOOLEAN, true));
        return item;
    }

    private static boolean isMark(@Nullable ItemStack item) {
        return !none(item) && item.getPersistentDataContainer().has(MARK);
    }

    // ------------------------------------------------------------------ the screen

    private static final class View implements InventoryHolder {
        private final Player player;
        private final boolean pack;
        private final Inventory inventory;

        View(Player player) {
            this.player = player;
            this.pack = AdornmentPack.loaded(player);
            // The pack draws the slots with a glyph in the title; without it the title names them.
            Component title = pack
                    ? Component.text("", NamedTextColor.WHITE).font(Key.key(Keys.NAMESPACE, "screen"))
                            .append(Component.text("Adornments", NamedTextColor.DARK_GRAY).font(Key.key("minecraft", "default")))
                    : Component.text("Necklace, back and belt");
            this.inventory = Bukkit.createInventory(this, 9, title);
            draw();
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }

        void draw() {
            for (int i = 0; i < 9; i++) inventory.setItem(i, mark(Material.BLACK_STAINED_GLASS_PANE, pack ? "empty" : null, null));
            for (Adornment.Slot slot : SLOTS) {
                ItemStack item = Adornments.get(player, slot);
                inventory.setItem(COLUMNS[slot.ordinal()], item != null ? item
                        : mark(Material.LIGHT_GRAY_STAINED_GLASS_PANE, pack ? "slot_" + slot.id() : null,
                        Component.text(slot.label(), NamedTextColor.GRAY)));
            }
        }

        @Nullable Adornment.Slot at(int raw) {
            for (Adornment.Slot slot : SLOTS) if (COLUMNS[slot.ordinal()] == raw) return slot;
            return null;
        }
    }

    private void click(InventoryClickEvent event, View view) {
        Player player = view.player;
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
            return;
        }
        int raw = event.getRawSlot();
        if (raw < 0) return;
        if (raw >= view.inventory.getSize()) {
            if (!event.isShiftClick()) return;
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            Adornment adornment = Adornment.of(item);
            if (adornment == null || !fits(item, adornment.slot()) || Adornments.get(player, adornment.slot()) != null) return;
            Adornments.set(player, adornment.slot(), item);
            event.setCurrentItem(null);
            view.draw();
            return;
        }
        event.setCancelled(true);
        Adornment.Slot slot = view.at(raw);
        if (slot == null) return;
        ItemStack worn = Adornments.get(player, slot);
        PlayerInventory inventory = player.getInventory();
        switch (event.getClick()) {
            case LEFT, RIGHT -> {
                ItemStack cursor = event.getCursor();
                if (!none(cursor) && !fits(cursor, slot)) return;
                Adornments.set(player, slot, cursor);
                event.getView().setCursor(worn);
            }
            case SHIFT_LEFT, SHIFT_RIGHT -> {
                if (worn == null || inventory.firstEmpty() < 0) return;
                Adornments.set(player, slot, null);
                inventory.addItem(worn);
            }
            case NUMBER_KEY, SWAP_OFFHAND -> {
                int at = event.getClick() == ClickType.SWAP_OFFHAND ? OFF_HAND : event.getHotbarButton();
                ItemStack other = inventory.getItem(at);
                if (!none(other) && !fits(other, slot)) return;
                Adornments.set(player, slot, other);
                inventory.setItem(at, worn);
            }
            case DROP, CONTROL_DROP -> {
                if (worn == null) return;
                Adornments.set(player, slot, null);
                player.dropItem(worn);
            }
            default -> {
                return;
            }
        }
        view.draw();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof View view) {
            click(event, view);
        } else if (isMark(event.getCurrentItem())) {
            event.setCancelled(true);
            if (event.isLeftClick() && none(event.getCursor()) && event.getWhoClicked() instanceof Player player) {
                later(player, AdornmentScreen::open);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top.getHolder(false) instanceof View
                && event.getRawSlots().stream().anyMatch(raw -> raw < top.getSize())) event.setCancelled(true);
    }

    // ------------------------------------------------------------------ right-click

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() == null || event.useItemInHand() == Event.Result.DENY) return;
        ItemStack item = event.getItem();
        Adornment adornment = Adornment.of(item);
        if (adornment == null) return;
        Player player = event.getPlayer();
        // Chests, doors and the like come first, as with any item.
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().isInteractable() && !player.isSneaking()) return;
        event.setCancelled(true);
        if (Adornments.get(player, adornment.slot()) != null || !fits(item, adornment.slot())) {
            player.sendActionBar(Component.text("You already wear an adornment there.", NamedTextColor.GRAY));
            return;
        }
        Adornments.set(player, adornment.slot(), item);
        player.getInventory().setItem(event.getHand(), null);
        player.getWorld().playSound(player, Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);
    }

    // ------------------------------------------------------------------ the button

    private void later(Player player, Consumer<Player> task) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) task.accept(player);
        });
    }

    /// Shows the button while the 2x2 grid is empty.
    private void button(Player player) {
        // The grid is only reachable while no other screen is open. In creative there is none.
        if (player.getOpenInventory().getType() != InventoryType.CRAFTING
                || !(player.getOpenInventory().getTopInventory() instanceof CraftingInventory grid)) return;
        for (ItemStack item : grid.getMatrix()) if (!none(item)) return;
        grid.setResult(mark(Material.ARMOR_STAND, AdornmentPack.loaded(player) ? "adornments_button" : null,
                Component.text("Adornments", NamedTextColor.GOLD)));
        // The client empties its own result slot whenever the inventory screen goes away.
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepare(PrepareItemCraftEvent event) {
        if (event.getView().getType() == InventoryType.CRAFTING && event.getView().getPlayer() instanceof Player player) {
            later(player, this::button);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) later(player, this::button);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        later(event.getPlayer(), this::button);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        later(event.getPlayer(), this::button);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameMode(PlayerGameModeChangeEvent event) {
        later(event.getPlayer(), this::button);
    }
}
