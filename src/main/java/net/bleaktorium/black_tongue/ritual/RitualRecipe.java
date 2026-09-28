package net.bleaktorium.black_tongue.ritual;

import net.bleaktorium.black_tongue.block.custom.MoonPhase;
import net.minecraft.core.BlockPos;
import java.util.List;

public record RitualRecipe(String name, Kind kind, List<Seat> requiredSeats, boolean usesOfferings,
                           int minOfferings, int requiredAmplification) {

    public enum Kind { OFFERING_GIFT, COVEN_FORMATION }

    public enum SeatRole {
        COVEN_MOTHER,
        INITIATOR
    }

    public record Seat(BlockPos offset, MoonPhase phase, SeatRole role) {

        public String describe() {
            String who = role == SeatRole.COVEN_MOTHER ? "a Coven Mother" : "yourself";
            String phaseName = phase.getSerializedName().replace('_', ' ');
            return who + " on the " + phaseName + " rune (" + compass() + ")";
        }

        private String compass() {
            String ns = offset.getZ() < 0 ? "north" : offset.getZ() > 0 ? "south" : "";
            String ew = offset.getX() > 0 ? "east" : offset.getX() < 0 ? "west" : "";
            return ns.isEmpty() || ew.isEmpty() ? ns + ew : ns + "-" + ew;
        }
    }

    public static final RitualRecipe COVEN_FORMATION = new RitualRecipe(
            "Coven Formation", Kind.COVEN_FORMATION,
            List.of(
                    new Seat(new BlockPos(3, -1, -3), MoonPhase.FULL_MOON, SeatRole.COVEN_MOTHER),
                    new Seat(new BlockPos(-3, -1, 3), MoonPhase.NEW_MOON, SeatRole.INITIATOR)),
            false, 0, 50);

    public static final RitualRecipe OFFERING_GIFT = new RitualRecipe(
            "Offering Gift", Kind.OFFERING_GIFT, List.of(), true, 1, 50);

    public static final List<RitualRecipe> ALL = List.of(COVEN_FORMATION, OFFERING_GIFT);
}