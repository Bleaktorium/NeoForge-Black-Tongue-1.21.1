package net.bleaktorium.black_tongue.coven;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CovenMenuServerHandler {
    public static void handleAccept(CovenMenuAcceptPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CovenMenus.handleAccept(player, packet.covenName(), packet.extras());
            }
        });
    }

    public static void handleRename(CovenRenamePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CovenMenus.handleRename(player, packet.name());
            }
        });
    }

    public static void handleKick(CovenKickPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CovenMember.Kind kind = packet.isPlayer() ? CovenMember.Kind.PLAYER : CovenMember.Kind.WITCH;
                CovenMenus.handleKick(player, kind, packet.key());
            }
        });
    }

    public static void handleLeaveOrAbandon(CovenLeaveOrAbandonPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CovenSavedData data = CovenSavedData.get(player.server);
                Coven coven = data.findContaining(player.getUUID());
                if (coven == null) return;
                if (coven.founderId().equals(player.getUUID())) {
                    CovenMenus.handleAbandon(player);
                } else {
                    CovenMenus.handleLeave(player);
                }
            }
        });
    }
}