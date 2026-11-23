package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/// Miruvor, the cordial of Imladris: the Peredhil's template (the Evenstar) combined with a honey bottle.
/// Drinking it fills your hunger, gives Regeneration II, and lifts weariness. The Peredhil feel it twice
/// as long. Each Evenstar makes one draught.
public final class Miruvor implements ToolUpgrade, Listener {
    public static final String ID = "miruvor";
    private static final int REGEN_TICKS = 160;
    private static final List<PotionEffectType> CLEARS = List.of(
            PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS, PotionEffectType.MINING_FATIGUE);

    /// Other features that miruvor eases (the ring's Shadow, the Morgul wound) register here.
    private static final List<Consumer<Player>> ON_DRINK = new ArrayList<>();

    public static void onDrink(Consumer<Player> effect) {
        ON_DRINK.add(effect);
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ItemStack template() {
        return RaceTemplate.EVENSTAR.create();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return RaceTemplate.EVENSTAR.is(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return List.of(Material.HONEY_BOTTLE);
    }

    @Override
    public ItemStack upgrade(ItemStack base) {
        ItemStack draught = base.clone();
        draught.setData(DataComponentTypes.ITEM_NAME, Component.text("Miruvor", RaceTemplate.EVENSTAR.color()));
        draught.setData(DataComponentTypes.RARITY, ItemRarity.RARE);
        draught.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        draught.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(ID).build());
        draught.setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("The cordial of Imladris.", NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Fills your hunger, Regeneration II", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("for 8s, lifts Slowness, Weakness", NamedTextColor.BLUE))
                .addLine(ToolUpgrade.lore("and Mining Fatigue", NamedTextColor.BLUE))
                .addLine(Component.empty())
                .addLine(ToolUpgrade.lore("Peredhil: Regeneration lasts 16s", NamedTextColor.GOLD))
                .build());
        CustomItems.tag(draught, ID);
        return draught;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrinkEvent(PlayerItemConsumeEvent event) {
        if (!is(event.getItem())) return;
        Player player = event.getPlayer();
        boolean peredhil = OriginCheck.is(player, OriginCheck.PEREDHIL);
        player.setFoodLevel(20);
        player.setSaturation(Math.max(player.getSaturation(), 10f));
        CLEARS.forEach(player::removePotionEffect);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                peredhil ? REGEN_TICKS * 2 : REGEN_TICKS, 1, false, true, true));
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1.2, 0), 15, 0.3, 0.4, 0.3, 0.02);
        ON_DRINK.forEach(effect -> effect.accept(player));
    }
}
