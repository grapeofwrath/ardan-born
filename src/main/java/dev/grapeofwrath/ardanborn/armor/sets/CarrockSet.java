package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.ArmorSet;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.armor.effects.BeeswaxEffect;
import dev.grapeofwrath.ardanborn.armor.effects.GreatBoundsEffect;
import dev.grapeofwrath.ardanborn.armor.effects.HoneyFedEffect;
import dev.grapeofwrath.ardanborn.armor.effects.MaulEffect;
import dev.grapeofwrath.ardanborn.armor.effects.WildWaysEffect;
import dev.grapeofwrath.ardanborn.weapons.HoneyOfBeorn;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/// Beorn's land: the Beornings' armor set, made with the Honey of Beorn.
public final class CarrockSet implements ArmorSet {
    public static final String ID = "carrock";

    private final HoneyFedEffect honeyFed = new HoneyFedEffect(ID);
    private final BeeswaxEffect beeswax = new BeeswaxEffect(ID);
    private final GreatBoundsEffect greatBounds = new GreatBoundsEffect(ID);
    private final WildWaysEffect wildWays = new WildWaysEffect(ID);
    private final MaulEffect maul = new MaulEffect(ID);

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Carrock";
    }

    @Override
    public TextColor color() {
        return HoneyOfBeorn.COLOR;
    }

    @Override
    public ItemStack createTemplate() {
        return HoneyOfBeorn.create();
    }

    @Override
    public boolean isTemplate(ItemStack item) {
        return HoneyOfBeorn.is(item);
    }

    @Override
    public void registerRecipes(Plugin plugin) {
        HoneyOfBeorn.registerRecipe();
    }

    @Override
    public Optional<SetEffect> pieceEffect(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Optional.of(honeyFed);
            case CHEST -> Optional.of(beeswax);
            case LEGS -> Optional.of(greatBounds);
            case FEET -> Optional.of(wildWays);
            default -> Optional.empty();
        };
    }

    @Override
    public Optional<SetEffect> fullSetBonus() {
        return Optional.of(maul);
    }
}
