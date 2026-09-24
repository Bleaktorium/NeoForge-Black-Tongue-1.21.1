package net.bleaktorium.black_tongue.dialog;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DialogServerHandler {
    public static void handleChoice(DialogChoicePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                DialogHandler.handleChoice(serverPlayer, packet.optionIndex());
            }
        });
    }
}