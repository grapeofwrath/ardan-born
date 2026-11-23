package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// Aeglos, "snow-point": the Feather of Thorondor (the Eagles' template, which also makes the Thorondor
/// armor) combined with a diamond or netherite spear. It strikes glowing foes harder; Eagles striking from
/// the air mark their foe so it glows.
public final class Aeglos implements ToolUpgrade, Listener {
    public static final String ID = "aeglos";
    private static final double GLOWING_BONUS = 1.25;
    private static final int MARK_TICKS = 100;

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ItemStack template() {
        return FeatherOfThorondor.create();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return FeatherOfThorondor.is(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.DIAMOND_SPEAR, Material.NETHERITE_SPEAR);
    }

    @Override
    public ItemStack upgrade(ItemStack spear) {
        ItemStack aeglos = spear.clone();
        // No tier in the name, so a later netherite upgrade (which copies our data) needs no renaming.
        aeglos.setData(DataComponentTypes.ITEM_NAME, Component.text("Aeglos", FeatherOfThorondor.COLOR));
        aeglos.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        aeglos.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        aeglos.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(ToolUpgrade.lore("+25% damage to glowing foes", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Eagles: hits while gliding", NamedTextColor.GOLD))
                .addLine(ToolUpgrade.lore("make the foe glow", NamedTextColor.GOLD))
                .build());
        CustomItems.tag(aeglos, ID);
        return aeglos;
    }

    /// Any hit with Aeglos in hand: a swing, or a charge with the spear.
    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!is(player.getInventory().getItemInMainHand())) return;

        if (victim.isGlowing() || victim.hasPotionEffect(PotionEffectType.GLOWING)) {
            event.setDamage(event.getDamage() * GLOWING_BONUS);
        }
        if (player.isGliding() && OriginCheck.is(player, OriginCheck.GREAT_EAGLE)) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, MARK_TICKS, 0, true, false, false));
        }
    }
}
