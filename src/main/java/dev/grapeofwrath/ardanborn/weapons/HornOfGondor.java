package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// The Horn of Gondor: the Edain's template (Star of Elendil) combined with a goat horn. Blown, it lifts
/// every player within 48 blocks with Strength and Resistance, and rallies nearby iron golems to the
/// horn-blower's side for 30 seconds. Edain who blow it also find their hope renewed: Last Stand is ready
/// again.
public final class HornOfGondor implements ToolUpgrade, Listener {
    public static final String ID = "horn_of_gondor";
    private static final long COOLDOWN_MS = 300_000;
    private static final double RANGE = 48;
    private static final int BUFF_TICKS = 300;
    private static final int RALLY_SECONDS = 30;
    private static final double GOLEM_HUNT_RANGE = 16;

    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();
    private Plugin plugin;

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ItemStack template() {
        return RaceTemplate.STAR_OF_ELENDIL.create();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return RaceTemplate.STAR_OF_ELENDIL.is(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.GOAT_HORN);
    }

    @Override
    public ItemStack upgrade(ItemStack base) {
        ItemStack horn = base.clone();
        horn.setData(DataComponentTypes.ITEM_NAME, Component.text("Horn of Gondor", RaceTemplate.STAR_OF_ELENDIL.color()));
        horn.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        horn.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        horn.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("Blown in need, it is heard far off.", NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Players within 48 blocks: Strength I", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("and Resistance I for 15s", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("Iron golems rally to you for 30s", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("5 minute cooldown", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Edain: renews Hope Unquenched", NamedTextColor.GOLD))
                .build());
        CustomItems.tag(horn, ID);
        return horn;
    }

    public void start(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlow(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!is(event.getItem())) return;
        Player player = event.getPlayer();
        // Vanilla plays the horn's sound; while on cooldown, it's just a horn.
        if (!cooldown.tryUse(player, COOLDOWN_MS)) return;

        List<Player> allies = new ArrayList<>();
        allies.add(player);
        List<IronGolem> golems = new ArrayList<>();
        for (Entity near : player.getNearbyEntities(RANGE, RANGE / 2, RANGE)) {
            if (near instanceof Player ally) allies.add(ally);
            else if (near instanceof IronGolem golem) golems.add(golem);
        }
        for (Player ally : allies) {
            ally.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, BUFF_TICKS, 0, true, true, true));
            ally.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, BUFF_TICKS, 0, true, true, true));
            ally.sendActionBar(Component.text("The Horn of Gondor sounds!", RaceTemplate.STAR_OF_ELENDIL.color()));
        }
        player.getWorld().spawnParticle(Particle.NOTE, player.getEyeLocation(), 12, 1, 0.5, 1, 1);
        if (OriginCheck.is(player, OriginCheck.EDAIN)) OriginAbilities.HopeUnquenched.renew(player);
        if (!golems.isEmpty() && plugin != null) rally(player, golems);
    }

    /// For 30 seconds, each second: golems walk to the horn-blower and attack the nearest monster near them.
    private void rally(Player player, List<IronGolem> golems) {
        new BukkitRunnable() {
            int left = RALLY_SECONDS;

            @Override
            public void run() {
                if (left-- <= 0 || !player.isOnline()) {
                    cancel();
                    return;
                }
                Monster nearest = nearestMonster(player);
                for (IronGolem golem : golems) {
                    if (!golem.isValid() || !golem.getWorld().equals(player.getWorld())) continue;
                    if (nearest != null) {
                        golem.setTarget(nearest);
                    } else if (golem.getLocation().distanceSquared(player.getLocation()) > 36) {
                        golem.getPathfinder().moveTo(player, 1.0);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private static @Nullable Monster nearestMonster(Player player) {
        Monster nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity near : player.getNearbyEntities(GOLEM_HUNT_RANGE, GOLEM_HUNT_RANGE / 2, GOLEM_HUNT_RANGE)) {
            if (!(near instanceof Monster monster) || monster.isDead()) continue;
            double d = monster.getLocation().distanceSquared(player.getLocation());
            if (d < best) {
                best = d;
                nearest = monster;
            }
        }
        return nearest;
    }
}
