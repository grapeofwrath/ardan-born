package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.ArdanBorn;
import dev.grapeofwrath.ardanborn.Keys;
import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// The Hunting Bow: the Honey of Beorn (the Beornings' template) combined with a bow. Its arrows fly
/// true, with no drop, for a few seconds. Beornings' arrows from it hit harder at night.
public final class HuntingBow implements ToolUpgrade, Listener {
    public static final String ID = "hunting_bow";
    /// Marks arrows shot from the bow, so their hits are known even after the bow is put away.
    private static final NamespacedKey ARROW_KEY = Keys.key("hunting_arrow");
    /// How long an arrow flies straight before gravity takes it, about 150 blocks at full draw.
    private static final long TRUE_FLIGHT_TICKS = 60;
    private static final double BEORNING_NIGHT_BONUS = 1.20;

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ItemStack template() {
        return HoneyOfBeorn.create();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return HoneyOfBeorn.is(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.BOW);
    }

    @Override
    public ItemStack upgrade(ItemStack base) {
        ItemStack bow = base.clone();
        bow.setData(DataComponentTypes.ITEM_NAME, Component.text("Hunting Bow", HoneyOfBeorn.COLOR));
        bow.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        bow.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        bow.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(ToolUpgrade.lore("Arrows fly true", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Beornings: +20% arrow", NamedTextColor.GOLD))
                .addLine(ToolUpgrade.lore("damage at night", NamedTextColor.GOLD))
                .build());
        // Being tagged also keeps it out of the crafting grid's repair recipe, which would strip it.
        CustomItems.tag(bow, ID);
        return bow;
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!is(event.getBow())) return;
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) return;
        arrow.setGravity(false);
        arrow.getPersistentDataContainer().set(ARROW_KEY, PersistentDataType.BOOLEAN, true);
        // Gravity returns if it's still flying; an arrow stuck in a block stays put either way.
        arrow.getScheduler().runDelayed(JavaPlugin.getPlugin(ArdanBorn.class),
                _ -> arrow.setGravity(true), null, TRUE_FLIGHT_TICKS);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
        if (!arrow.getPersistentDataContainer().has(ARROW_KEY)) return;
        if (!(arrow.getShooter() instanceof Player shooter)) return;
        if (isNight(shooter.getWorld()) && OriginCheck.is(shooter, OriginCheck.BEORNING)) {
            event.setDamage(event.getDamage() * BEORNING_NIGHT_BONUS);
        }
    }

    /// Same night as the Beornings' Moonbound power.
    private static boolean isNight(World world) {
        if (world.getEnvironment() != World.Environment.NORMAL) return false;
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }
}
