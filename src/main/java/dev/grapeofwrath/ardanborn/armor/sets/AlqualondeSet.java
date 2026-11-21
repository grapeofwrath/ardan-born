package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.ArmorSet;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.armor.effects.FriendOfOsseEffect;
import dev.grapeofwrath.ardanborn.weapons.HavensTrident;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/// The Swanhaven of the Teleri: a sea-elven armor set made to complement the Teleri origin. Made with
/// the Pearl of Ossë, the Teleri's template, which also makes the Spear of the Havens.
public final class AlqualondeSet implements ArmorSet {
    public static final String ID = "alqualonde";
    private static final TextColor COLOR = TextColor.color(0x7FD8E6);

    private final FriendOfOsseEffect friendOfOsse = new FriendOfOsseEffect(ID);

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return "Alqualondë";
    }

    @Override
    public TextColor color() {
        return COLOR;
    }

    @Override
    public ItemStack createTemplate() {
        return HavensTrident.createTemplate();
    }

    @Override
    public boolean isTemplate(ItemStack item) {
        return HavensTrident.isPearl(item);
    }

    @Override
    public void registerRecipes(Plugin plugin) {
        HavensTrident.registerTemplateRecipe();
    }

    /// The helmet is a turtle shell with diamond or netherite stats: vanilla gives water breathing after
    /// surfacing only to `minecraft:turtle_helmet`, and it gives the helmet its sea-shell look.
    @Override
    public Optional<Material> baseMaterial(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD ? Optional.of(Material.TURTLE_HELMET) : Optional.empty();
    }

    @Override
    public Optional<SetEffect> pieceEffect(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> Optional.of(friendOfOsse);
            // Chestplate, leggings, boots and the full-set bonus are still to be designed.
            default -> Optional.empty();
        };
    }
}
