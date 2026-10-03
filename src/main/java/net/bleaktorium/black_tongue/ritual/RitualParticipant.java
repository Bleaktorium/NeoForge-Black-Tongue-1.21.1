package net.bleaktorium.black_tongue.ritual;

import net.bleaktorium.black_tongue.entity.custom.WitchIdentity;
import net.minecraft.server.level.ServerPlayer;

public record RitualParticipant(Kind kind, String key, String displayName, boolean covenMotherTier) {

    public enum Kind { PLAYER, WITCH }

    public static RitualParticipant player(ServerPlayer p) {
        return new RitualParticipant(Kind.PLAYER, p.getUUID().toString(), p.getName().getString(), false);
    }

    public static RitualParticipant witch(WitchIdentity w, String key) {
        return new RitualParticipant(Kind.WITCH, key, w.name(), w.covenMotherTier());
    }
}