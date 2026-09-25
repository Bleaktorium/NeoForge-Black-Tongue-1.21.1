package net.bleaktorium.black_tongue.coven;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CovenQuestClientHandler {
    public static void handleSync(CovenQuestSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientCovenQuestState.update(packet.active(), packet.assignedPotions()));
    }
}