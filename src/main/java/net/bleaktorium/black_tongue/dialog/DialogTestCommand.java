package net.bleaktorium.black_tongue.dialog;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

@EventBusSubscriber
public class DialogTestCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("blacktongue")
                .then(Commands.literal("testdialog")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();

                            DialogNode root = new DialogNode(
                                    "Hello, traveler. Ask me something.",
                                    List.of(
                                            new DialogOption("Tell me a secret", p -> new DialogNode(
                                                    "The secret is... there is no secret.",
                                                    List.of(new DialogOption("Back", DialogSessionManager::goBack))
                                            )),
                                            new DialogOption("Leave", p -> null)
                                    )
                            );

                            DialogSessionManager.startSession(player, root, "Test NPC", "witchcraft");
                            DialogHandler.sendNode(player, root);
                            return 1;
                        })));
    }
}