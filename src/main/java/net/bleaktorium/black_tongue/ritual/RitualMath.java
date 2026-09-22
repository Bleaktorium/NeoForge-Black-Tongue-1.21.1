package net.bleaktorium.black_tongue.ritual;

import net.minecraft.util.RandomSource;

public class RitualMath {

    // Tunable balance numbers

    public static final int PLAYER_BASE_AMPLIFICATION = 30; // a solo player brings this much "power" on their own
    public static final int PLAYER_BASE_STABILITY = 70;      // and this much "control" over that power

    public static final int NESTING_FILLED_AMPLIFICATION = 10; // a Nesting rune holding an item adds this much power
    public static final int NESTING_STABILITY = 50;             // and counts as a "medium control" contributor

    public static final int POTENCY_AMPLIFICATION = 15; // Potency runes add more raw power...
    public static final int POTENCY_STABILITY = 0;       // ...but contribute nothing to stability (drags the average down)


    public enum RitualOutcome {
        CRITICAL_FAILURE,
        FAILURE_WITH_SIDE_EFFECT,
        PARTIAL_SUCCESS_WITH_SIDE_EFFECT,
        SUCCESS,
        CRITICAL_SUCCESS
    }

    public static RitualOutcome rollRitual(int totalAmplification, int requiredAmplification,
                                           double weightedStability, RandomSource random) {

        if (totalAmplification < requiredAmplification) {
            return null; // not enough power means no roll happens at all
        }

        RitualOutcome[] tiers = RitualOutcome.values();

        int centerTierIndex = (int) (weightedStability / 100.0 * (tiers.length - 1));
        centerTierIndex = Math.max(0, Math.min(tiers.length - 1, centerTierIndex)); // safety clamp

        int variance = random.nextInt(3) - 1;

        int finalTierIndex = centerTierIndex + variance;
        finalTierIndex = Math.max(0, Math.min(tiers.length - 1, finalTierIndex));

        return tiers[finalTierIndex];
    }

    public static RitualOutcome mapScoreToOutcome(double score01) {
        RitualOutcome[] tiers = RitualOutcome.values();
        int index = (int) (score01 * (tiers.length - 1));
        index = Math.max(0, Math.min(tiers.length - 1, index));
        return tiers[index];
    }
}