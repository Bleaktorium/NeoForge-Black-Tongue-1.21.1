package net.bleaktorium.black_tongue.farming;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
        register(net.minecraft.world.item.Items.WHEAT, new ItemStack(net.minecraft.world.item.Items.WHEAT_SEEDS));
    }
}