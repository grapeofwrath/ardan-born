package dev.grapeofwrath.ardanborn.armor;

import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// Base of the armor effects: which set and slot it belongs to, and whose people get its extra.
public abstract class PieceEffect implements SetEffect {
    private static final List<EquipmentSlot> SLOTS =
            List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    protected final String id;
    private final String name;
    private final String setId;
    private final @Nullable EquipmentSlot slot;
    private final String people;

    /// `slot` is null for a full-set bonus; `people` is an origin file name (see [OriginCheck]).
    protected PieceEffect(String id, String name, String setId, @Nullable EquipmentSlot slot, String people) {
        this.id = id;
        this.name = name;
        this.setId = setId;
        this.slot = slot;
        this.people = people;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    protected abstract List<String> lines();

    @Override
    public List<Component> describe(ArmorTier tier) {
        return lines().stream().<Component>map(Component::text).toList();
    }

    protected boolean worn(Player player) {
        if (!ItemPower.enabled(id)) return false;
        if (slot != null) return CustomArmor.wornTier(player, slot, setId) != null;
        for (EquipmentSlot each : SLOTS) {
            if (CustomArmor.wornTier(player, each, setId) == null) return false;
        }
        return true;
    }

    protected boolean own(Player player) {
        return OriginCheck.is(player, people);
    }

    /// True once every `ticks` game ticks, for use in [#tick].
    protected static boolean every(int ticks) {
        return Bukkit.getCurrentTick() % ticks < ArmorEffectManager.TICK_PERIOD;
    }
}
