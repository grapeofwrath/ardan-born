package dev.grapeofwrath.ardanborn;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.grapeofwrath.ardanborn.adornments.Adornment;
import dev.grapeofwrath.ardanborn.afflictions.Athelas;
import dev.grapeofwrath.ardanborn.armor.ArmorSets;
import dev.grapeofwrath.ardanborn.items.rings.RingType;
import dev.grapeofwrath.ardanborn.items.rings.Rings;
import dev.grapeofwrath.ardanborn.weapons.ToolUpgrades;
import dev.grapeofwrath.ardanborn.weapons.runes.Runes;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/// Runs before the server finishes starting. Use for commands, registry edits and datapack discovery.
public final class ArdanBornBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        String version = context.getPluginMeta().getVersion();

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LiteralCommandNode<CommandSourceStack> root = Commands.literal("ardanborn")
                    .executes(ctx -> {
                        ctx.getSource().getSender()
                                .sendRichMessage("<gold>ArdanBorn</gold> <gray>v" + version);
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(giveCommand())
                    .then(ringCommand())
                    .build();
            event.registrar().register(root, "ArdanBorn version and admin commands");
        });
    }

    /// `/ardanborn give <targets> <item> [amount]`. Custom items are vanilla items with plugin data,
    /// so vanilla `/give` can't create them.
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> giveCommand() {
        return Commands.literal("give")
                .requires(source -> source.getSender().hasPermission("ardanborn.give"))
                .then(Commands.argument("targets", ArgumentTypes.players())
                        .then(Commands.argument("item", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    String typed = builder.getRemainingLowerCase();
                                    for (String id : giveableItems().keySet()) {
                                        if (id.startsWith(typed)) builder.suggest(id);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> give(ctx, 1))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                                        .executes(ctx -> give(ctx, IntegerArgumentType.getInteger(ctx, "amount"))))));
    }

    /// `/ardanborn ring release <ring>`: sends a unique ring back into the loot pool. Any copy still in
    /// the world crumbles. For a ring lost somewhere it can't be reached, now that `/kill` can't remove one.
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> ringCommand() {
        return Commands.literal("ring")
                .requires(source -> source.getSender().hasPermission("ardanborn.rings"))
                .then(Commands.literal("release")
                        .then(Commands.argument("ring", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (RingType type : RingType.values()) {
                                        if (type.unique() && type.id().startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(type.id());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    var sender = ctx.getSource().getSender();
                                    String id = StringArgumentType.getString(ctx, "ring").toLowerCase(Locale.ROOT);
                                    RingType type = RingType.byId(id);
                                    if (type == null || !Rings.releaseByCommand(type)) {
                                        sender.sendMessage(Component.text("Not a unique ring: " + id, NamedTextColor.RED));
                                        return 0;
                                    }
                                    sender.sendMessage(Component.text(type.displayName()
                                            + " is back in the loot pool. Any existing copy will crumble.", NamedTextColor.GRAY));
                                    return Command.SINGLE_SUCCESS;
                                })));
    }

    /// Every plugin item obtainable by command: race templates, armor pieces, and tools and weapons.
    private static Map<String, Supplier<ItemStack>> giveableItems() {
        Map<String, Supplier<ItemStack>> items = new LinkedHashMap<>(ArmorSets.giveableItems());
        items.putAll(ToolUpgrades.giveableItems());
        items.putAll(Runes.giveableItems());
        items.putAll(Rings.giveableItems());
        items.putAll(Adornment.giveableItems());
        items.put(Athelas.ID, Athelas::create);
        return items;
    }

    private static int give(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        var sender = ctx.getSource().getSender();
        String id = StringArgumentType.getString(ctx, "item").toLowerCase(Locale.ROOT);
        Supplier<ItemStack> factory = giveableItems().get(id);
        if (factory == null) {
            sender.sendMessage(Component.text("Unknown ArdanBorn item: " + id, NamedTextColor.RED));
            return 0;
        }

        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                .resolve(ctx.getSource());
        if (targets.isEmpty()) {
            sender.sendMessage(Component.text("No player was found", NamedTextColor.RED));
            return 0;
        }
        ItemStack sample = factory.get();
        for (Player player : targets) {
            List<ItemStack> items = new ArrayList<>();
            int maxStack = sample.getMaxStackSize();
            for (int left = amount; left > 0; left -= maxStack) {
                ItemStack stack = factory.get();
                stack.setAmount(Math.min(left, maxStack));
                items.add(stack);
            }
            // Drops whatever doesn't fit at the player's feet.
            player.give(items);
        }

        Component name = sample.getData(DataComponentTypes.ITEM_NAME);
        Component itemName = name != null ? name : Component.text(id);
        Component who = targets.size() == 1
                ? Component.text(targets.getFirst().getName())
                : Component.text(targets.size() + " players");
        sender.sendMessage(Component.text("Gave " + amount + " ", NamedTextColor.GRAY)
                .append(itemName).append(Component.text(" to ", NamedTextColor.GRAY)).append(who));
        return targets.size();
    }
}
