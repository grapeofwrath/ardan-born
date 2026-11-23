package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.ArmorSet;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.armor.effects.HawkSightEffect;
import dev.grapeofwrath.ardanborn.armor.effects.StormFeathersEffect;
import dev.grapeofwrath.ardanborn.armor.effects.TalonGripEffect;
import dev.grapeofwrath.ardanborn.armor.effects.UpdraftEffect;
import dev.grapeofwrath.ardanborn.armor.effects.WindlordEffect;
import dev.grapeofwrath.ardanborn.weapons.FeatherOfThorondor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/// The King of Eagles: an armor set made to complement the Great Eagle origin. Made with the Feather of
/// Thorondor, the Eagles' template, which also makes Aeglos.
public final class ThorondorSet implements ArmorSet {
    public static final String ID = "thorondor";

    private final HawkSightEffect hawkSight = new HawkSightEffect();
    private final StormFeathersEffect stormFeathers = new StormFeathersEffect(ID);
    private final TalonGripEffect talonGrip = new TalonGripEffect(ID);
    private final UpdraftEffect updraft = new UpdraftEffect(ID);
    private final WindlordEffect windlord = new WindlordEffect(ID);

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Thorondor";
    }

    @Override
    public TextColor color() {
        return FeatherOfThorondor.COLOR;
    }

    @Override
    public ItemStack createTemplate() {
        return FeatherOfThorondor.create();
    }

    @Override
    public boolean isTemplate(ItemStack item) {
        return FeatherOfThorondor.is(item);
    }

    @Override
    public void registerRecipes(Plugin plugin) {
        FeatherOfThorondor.registerRecipe();
    }

    /// The helmet is a spyglass worn as a helmet: the client only zooms while a `minecraft:spyglass` is
    /// being used, so this is the only way a helmet can zoom like one. It is worn and shown like the
    /// diamond or netherite helmet (see [dev.grapeofwrath.ardanborn.armor.CustomArmor#applyTierStats]).
    @Override
    public Optional<Material> baseMaterial(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD ? Optional.of(Material.SPYGLASS) : Optional.empty();
    }

    @Override
    public Optional<SetEffect> pieceEffect(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Optional.of(hawkSight);
            case CHEST -> Optional.of(stormFeathers);
            case LEGS -> Optional.of(talonGrip);
            case FEET -> Optional.of(updraft);
            default -> Optional.empty();
        };
    }

    @Override
    public Optional<SetEffect> fullSetBonus() {
        return Optional.of(windlord);
    }
}
