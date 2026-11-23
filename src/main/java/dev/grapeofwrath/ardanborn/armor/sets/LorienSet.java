package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.ArmorSet;
import dev.grapeofwrath.ardanborn.armor.ArmorTier;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.armor.effects.BoughRunnerEffect;
import dev.grapeofwrath.ardanborn.armor.effects.ElusiveEffect;
import dev.grapeofwrath.ardanborn.armor.effects.FarSightedEffect;
import dev.grapeofwrath.ardanborn.armor.effects.GaladhrimVolleyEffect;
import dev.grapeofwrath.ardanborn.armor.effects.LeafLightEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/// The Galadhrim of Lothlórien: an armor set made to complement the Sindar origin. Made with the Mallorn
/// Leaf, the Sindar's template. The chestplate is the Cloak of Lórien.
public final class LorienSet implements ArmorSet {
    public static final String ID = "lorien";
    private static final TextColor COLOR = TextColor.color(0x9DB58A);

    private final ElusiveEffect elusive = new ElusiveEffect(ID);
    private final FarSightedEffect farSighted = new FarSightedEffect(ID);
    private final LeafLightEffect leafLight = new LeafLightEffect(ID);
    private final BoughRunnerEffect boughRunner = new BoughRunnerEffect(ID);
    private final GaladhrimVolleyEffect galadhrimVolley = new GaladhrimVolleyEffect(ID);

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Lórien";
    }

    @Override
    public TextColor color() {
        return COLOR;
    }

    @Override
    public ItemStack createTemplate() {
        return RaceTemplate.MALLORN_LEAF.create();
    }

    @Override
    public boolean isTemplate(ItemStack item) {
        return RaceTemplate.MALLORN_LEAF.is(item);
    }

    @Override
    public void registerRecipes(Plugin plugin) {
        RaceTemplate.MALLORN_LEAF.registerRecipe();
    }

    /// The chestplate is a leather tunic with diamond or netherite stats, dyed the grey-green of the
    /// elven cloaks, so it looks like a cloak rather than plate.
    @Override
    public Optional<Material> baseMaterial(EquipmentSlot slot) {
        return slot == EquipmentSlot.CHEST ? Optional.of(Material.LEATHER_CHESTPLATE) : Optional.empty();
    }

    @Override
    public Optional<Color> dyeColor(ArmorTier tier) {
        return Optional.of(tier == ArmorTier.NETHERITE ? Color.fromRGB(0x5E6B55) : Color.fromRGB(0x8A9A7B));
    }

    @Override
    public Optional<SetEffect> pieceEffect(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Optional.of(farSighted);
            case CHEST -> Optional.of(elusive);
            case LEGS -> Optional.of(leafLight);
            case FEET -> Optional.of(boughRunner);
            default -> Optional.empty();
        };
    }

    @Override
    public Optional<SetEffect> fullSetBonus() {
        return Optional.of(galadhrimVolley);
    }
}
