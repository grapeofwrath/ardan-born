package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// The Scimitar of Isengard: the Uruk-hai's template (the White Hand) combined with a diamond or netherite
/// sword. It finishes the wounded; Uruk-hai in the fury of battle drink strength from each blow.
public final class ScimitarOfIsengard implements ToolUpgrade, Listener {
    public static final String ID = "scimitar_of_isengard";
    private static final double FINISHING_BONUS = 1.25;
    private static final double FURY_HEAL = 1.0; // half a heart

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ItemStack template() {
        return RaceTemplate.WHITE_HAND.create();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return RaceTemplate.WHITE_HAND.is(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.DIAMOND_SWORD, Material.NETHERITE_SWORD);
    }

    @Override
    public ItemStack upgrade(ItemStack base) {
        ItemStack blade = base.clone();
        // No tier in the name, so a later netherite upgrade (which copies our data) needs no renaming.
        blade.setData(DataComponentTypes.ITEM_NAME, Component.text("Scimitar of Isengard", RaceTemplate.WHITE_HAND.color()));
        blade.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        blade.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        blade.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(ToolUpgrade.lore("+25% damage to foes", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("below half health", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Uruk-hai: during Battle-fury,", NamedTextColor.GOLD))
                .addLine(ToolUpgrade.lore("each hit heals half a heart", NamedTextColor.GOLD))
                .build());
        CustomItems.tag(blade, ID);
        return blade;
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!is(player.getInventory().getItemInMainHand())) return;

        var attr = victim.getAttribute(Attribute.MAX_HEALTH);
        double max = attr == null ? victim.getHealth() : attr.getValue();
        if (victim.getHealth() < max / 2) event.setDamage(event.getDamage() * FINISHING_BONUS);

        if (OriginCheck.is(player, OriginCheck.URUK_HAI) && OriginAbilities.Bloodlust.active(player)) {
            player.heal(FURY_HEAL, EntityRegainHealthEvent.RegainReason.CUSTOM);
        }
    }
}
