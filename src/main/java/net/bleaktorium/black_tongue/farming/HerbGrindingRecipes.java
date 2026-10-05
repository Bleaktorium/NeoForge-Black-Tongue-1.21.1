package net.bleaktorium.black_tongue.farming;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class HerbGrindingRecipes {
    private static final Map<Item, ItemStack> RECIPES = new HashMap<>();

    public static void register(Item input, ItemStack output) {
        RECIPES.put(input, output);
    }

    public static ItemStack getResult(Item input) {
        ItemStack template = RECIPES.get(input);
        return template == null ? null : template.copy();
    }

    public static boolean canGrind(Item input) {
        return RECIPES.containsKey(input);
    }

    public static void bootstrap() {
        register(ModItems.MOTHLEAF.get(), new ItemStack(ModItems.MOTHLEAF_DUST.get()));
        register(Items.WHEAT_SEEDS, new ItemStack(ModItems.SEED_OIL.get()));
    }
}