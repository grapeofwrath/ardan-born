package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.ArmorSet;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.armor.effects.FellowshipEffect;
import dev.grapeofwrath.ardanborn.armor.effects.MerryHeartEffect;
import dev.grapeofwrath.ardanborn.armor.effects.MithrilGuardEffect;
import dev.grapeofwrath.ardanborn.armor.effects.StonesThrowEffect;
import dev.grapeofwrath.ardanborn.armor.effects.ThereAndBackEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/// Mithril, the Hobbits' armor set, made with the Mathom template. The chestplate is the
/// Mithril Coat: Bilbo's gift from Thorin, later Frodo's.
public final class MithrilSet implements ArmorSet {
    public static final String ID = "mithril";
    private static final TextColor COLOR = TextColor.color(0xD7E3EC);

    private final MithrilGuardEffect guard = new MithrilGuardEffect(ID);
    private final MerryHeartEffect merryHeart = new MerryHeartEffect(ID);
    private final StonesThrowEffect stonesThrow = new StonesThrowEffect(ID);
    private final ThereAndBackEffect thereAndBack = new ThereAndBackEffect(ID);
    private final FellowshipEffect fellowship = new FellowshipEffect(ID);

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Mithril";
    }

    @Override
    public TextColor color() {
        return COLOR;
    }

    @Override
    public ItemStack createTemplate() {
        return RaceTemplate.MATHOM.create();
    }

    @Override
    public boolean isTemplate(ItemStack item) {
        return RaceTemplate.MATHOM.is(item);
    }

    @Override
    public void registerRecipes(Plugin plugin) {
        RaceTemplate.MATHOM.registerRecipe();
    }

    /// The coat is chain mail with diamond or netherite stats: light and silver, as mithril should look.
    @Override
    public Optional<Material> baseMaterial(EquipmentSlot slot) {
        return slot == EquipmentSlot.CHEST ? Optional.of(Material.CHAINMAIL_CHESTPLATE) : Optional.empty();
    }

    @Override
    public Optional<SetEffect> pieceEffect(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Optional.of(merryHeart);
            case CHEST -> Optional.of(guard);
            case LEGS -> Optional.of(stonesThrow);
            case FEET -> Optional.of(thereAndBack);
            default -> Optional.empty();
        };
    }

    @Override
    public Optional<SetEffect> fullSetBonus() {
        return Optional.of(fellowship);
    }
}
