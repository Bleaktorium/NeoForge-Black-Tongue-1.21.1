package net.bleaktorium.black_tongue.coven;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CovenMenuClientHandler {
    public static void handleSync(CovenMenuSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().setScreen(new CovenScreen(packet)));
    }

    public static void handleViewSync(CovenViewSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            CovenMenus.ViewerRole role = packet.isFounder() ? CovenMenus.ViewerRole.FOUNDER : CovenMenus.ViewerRole.MEMBER;
            Minecraft.getInstance().setScreen(
                    new CovenViewScreen(role, packet.covenName(), packet.mother(), packet.founder(), packet.extras()));
        });
    }
}