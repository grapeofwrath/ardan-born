package dev.grapeofwrath.ardanborn.weapons;

import dev.grapeofwrath.ardanborn.armor.CustomItems;
import dev.grapeofwrath.ardanborn.armor.sets.ArkenstoneSet;
import dev.grapeofwrath.ardanborn.items.ItemPower;
import dev.grapeofwrath.ardanborn.origins.OriginCheck;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/// Base of the race weapons and artifacts: the naming, lore and tagging they all share.
public abstract class RaceItem implements ToolUpgrade {
    /// A race's template, whichever class it lives in.
    protected record Template(Supplier<ItemStack> create, Predicate<ItemStack> is, TextColor color) {
        static final Template ARKENSTONE = new Template(ArkenstoneSet::template,
                item -> ArkenstoneSet.ID.equals(CustomItems.idOf(item)), TextColor.color(0xD8ECFF));
        static final Template PEARL = new Template(SpearOfTheHavens::createTemplate, SpearOfTheHavens::isPearl,
                TextColor.color(0x7FD8E6));
        static final Template FEATHER = new Template(FeatherOfThorondor::create, FeatherOfThorondor::is,
                FeatherOfThorondor.COLOR);
        static final Template HONEY = new Template(HoneyOfBeorn::create, HoneyOfBeorn::is, HoneyOfBeorn.COLOR);

        static Template of(RaceTemplate template) {
            return new Template(template::create, template::is, template.color());
        }
    }

    protected static final List<Material> SWORDS = List.of(Material.DIAMOND_SWORD, Material.NETHERITE_SWORD);

    protected final String id;
    private final String name;
    private final Template template;
    private final String people;
    private final List<Material> bases;

    /// `people` is an origin file name (see [OriginCheck]).
    protected RaceItem(String id, String name, Template template, String people, List<Material> bases) {
        this.id = id;
        this.name = name;
        this.template = template;
        this.people = people;
        this.bases = bases;
    }

    /// A short line of lore, shown first. Items that can be enchanted have none, to keep their tooltips brief.
    protected List<String> flavor() {
        return List.of();
    }

    /// Lore for anyone.
    protected abstract List<String> lines();

    /// Lore for the item's own people.
    protected abstract List<String> ownLines();

    protected boolean own(Player player) {
        return OriginCheck.is(player, people);
    }

    /// Holding it in the main hand, and it isn't disabled in the config.
    protected boolean wields(Player player) {
        return is(player.getInventory().getItemInMainHand()) && ItemPower.enabled(id);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ItemStack template() {
        return template.create().get();
    }

    @Override
    public boolean isTemplate(@Nullable ItemStack item) {
        return template.is().test(item);
    }

    @Override
    public List<Material> baseMaterials() {
        return bases;
    }

    @Override
    public ItemStack upgrade(ItemStack base) {
        ItemStack item = base.clone();
        // No tier in the name, so a later netherite upgrade (which copies our data) needs no renaming.
        item.setData(DataComponentTypes.ITEM_NAME, Component.text(name, template.color()));
        item.setData(DataComponentTypes.RARITY, ItemRarity.EPIC);
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(id).build());
        ItemLore.Builder lore = ItemLore.lore();
        boolean brief = flavor().isEmpty();
        for (String line : flavor()) {
            lore.addLine(Component.text(line, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true));
        }
        if (!brief) lore.addLine(Component.empty());
        for (String line : lines()) lore.addLine(ToolUpgrade.lore(line, NamedTextColor.BLUE));
        if (!brief) lore.addLine(Component.empty());
        for (String line : ownLines()) lore.addLine(ToolUpgrade.lore(line, NamedTextColor.GOLD));
        item.setData(DataComponentTypes.LORE, lore.build());
        // Being tagged also keeps it out of crafting recipes, which would strip or use it up.
        CustomItems.tag(item, id);
        return item;
    }
}
