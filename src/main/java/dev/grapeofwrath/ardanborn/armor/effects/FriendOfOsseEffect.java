package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.CustomArmor;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.PufferFish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// Helmet: the creatures of the sea leave you alone unless you strike first. Teleri also swim swiftly.
public final class FriendOfOsseEffect implements SetEffect, Listener {
    private static final Set<EntityType> SEA_FOLK = EnumSet.of(
            EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.PUFFERFISH);
    /// How long the sea stays hostile after you strike one of its creatures.
    private static final long PROVOKED_MS = 30_000;

    private final String setId;
    /// When each wearer last struck a creature of the sea.
    private final Map<UUID, Long> lastStruck = new HashMap<>();

    public FriendOfOsseEffect(String setId) {
        this.setId = setId;
    }

    @Override
    public String name() {
        return "Friend of Ossë";
    }

    @Override
    public List<Component> describe(ArmorTier tier) {
        return List.of(
                Component.text("Drowned, guardians and pufferfish"),
                Component.text("leave you be unless you strike first"),
                Component.text("Teleri: swift swimming"));
    }

    @Override
    public void tick(Player player, ArmorTier tier) {
        if (player.isInWater() && OriginCheck.is(player, OriginCheck.TELERI)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 40, 0, true, false, true));
        }
    }

    private boolean protects(Player player) {
        if (CustomArmor.wornTier(player, EquipmentSlot.HEAD, setId) == null) return false;
        Long struck = lastStruck.get(player.getUniqueId());
        return struck == null || System.currentTimeMillis() - struck > PROVOKED_MS;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStrike(EntityDamageByEntityEvent event) {
        if (!SEA_FOLK.contains(event.getEntityType())) return;
        Entity damager = event.getDamager();
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) damager = shooter;
        if (damager instanceof Player player) lastStruck.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!SEA_FOLK.contains(event.getEntityType())) return;
        if (!(event.getTarget() instanceof Player player)) return;
        EntityTargetEvent.TargetReason reason = event.getReason();
        if (reason == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY
                || reason == EntityTargetEvent.TargetReason.CUSTOM) return;
        if (protects(player)) event.setCancelled(true);
    }

    /// Pufferfish sting whoever comes close, without targeting them first.
    @EventHandler(ignoreCancelled = true)
    public void onSting(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof PufferFish && event.getEntity() instanceof Player player && protects(player)) {
            event.setCancelled(true);
        }
    }

    /// Elder guardians curse every player nearby with Mining Fatigue, again without targeting them.
    @EventHandler(ignoreCancelled = true)
    public void onCurse(EntityPotionEffectEvent event) {
        if (event.getCause() != EntityPotionEffectEvent.Cause.ATTACK) return;
        PotionEffect added = event.getNewEffect();
        if (added == null || added.getType() != PotionEffectType.MINING_FATIGUE) return;
        if (event.getEntity() instanceof Player player && protects(player)) event.setCancelled(true);
    }
}
