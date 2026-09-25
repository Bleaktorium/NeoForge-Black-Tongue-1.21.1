package net.bleaktorium.black_tongue.dialog;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DialogClientHandler {
    public static void handleSync(DialogSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() ->
                Minecraft.getInstance().setScreen(new DialogScreen(packet.text(), packet.speakerName(), packet.themeId(), packet.options()))
        );
    }

    public static void handleClose(DialogClosePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof DialogScreen) {
                Minecraft.getInstance().setScreen(null);
            }
        });
    }
}
