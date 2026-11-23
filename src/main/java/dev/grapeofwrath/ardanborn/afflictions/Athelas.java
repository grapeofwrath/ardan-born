package dev.grapeofwrath.ardanborn.afflictions;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.weapons.ToolUpgrade;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/// Athelas, kingsfoil: a common-looking herb with a rare virtue. Anyone may find it (1% from ferns and
/// grass) and use it: right-click to cure your own Morgul wound, or sneak and right-click a wounded player
/// to cure theirs. Used on someone who isn't wounded, it gives a moment's Regeneration.
public final class Athelas implements Listener {
    public static final String ID = "athelas";
    private static final TextColor COLOR = TextColor.color(0x8FD18A);
    private static final double DROP_CHANCE = 0.01;
    private static final Set<Material> SOURCES = Set.of(Material.FERN, Material.LARGE_FERN, Material.SHORT_GRASS);

    public static void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new Athelas(), plugin);
    }

    public static ItemStack create() {
        // Looks like a fern, but is built on prismarine crystals so it can't be planted or used in recipes.
        ItemStack item = ItemStack.of(Material.PRISMARINE_CRYSTALS);
        item.setData(DataComponentTypes.ITEM_MODEL, Material.FERN.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Athelas", COLOR));
        item.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        item.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("Kingsfoil. In the hands of the king", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.text("it has a healing virtue.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Right-click: cure a Morgul wound", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("Sneak + right-click a player: cure theirs", NamedTextColor.BLUE))
                .build());
        CustomItems.tag(item, ID);
        return item;
    }

    public static boolean is(@Nullable ItemStack item) {
        return ID.equals(CustomItems.idOf(item));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!SOURCES.contains(event.getBlock().getType())) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (ThreadLocalRandom.current().nextDouble() >= DROP_CHANCE) return;
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.3, 0.5), create());
    }

    @EventHandler
    public void onUseOnPlayer(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player healer = event.getPlayer();
        if (!healer.isSneaking() || !(event.getRightClicked() instanceof Player target)) return;
        if (!is(healer.getInventory().getItemInMainHand())) return;
        event.setCancelled(true);
        apply(healer, target);
    }

    @EventHandler
    public void onUseOnSelf(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND || !is(event.getItem())) return;
        event.setCancelled(true);
        apply(event.getPlayer(), event.getPlayer());
    }

    private static void apply(Player user, Player target) {
        if (!MorgulWound.cure(target)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0, false, true, true));
        }
        target.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, target.getLocation().add(0, 1.2, 0), 12, 0.4, 0.5, 0.4, 0);
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_AZALEA_LEAVES_BREAK, 1f, 1.2f);
        if (user.getGameMode() != GameMode.CREATIVE) {
            user.getInventory().getItemInMainHand().subtract();
        }
    }
}
