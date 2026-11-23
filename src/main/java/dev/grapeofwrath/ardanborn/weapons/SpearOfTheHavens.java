package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.ArmorSets;
import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// The Spear of the Havens: the Pearl of Ossë (the Teleri's template, which also makes the Alqualondë
/// armor) combined with a trident. It always returns when thrown, and with Channeling calls lightning on
/// foes in water or rain. Teleri deal extra damage with it.
public final class SpearOfTheHavens implements ToolUpgrade, Listener {
    public static final String TEMPLATE_ID = "pearl_of_osse";
    public static final String SPEAR_ID = "spear_of_the_havens";
    /// Extra damage for Teleri wielders.
    private static final double TELERI_BONUS = 1.15;
    private static final TextColor COLOR = TextColor.color(0x7FD8E6);

    @Override
    public String id() {
        return SPEAR_ID;
    }

    @Override
    public ItemStack template() {
        return createTemplate();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return SpearOfTheHavens.isPearl(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.TRIDENT);
    }

    /// The Pearl of Ossë.
    public static ItemStack createTemplate() {
        // Looks like a heart of the sea, but is built on prismarine crystals so it can't be used in a
        // conduit recipe. RecipeGuardListener keeps it out of prismarine recipes.
        ItemStack item = ItemStack.of(Material.PRISMARINE_CRYSTALS);
        item.setData(DataComponentTypes.ITEM_MODEL, Material.HEART_OF_THE_SEA.getKey());
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Pearl of Ossë", COLOR));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(TEMPLATE_ID).build());
        item.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("A pearl of the shores of Alqualondë,", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.text("gift of Ossë.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Combine with diamond or netherite", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("armor or a trident at a Smithing Table.", NamedTextColor.BLUE))
                .build());
        CustomItems.tag(item, TEMPLATE_ID);
        return item;
    }

    public static boolean isPearl(@Nullable ItemStack item) {
        return TEMPLATE_ID.equals(CustomItems.idOf(item));
    }

    public static void registerTemplateRecipe() {
        NamespacedKey key = Keys.key(TEMPLATE_ID);
        ShapedRecipe recipe = new ShapedRecipe(key, createTemplate());
        recipe.shape("PNP", "GDG", "PNP");
        recipe.setIngredient('P', Material.PRISMARINE_SHARD);
        recipe.setIngredient('N', Material.NAUTILUS_SHELL);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('D', Material.DIAMOND);
        Bukkit.addRecipe(recipe);
        ArmorSets.trackRecipe(key);
    }

    @Override
    public ItemStack upgrade(ItemStack trident) {
        ItemStack spear = trident.clone();
        spear.setData(DataComponentTypes.ITEM_NAME, Component.text("Spear of the Havens", COLOR));
        spear.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        spear.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(SPEAR_ID).build());
        spear.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(ToolUpgrade.lore("Always returns when thrown", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("Channeling calls lightning on", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("foes in water or rain", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Teleri: +15% damage", NamedTextColor.DARK_AQUA))
                .build());
        // Being tagged also keeps it out of the crafting grid's repair recipe, which would strip it.
        CustomItems.tag(spear, SPEAR_ID);
        return spear;
    }

    /// Always returns: a thrown spear without Loyalty flies back as if it had Loyalty I.
    @EventHandler(ignoreCancelled = true)
    public void onThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!is(trident.getItemStack())) return;
        if (trident.getLoyaltyLevel() == 0) trident.setLoyaltyLevel(1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        Player wielder;
        ItemStack spear;
        if (event.getDamager() instanceof Trident trident && trident.getShooter() instanceof Player thrower) {
            wielder = thrower;
            spear = trident.getItemStack();
            if (is(spear)) callLightning(trident, thrower, event);
        } else if (event.getDamager() instanceof Player player
                && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            wielder = player;
            spear = player.getInventory().getItemInMainHand();
        } else {
            return;
        }
        if (is(spear) && OriginCheck.is(wielder, OriginCheck.TELERI)) {
            event.setDamage(event.getDamage() * TELERI_BONUS);
        }
    }

    /// Channeling outside a thunderstorm, for foes in water or rain. In a storm vanilla Channeling applies.
    private static void callLightning(Trident trident, Player thrower, EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!trident.getItemStack().containsEnchantment(Enchantment.CHANNELING)) return;
        if (victim.getWorld().isThundering() || !victim.isInWaterOrRain()) return;
        LightningStrike strike = victim.getWorld().strikeLightning(victim.getLocation());
        strike.setCausingPlayer(thrower);
    }
}
