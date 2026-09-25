package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

public class CovenSync {

    public static void syncQuestToClient(ServerPlayer player, CovenPlayerData data) {
        boolean active = data.state() == CovenRelationshipState.TASK_ACCEPTED;
        ModMessages.sendToPlayer(player, new CovenQuestSyncPacket(active, active ? data.assignedPotions() : List.of()));
    }
}