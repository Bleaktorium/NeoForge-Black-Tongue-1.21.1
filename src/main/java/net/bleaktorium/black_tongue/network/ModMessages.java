package net.bleaktorium.black_tongue.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class ModMessages {
    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}