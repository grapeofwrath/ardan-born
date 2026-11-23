package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.armor.effects.LoreWiseEffect;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginAbilities;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// The Phial of Galadriel: the Noldor's template (Light of the Trees) combined with a glass bottle.
///
/// Right-click to raise it: monsters nearby are revealed, spiders recoil and are burned, and darkness is
/// lifted from you. Held in the off-hand, it lights the way. The light is a fake light block sent only to
/// players' clients, so the server's world and lighting never change and nothing needs cleaning up.
public final class PhialOfGaladriel implements ToolUpgrade, Listener {
    public static final String ID = "phial_of_galadriel";
    private static final long COOLDOWN_MS = 60_000;
    private static final long NOLDOR_COOLDOWN_MS = 40_000;
    private static final double RANGE = 10;
    private static final double NOLDOR_RANGE = 16;
    private static final double SPIDER_DAMAGE = 4.0; // 2 hearts
    /// Players within this distance of a holder see the off-hand light.
    private static final double VIEW_RANGE = 48;
    private static final long LIGHT_PERIOD_TICKS = 4;

    private final OriginAbilities.Cooldown cooldown = new OriginAbilities.Cooldown();
    private final Map<UUID, Glow> glows = new HashMap<>();

    /// Where a holder's light is, and who has been sent it.
    private static final class Glow {
        Location at;
        final Set<UUID> viewers = new HashSet<>();

        Glow(Location at) {
            this.at = at;
        }
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ItemStack template() {
        return RaceTemplate.LIGHT_OF_THE_TREES.create();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return RaceTemplate.LIGHT_OF_THE_TREES.is(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.GLASS_BOTTLE);
    }

    @Override
    public ItemStack upgrade(ItemStack base) {
        ItemStack phial = base.clone();
        phial.setData(DataComponentTypes.ITEM_NAME, Component.text("Phial of Galadriel", RaceTemplate.LIGHT_OF_THE_TREES.color()));
        phial.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        phial.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        phial.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        phial.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        phial.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("The light of a star is caught in it.", NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Right-click: monsters are revealed, spiders", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("driven back, and darkness lifted", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("Off-hand: it lights the way", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Noldor: stronger, and frees allies also", NamedTextColor.GOLD))
                .build());
        CustomItems.tag(phial, ID);
        return phial;
    }

    public void start(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this::updateLights, LIGHT_PERIOD_TICKS, LIGHT_PERIOD_TICKS);
    }

    // ------------------------------------------------------------------ raising the Phial

    /// Right-clicking with the Phial in the main hand raises it. Every right-click with it, in either hand,
    /// is cancelled so the glass bottle never fills with water. From the off-hand it only gives light, so
    /// right-clicking with a tool in the main hand doesn't set it off.
    @EventHandler(priority = EventPriority.LOW)
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!is(event.getItem())) return;
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND || !ItemPower.enabled(ID)) return;
        Player player = event.getPlayer();
        boolean noldor = OriginCheck.is(player, OriginCheck.NOLDOR);
        if (!cooldown.tryUse(player,
                LoreWiseEffect.shorten(player, noldor ? NOLDOR_COOLDOWN_MS : COOLDOWN_MS))) return;
        raise(player, noldor ? NOLDOR_RANGE : RANGE, noldor);
    }

    private static void raise(Player player, double range, boolean noldor) {
        World world = player.getWorld();
        world.spawnParticle(Particle.END_ROD, player.getEyeLocation(), 80, range / 3, 1.5, range / 3, 0.03);
        world.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1.6f);
        lift(player);
        DamageSource light = DamageSource.builder(DamageType.INDIRECT_MAGIC)
                .withCausingEntity(player).withDirectEntity(player).build();
        for (Entity near : player.getNearbyEntities(range, range / 2, range)) {
            if (near instanceof Player ally) {
                if (noldor) lift(ally);
                continue;
            }
            if (!(near instanceof Monster monster)) continue;
            monster.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, true, false, true));
            if (monster instanceof Spider || monster instanceof CaveSpider) {
                monster.damage(ItemPower.amount(ID, SPIDER_DAMAGE), light);
                monster.setTarget(null);
                Vector away = monster.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0);
                if (away.lengthSquared() > 0) monster.setVelocity(away.normalize().multiply(1.4).setY(0.4));
            }
        }
    }

    private static void lift(Player player) {
        player.removePotionEffect(PotionEffectType.DARKNESS);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    // ------------------------------------------------------------------ the off-hand light

    private void updateLights() {
        Set<UUID> holders = new HashSet<>();
        for (Player holder : Bukkit.getOnlinePlayers()) {
            if (!ItemPower.enabled(ID) || !is(holder.getInventory().getItemInOffHand())) continue;
            Location spot = lightSpot(holder);
            if (spot == null) continue;
            holders.add(holder.getUniqueId());
            Glow glow = glows.get(holder.getUniqueId());
            if (glow != null && !sameBlock(glow.at, spot)) {
                clear(glow);
                glow = null;
            }
            if (glow == null) {
                glow = new Glow(spot);
                glows.put(holder.getUniqueId(), glow);
            }
            send(glow);
        }
        // Holders who put the Phial away or can't place a light right now.
        for (UUID id : Set.copyOf(glows.keySet())) {
            if (!holders.contains(id)) clear(glows.remove(id));
        }
    }

    /// The air block at the holder's head, or their feet if the head is in something else.
    private static @Nullable Location lightSpot(Player holder) {
        Block head = holder.getEyeLocation().getBlock();
        if (head.getType().isAir()) return head.getLocation();
        Block feet = holder.getLocation().getBlock();
        if (feet.getType().isAir()) return feet.getLocation();
        return null;
    }

    /// Sends the light to players in range who haven't got it, and restores it for those who left range.
    private static void send(Glow glow) {
        BlockData light = Material.LIGHT.createBlockData(data -> ((Light) data).setLevel(15));
        Set<UUID> inRange = new HashSet<>();
        for (Player viewer : glow.at.getWorld().getPlayers()) {
            if (viewer.getLocation().distanceSquared(glow.at) > VIEW_RANGE * VIEW_RANGE) continue;
            inRange.add(viewer.getUniqueId());
            if (glow.viewers.add(viewer.getUniqueId())) viewer.sendBlockChange(glow.at, light);
        }
        for (UUID id : Set.copyOf(glow.viewers)) {
            if (inRange.contains(id)) continue;
            glow.viewers.remove(id);
            Player gone = Bukkit.getPlayer(id);
            if (gone != null && gone.getWorld().equals(glow.at.getWorld())) {
                gone.sendBlockChange(glow.at, glow.at.getBlock().getBlockData());
            }
        }
    }

    /// Shows everyone who saw the light the real block again.
    private static void clear(@Nullable Glow glow) {
        if (glow == null) return;
        BlockData real = glow.at.getBlock().getBlockData();
        for (UUID id : glow.viewers) {
            Player viewer = Bukkit.getPlayer(id);
            if (viewer != null && viewer.getWorld().equals(glow.at.getWorld())) viewer.sendBlockChange(glow.at, real);
        }
        glow.viewers.clear();
    }

    private static boolean sameBlock(Location a, Location b) {
        return a.getWorld().equals(b.getWorld())
                && a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clear(glows.remove(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        // The old world's light: its viewers are still there, only the holder left.
        clear(glows.remove(event.getPlayer().getUniqueId()));
    }
}
