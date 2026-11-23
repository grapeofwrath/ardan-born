package dev.grapeofwrath.ardanborn.armor.effects;

import dev.grapeofwrath.ardanborn.armor.PieceEffect;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.List;

/// Boots: sneak in the air for a second leap. Eagles on the wing surge forward instead.
public final class UpdraftEffect extends PieceEffect implements Listener {
    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();

    public UpdraftEffect(String setId) {
        super("updraft", "Updraft", setId, EquipmentSlot.FEET, OriginCheck.GREAT_EAGLE);
    }

    @Override
    protected List<String> lines() {
        return List.of("Sneak in mid-air and you leap again", "Eagles: or gliding, surge forward");
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking() || player.isOnGround() || player.isFlying() || player.isInsideVehicle()) return;
        if (player.isInWater() || player.isInLava() || player.isClimbing() || !worn(player)) return;
        boolean gliding = player.isGliding();
        if (gliding && !own(player)) return;
        if (!cooldown.tryUse(player, LoreWiseEffect.shorten(player, gliding ? 12_000 : 8_000))) return;
        Vector look = player.getLocation().getDirection();
        player.setVelocity(gliding
                ? player.getVelocity().add(look.multiply(1.0))
                : look.setY(0).multiply(0.4).setY(0.7));
        player.setFallDistance(0);
        player.getWorld().spawnParticle(Particle.GUST, player.getLocation(), 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_JUMP, 0.8f, 1.2f);
    }
}
