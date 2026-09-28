package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.coven.CovenPlayerData;
import net.bleaktorium.black_tongue.coven.ModAttachments;
import net.bleaktorium.black_tongue.dialog.DialogHandler;
import net.bleaktorium.black_tongue.dialog.DialogNode;
import net.bleaktorium.black_tongue.dialog.DialogSessionManager;
import net.minecraft.server.level.ServerPlayer;

public class CovenDialogOpener {
    public static void open(ServerPlayer player) {
        CovenPlayerData data = player.getData(ModAttachments.COVEN_DATA.get());

        DialogNode root = switch (data.state()) {
            case NEVER_ASKED -> YagaDialogTrees.firstMeeting();
            case TASK_DECLINED -> YagaDialogTrees.followUpB();
            case TASK_ACCEPTED -> YagaDialogTrees.followUpA();
            case POTION_DELIVERED -> YagaDialogTrees.followUpD();
            case GRIMOIRE_RECEIVED -> YagaDialogTrees.followUpC();
        };

        DialogSessionManager.startSession(player, root, "Coven Mother Yaga", "witchcraft");
        DialogHandler.sendNode(player, root);
    }
}