package dev.grapeofwrath.ardanborn.armor.sets;

import dev.grapeofwrath.ardanborn.armor.ArmorSet;
import dev.grapeofwrath.ardanborn.armor.SetEffect;
import dev.grapeofwrath.ardanborn.weapons.RaceTemplate;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;

/// An armor set made with a [RaceTemplate], with an effect on every piece and a full-set bonus.
public abstract class RaceSet implements ArmorSet {
    private final String id;
    private final String name;
    private final TextColor color;
    private final RaceTemplate template;
    private final Map<EquipmentSlot, SetEffect> pieces;
    private final SetEffect bonus;

    protected RaceSet(String id, String name, TextColor color, RaceTemplate template, SetEffect head, SetEffect chest,
                      SetEffect legs, SetEffect feet, SetEffect bonus) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.template = template;
        this.pieces = Map.of(EquipmentSlot.HEAD, head, EquipmentSlot.CHEST, chest,
                EquipmentSlot.LEGS, legs, EquipmentSlot.FEET, feet);
        this.bonus = bonus;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String displayName() {
        return name;
    }

    @Override
    public TextColor color() {
        return color;
    }

    @Override
    public ItemStack createTemplate() {
        return template.create();
    }

    @Override
    public boolean isTemplate(ItemStack item) {
        return template.is(item);
    }

    @Override
    public void registerRecipes(Plugin plugin) {
        template.registerRecipe();
    }

    @Override
    public Optional<SetEffect> pieceEffect(EquipmentSlot slot) {
        return Optional.ofNullable(pieces.get(slot));
    }

    @Override
    public Optional<SetEffect> fullSetBonus() {
        return Optional.of(bonus);
    }
}
