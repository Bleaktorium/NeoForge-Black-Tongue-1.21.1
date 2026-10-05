package net.bleaktorium.black_tongue.ritual;

import net.bleaktorium.black_tongue.block.custom.MoonPhase;
import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record RitualRecipe(String name, Kind kind, List<Seat> requiredSeats, Map<BlockPos, ItemLike> requiredItems,
                           boolean usesOfferings, int minOfferings, int requiredAmplification,
                           @Nullable ItemLike result, int resultCount) {

    public enum Kind { OFFERING_GIFT, COVEN_FORMATION, INFUSION }

    public enum SeatRole {
        COVEN_MOTHER,
        INITIATOR,
        COVEN_MEMBER
    }

    public record Seat(BlockPos offset, MoonPhase phase, SeatRole role) {

        public String describe() {
            String who = switch (role) {
                case COVEN_MOTHER -> "a Coven Mother";
                case INITIATOR -> "yourself";
                case COVEN_MEMBER -> "a member of your coven";
            };
            String phaseName = phase.getSerializedName().replace('_', ' ');
            return who + " on the " + phaseName + " rune (" + compass(offset) + ")";
        }
    }

    public static String compass(BlockPos offset) {
        String ns = offset.getZ() < 0 ? "north" : offset.getZ() > 0 ? "south" : "";
        String ew = offset.getX() > 0 ? "east" : offset.getX() < 0 ? "west" : "";
        return ns.isEmpty() || ew.isEmpty() ? ns + ew : ns + "-" + ew;
    }

    public static String describeItem(BlockPos offset, ItemLike item) {
        return new ItemStack(item).getHoverName().getString() + " on the holding rune (" + compass(offset) + ")";
    }

    // True when this recipe wants exactly this item on the holding rune at this offset.
    public boolean needsAt(BlockPos offset, ItemStack stack) {
        ItemLike wanted = requiredItems.get(offset);
        return wanted != null && stack.is(wanted.asItem());
    }

    // Infusion layout: the numbered marks from Helen's diagram.
    private static final List<BlockPos> MARK_1 = List.of(
            new BlockPos(-2, -1, -4), new BlockPos(2, -1, -4), new BlockPos(-2, -1, 4), new BlockPos(2, -1, 4));
    private static final List<BlockPos> MARK_2 = List.of(
            new BlockPos(-1, -1, -4), new BlockPos(1, -1, -4), new BlockPos(-1, -1, 4), new BlockPos(1, -1, 4));
    private static final List<BlockPos> MARK_3 = List.of(
            new BlockPos(-4, -1, -2), new BlockPos(4, -1, -2), new BlockPos(-4, -1, 2), new BlockPos(4, -1, 2));

    private static final List<Seat> INFUSION_SEATS = List.of(
            new Seat(new BlockPos(-3, -1, -3), MoonPhase.WANING_CRESCENT, SeatRole.INITIATOR),
            new Seat(new BlockPos(3, -1, -3), MoonPhase.WAXING_CRESCENT, SeatRole.COVEN_MOTHER),
            new Seat(new BlockPos(-3, -1, 3), MoonPhase.NEW_MOON, SeatRole.COVEN_MEMBER),
            new Seat(new BlockPos(3, -1, 3), MoonPhase.NEW_MOON, SeatRole.COVEN_MEMBER));

    private static Map<BlockPos, ItemLike> fill(Map<BlockPos, ItemLike> map, List<BlockPos> spots, ItemLike item) {
        Map<BlockPos, ItemLike> copy = new LinkedHashMap<>(map);
        for (BlockPos spot : spots) copy.put(spot, item);
        return Collections.unmodifiableMap(copy);
    }

    public static final RitualRecipe COVEN_FORMATION = new RitualRecipe(
            "Coven Formation", Kind.COVEN_FORMATION,
            List.of(
                    new Seat(new BlockPos(3, -1, -3), MoonPhase.FULL_MOON, SeatRole.COVEN_MOTHER),
                    new Seat(new BlockPos(-3, -1, 3), MoonPhase.NEW_MOON, SeatRole.INITIATOR)),
            Map.of(), false, 0, 50, null, 0);

    public static final RitualRecipe INFUSED_IRON = new RitualRecipe(
            "Iron Infusion", Kind.INFUSION, INFUSION_SEATS,
            fill(fill(Map.of(), MARK_1, Items.IRON_INGOT), MARK_2, ModItems.SEED_OIL),
            false, 0, 190, ModItems.INFUSED_IRON_INGOT, 4);

    public static final RitualRecipe INFUSED_DIAMOND = new RitualRecipe(
            "Diamond Infusion", Kind.INFUSION, INFUSION_SEATS,
            fill(fill(fill(Map.of(), MARK_1, Items.DIAMOND), MARK_2, ModItems.SEED_OIL), MARK_3, ModItems.HAUNTING_GLASS),
            false, 0, 250, ModItems.INFUSED_DIAMOND, 4);

    public static final RitualRecipe OFFERING_GIFT = new RitualRecipe(
            "Offering Gift", Kind.OFFERING_GIFT, List.of(), Map.of(), true, 1, 50, null, 0);

    public static final List<RitualRecipe> ALL = List.of(COVEN_FORMATION, INFUSED_DIAMOND, INFUSED_IRON, OFFERING_GIFT);
}