package net.bleaktorium.black_tongue.farming;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HerbDryingProfiles {
    private static final Map<Item, DryingProfile> PROFILES = new HashMap<>();

    public static final int TICKS_PER_STAGE = 2 * 60 * 20;

    public static void register(Item rawHerb, ItemStack... stageResultsInOrder) {
        PROFILES.put(rawHerb, new DryingProfile(List.of(stageResultsInOrder)));
    }

    public static DryingProfile get(Item rawHerb) {
        return PROFILES.get(rawHerb);
    }

    public static boolean canDry(Item rawHerb) {
        return PROFILES.containsKey(rawHerb);
    }

    public static void bootstrap() {
            register(ModItems.MOTHLEAF.get(), new ItemStack(ModItems.MOTHLEAF_DRY.get()));
            register(ModItems.DEVILSTHORN.get(),
                    new ItemStack(ModItems.DEVILSTHORN_DRIED1.get()),
                    new ItemStack(ModItems.DEVILSTHORN_DRIED2.get()));
    }

}