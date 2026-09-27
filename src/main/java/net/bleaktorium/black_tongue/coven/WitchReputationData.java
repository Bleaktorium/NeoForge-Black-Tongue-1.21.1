package net.bleaktorium.black_tongue.coven;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import java.util.Map;
import java.util.UUID;

public record WitchReputationData(Map<String, Integer> reputationByWitch) {
    public static final Codec<WitchReputationData> CODEC = Codec.unboundedMap(Codec.STRING, Codec.INT)
            .xmap(WitchReputationData::new, WitchReputationData::reputationByWitch);

    public static WitchReputationData initial() { return new WitchReputationData(Map.of()); }

    public int getReputation(String witchName) {
        return reputationByWitch.getOrDefault(witchName, 0);
    }

    public WitchReputationData withAdjustedReputation(String witchName, int delta) {
        Map<String, Integer> updated = new java.util.HashMap<>(reputationByWitch);
        updated.merge(witchName, delta, Integer::sum);
        return new WitchReputationData(updated);
    }
}