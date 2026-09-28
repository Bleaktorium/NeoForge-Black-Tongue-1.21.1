package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID)
public class CovenDebugCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("blacktongue")
                .then(Commands.literal("coven")
                        .executes(ctx -> show(ctx.getSource()))
                        .then(Commands.literal("reset")
                                .requires(src -> src.hasPermission(2)) // operators only: it deletes data
                                .executes(ctx -> reset(ctx.getSource())))));
    }

    private static int show(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Coven coven = CovenSavedData.get(source.getServer()).findContaining(player.getUUID());
        if (coven == null) {
            player.sendSystemMessage(Component.literal("You belong to no coven."));
            return 0;
        }

        List<String> extras = coven.extras().stream().map(CovenMember::displayName).toList();
        player.sendSystemMessage(Component.literal("Coven: " + coven.name()));
        player.sendSystemMessage(Component.literal("Coven Mother: " + coven.mother().displayName()
                + " | Founder: " + coven.founder().displayName()));
        player.sendSystemMessage(Component.literal("Members (" + extras.size() + "/" + Coven.MAX_EXTRA_MEMBERS + "): "
                + (extras.isEmpty() ? "none" : String.join(", ", extras))));
        return 1;
    }

    private static int reset(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean removed = CovenSavedData.get(source.getServer()).delete(player.getUUID());
        player.sendSystemMessage(Component.literal(removed ? "Your coven was dissolved." : "You have no coven of your own to dissolve."));
        return removed ? 1 : 0;
    }
}