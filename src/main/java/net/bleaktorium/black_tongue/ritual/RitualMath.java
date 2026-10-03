package net.bleaktorium.black_tongue.ritual;

import net.minecraft.util.RandomSource;

public class RitualMath {

    // Tunable balance numbers

    public static final int PLAYER_BASE_AMPLIFICATION = 30; // a solo player brings this much "power" on their own
    public static final int PLAYER_BASE_STABILITY = 70;      // and this much "control" over that power

    public static final int NESTING_FILLED_AMPLIFICATION = 10; // a Nesting rune holding an item adds this much power
    public static final int NESTING_STABILITY = 80;             // and counts as a "medium control" contributor

    public static final int POTENCY_AMPLIFICATION = 15; // Potency runes add more raw power...
    public static final int POTENCY_STABILITY = 0;       // ...but contribute nothing to stability (drags the average down)

    public static final int PLAYER_PARTICIPANT_AMPLIFICATION = 25;
    public static final int PLAYER_PARTICIPANT_STABILITY = 75;

    public static final int ANCESTOR_AMPLIFICATION = 20;
    public static final int ANCESTOR_STABILITY = 90;
    public static final int COVEN_REMAINS_AMPLIFICATION = 10;
    public static final int COVEN_REMAINS_STABILITY = 80;



    public enum RitualOutcome {
        CRITICAL_FAILURE,
        FAILURE_WITH_SIDE_EFFECT,
        PARTIAL_SUCCESS_WITH_SIDE_EFFECT,
        SUCCESS,
        CRITICAL_SUCCESS
    }

    public static RitualOutcome rollOutcome(double stability, RandomSource random) {
        RitualOutcome[] tiers = RitualOutcome.values();
        int center = (int) (stability / 100.0 * (tiers.length - 1));
        center = Math.max(0, Math.min(tiers.length - 1, center));

        int variance = random.nextInt(3) - 1; // -1, 0 or +1 tier
        int index = Math.max(0, Math.min(tiers.length - 1, center + variance));
        return tiers[index];
    }

    public static RitualOutcome mapScoreToOutcome(double score01) {
        RitualOutcome[] tiers = RitualOutcome.values();
        int index = (int) (score01 * (tiers.length - 1));
        index = Math.max(0, Math.min(tiers.length - 1, index));
        return tiers[index];
    }
}