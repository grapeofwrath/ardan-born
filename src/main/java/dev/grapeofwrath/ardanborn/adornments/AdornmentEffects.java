package dev.grapeofwrath.ardanborn.adornments;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.Set;

/// What the adornments do.
public final class AdornmentEffects implements Listener {
    private static final Set<PotionEffectType> LIFTED =
            Set.of(PotionEffectType.DARKNESS, PotionEffectType.BLINDNESS, PotionEffectType.WEAKNESS);
    private static final double SHADOW_REDUCTION = 0.25;
    private static final double DISCOUNT = 0.15;
    private static final double EMERALD_CHANCE = 0.25;
    private static final double ARROW_SPEED = 1.1;
    private static final double ARROW_CHANCE = 0.25;
    private static final float EXTRA_SATURATION = 0.5f;
    private static final double FOOD_CHANCE = 0.2;
    private static final Set<Material> COOKED = EnumSet.of(Material.COOKED_BEEF, Material.COOKED_PORKCHOP,
            Material.COOKED_CHICKEN, Material.COOKED_MUTTON, Material.COOKED_RABBIT, Material.COOKED_COD,
            Material.COOKED_SALMON, Material.BAKED_POTATO);
    private static final double HURT_REDUCTION = 0.15;
    private static final Set<EntityDamageEvent.DamageCause> UNSTOPPABLE = EnumSet.of(
            EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.KILL,
            EntityDamageEvent.DamageCause.SUICIDE);
    private static final double NO_WEAR_CHANCE = 0.5;

    private final Plugin plugin;
    /// While a shortened effect is being given, so that it isn't shortened again.
    private boolean shortening;

    AdornmentEffects(Plugin plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------------ White Gem of Arwen

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEffect(EntityPotionEffectEvent event) {
        if (shortening || !(event.getEntity() instanceof Player player)) return;
        PotionEffect effect = event.getNewEffect();
        if (effect == null || effect.isInfinite() || effect.getDuration() < 2 || !LIFTED.contains(effect.getType())) return;
        if (event.getAction() != EntityPotionEffectEvent.Action.ADDED
                && event.getAction() != EntityPotionEffectEvent.Action.CHANGED) return;
        if (!Adornments.wears(player, Adornment.WHITE_GEM_OF_ARWEN)) return;
        event.setCancelled(true);
        shortening = true;
        try {
            player.addPotionEffect(effect.withDuration(effect.getDuration() / 2));
        } finally {
            shortening = false;
        }
    }

    /** What the Shadow a worn ring adds each second is multiplied by. */
    public static double ringShadow(Player player) {
        return Adornments.wears(player, Adornment.WHITE_GEM_OF_ARWEN)
                ? ItemPower.reduction(Adornment.WHITE_GEM_OF_ARWEN.id(), SHADOW_REDUCTION) : 1;
    }

    // ------------------------------------------------------------------ Emeralds of Girion

    /// Villagers work out special prices anew when a trade begins, and the offers are sent after this event.
    /// (Wandering traders don't, so they give no discount.)
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrade(InventoryOpenEvent event) {
        if (!(event.getInventory() instanceof MerchantInventory trade)
                || !(trade.getMerchant() instanceof Villager villager)
                || !(event.getPlayer() instanceof Player player)
                || !Adornments.wears(player, Adornment.EMERALDS_OF_GIRION)) return;
        double discount = 1 - ItemPower.reduction(Adornment.EMERALDS_OF_GIRION.id(), DISCOUNT);
        for (MerchantRecipe recipe : villager.getRecipes()) {
            ItemStack price = recipe.getAdjustedIngredient1();
            if (price == null) continue;
            recipe.setSpecialPrice(recipe.getSpecialPrice() - (int) Math.round(price.getAmount() * discount));
        }
    }

    /// So that the discount isn't saved with the villager.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTradeEnd(InventoryCloseEvent event) {
        if (event.getInventory() instanceof MerchantInventory trade && trade.getMerchant() instanceof Villager villager
                && event.getPlayer() instanceof Player player && Adornments.wears(player, Adornment.EMERALDS_OF_GIRION)) {
            for (MerchantRecipe recipe : villager.getRecipes()) recipe.setSpecialPrice(0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMine(BlockDropItemEvent event) {
        if (!Tag.EMERALD_ORES.isTagged(event.getBlockState().getType())
                || !Adornments.wears(event.getPlayer(), Adornment.EMERALDS_OF_GIRION)) return;
        for (Item drop : event.getItems()) {
            ItemStack stack = drop.getItemStack();
            if (stack.getType() != Material.EMERALD) continue; // Silk Touch drops the ore
            if (ItemPower.roll(Adornment.EMERALDS_OF_GIRION.id(), EMERALD_CHANCE)) {
                stack.add();
                drop.setItemStack(stack);
            }
            return;
        }
    }

    // ------------------------------------------------------------------ Quiver of Legolas

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getProjectile() instanceof AbstractArrow arrow)
                || !Adornments.wears(player, Adornment.QUIVER_OF_LEGOLAS)) return;
        arrow.setVelocity(arrow.getVelocity().multiply(ARROW_SPEED));
        // Arrows that cost nothing (Infinity, Multishot's extra arrows, creative) can't be picked up.
        if (player.getGameMode() == GameMode.CREATIVE
                || arrow.getPickupStatus() != AbstractArrow.PickupStatus.ALLOWED) return;
        if (!ItemPower.roll(Adornment.QUIVER_OF_LEGOLAS.id(), ARROW_CHANCE)) return;
        // Give it back, and leave nothing to pick up.
        ItemStack back = arrow.getItemStack().asOne();
        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) player.give(back);
        });
    }

    // ------------------------------------------------------------------ Sam's Pack

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        FoodProperties food = item.getData(DataComponentTypes.FOOD);
        if (food == null || CustomItems.isCustom(item) || !Adornments.wears(player, Adornment.SAMS_PACK)) return;
        ItemStack back = COOKED.contains(item.getType()) && player.getGameMode() != GameMode.CREATIVE
                && ItemPower.roll(Adornment.SAMS_PACK.id(), FOOD_CHANCE) ? item.asOne() : null;
        // Once it has been eaten.
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline() || player.isDead()) return;
            player.setSaturation(Math.min(player.getFoodLevel(), player.getSaturation() + food.saturation() * EXTRA_SATURATION));
            if (back != null) player.give(back);
        });
    }

    // ------------------------------------------------------------------ Golden Belt of Boromir

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHurt(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || UNSTOPPABLE.contains(event.getCause())) return;
        var max = player.getAttribute(Attribute.MAX_HEALTH);
        if (max == null || player.getHealth() >= max.getValue() / 2
                || !Adornments.wears(player, Adornment.BELT_OF_BOROMIR)) return;
        event.setDamage(event.getDamage() * ItemPower.reduction(Adornment.BELT_OF_BOROMIR.id(), HURT_REDUCTION));
    }

    // ------------------------------------------------------------------ Sheath-belt of Andúril

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onWear(PlayerItemDamageEvent event) {
        if (Tag.ITEMS_SWORDS.isTagged(event.getItem().getType())
                && Adornments.wears(event.getPlayer(), Adornment.SHEATH_BELT_OF_ANDURIL)
                && ItemPower.roll(Adornment.SHEATH_BELT_OF_ANDURIL.id(), NO_WEAR_CHANCE)) event.setCancelled(true);
    }
}
