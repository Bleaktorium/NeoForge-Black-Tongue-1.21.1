package net.bleaktorium.black_tongue.dialog;

import net.bleaktorium.black_tongue.coven.WitchTradeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenWitchTradeServerHandler {
    public static void handle(OpenWitchTradePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                String name = packet.witchName();
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, inv, p) -> new WitchTradeMenu(containerId, inv, name, null, (ServerPlayer) p),
                        Component.literal(name)
                ), buf -> buf.writeUtf(name));
            }
        });
    }
}