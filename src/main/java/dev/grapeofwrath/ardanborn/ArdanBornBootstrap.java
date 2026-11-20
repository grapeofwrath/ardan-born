package dev.grapeofwrath.ardanborn;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

/// Runs before the server finishes starting. Use for commands, registry edits and datapack discovery.
public final class ArdanBornBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        String version = context.getPluginMeta().getVersion();

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
            event.registrar().register(
                Commands.literal("ardanborn")
                    .executes(ctx -> {
                        ctx.getSource().getSender()
                            .sendRichMessage("<gold>ArdanBorn</gold> <gray>v" + version);
                        return Command.SINGLE_SUCCESS;
                    })
                    .build(),
                "Shows the ArdanBorn version"
            )
        );
    }
}
