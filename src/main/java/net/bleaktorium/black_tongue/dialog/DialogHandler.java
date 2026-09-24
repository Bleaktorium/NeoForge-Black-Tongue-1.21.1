package net.bleaktorium.black_tongue.dialog;

import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class DialogHandler {

    public static void handleChoice(ServerPlayer player, int optionIndex) {
        DialogNode current = DialogSessionManager.getCurrent(player);
        if (current == null) return;

        List<DialogOption> visibleOptions = current.options().stream()
                .filter(opt -> opt.isVisible(player))
                .toList();

        if (optionIndex < 0 || optionIndex >= visibleOptions.size()) return;

        DialogOption chosen = visibleOptions.get(optionIndex);
        DialogNode next = chosen.action().apply(player);

        if (next == null) {
            DialogSessionManager.endSession(player);
            ModMessages.sendToPlayer(player, new DialogClosePacket()); // NEW — this line was missing entirely
        } else {
            if (!chosen.label().equals("Back")) {
                DialogSessionManager.goTo(player, current, next);
            }
            sendNode(player, next);
        }
    }

    public static void sendNode(ServerPlayer player, DialogNode node) {
        List<String> labels = node.options().stream()
                .filter(opt -> opt.isVisible(player))
                .map(DialogOption::label)
                .toList();
        ModMessages.sendToPlayer(player, new DialogSyncPacket(node.text(), labels));
    }
}