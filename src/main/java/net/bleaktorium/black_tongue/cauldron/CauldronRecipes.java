package net.bleaktorium.black_tongue.cauldron;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CauldronRecipes {
    private static final List<CauldronRecipe> RECIPES = new ArrayList<>();

    public static void bootstrap() {
        RECIPES.add(new CauldronRecipe(
                2,
                Map.of(Items.SUGAR, 1, Items.COCOA_BEANS, 1),
                Map.of(Items.HONEY_BOTTLE, 1, Items.MILK_BUCKET, 1),
                3,
                MobEffects.REGENERATION,
                60 * 20,
                1
        ));
        // Hot base (2) + 1 leather + 1 seed oil -> Infused Leather, 2 waves
        RECIPES.add(CauldronRecipe.item(
                2,
                Map.of(Items.LEATHER, 1),
                Map.of(ModItems.SEED_OIL.get(), 1),
                4,
                new ItemStack(ModItems.INFUSED_LEATHER.get())
        ));
    }

    public static List<CauldronRecipe> findMatchingFirstHalf(int baseTemperature, Map<Item, Integer> submitted, boolean solidsFirst) {
        List<CauldronRecipe> matches = new ArrayList<>();
        for (CauldronRecipe recipe : RECIPES) {
            if (recipe.baseTemperature() != baseTemperature) continue;

            Map<Item, Integer> requiredFirstHalf = solidsFirst ? recipe.solidIngredients() : recipe.liquidIngredients();
            if (requiredFirstHalf.equals(submitted)) {
                matches.add(recipe);
            }
        }
        return matches;
    }
}