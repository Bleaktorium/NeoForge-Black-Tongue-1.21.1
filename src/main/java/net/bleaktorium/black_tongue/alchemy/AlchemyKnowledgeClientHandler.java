package net.bleaktorium.black_tongue.alchemy;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AlchemyKnowledgeClientHandler {
    public static void handleSync(AlchemyKnowledgeSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientAlchemyKnowledge.update(packet.knownItems()));
    }
}