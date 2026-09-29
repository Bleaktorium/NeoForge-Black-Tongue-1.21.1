package net.bleaktorium.black_tongue.farming;

import net.bleaktorium.black_tongue.block.entity.MortarAndPestleBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MortarGrindServerHandler {
    public static void handle(MortarGrindPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.level() instanceof ServerLevel serverLevel) {
                if (serverLevel.getBlockEntity(packet.pos()) instanceof MortarAndPestleBlockEntity mortar) {
                    mortar.receiveGrindSignal(serverLevel);
                }
            }
        });
    }
}