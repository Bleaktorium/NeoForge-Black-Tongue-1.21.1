package net.bleaktorium.black_tongue.coven;

import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class WitchReputationHelper {
    public static int getReputation(ServerPlayer player, String witchName) {
        return player.getData(ModAttachments.WITCH_REPUTATION.get()).getReputation(witchName);
    }

    public static void adjustReputation(ServerPlayer player, String witchName, int delta) {
        WitchReputationData current = player.getData(ModAttachments.WITCH_REPUTATION.get());
        player.setData(ModAttachments.WITCH_REPUTATION.get(), current.withAdjustedReputation(witchName, delta));
    }
}