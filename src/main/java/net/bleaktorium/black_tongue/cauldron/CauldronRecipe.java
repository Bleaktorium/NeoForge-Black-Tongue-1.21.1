package net.bleaktorium.black_tongue.cauldron;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record CauldronRecipe(int baseTemperature, Map<Item, Integer> solidIngredients,
                             Map<Item, Integer> liquidIngredients, int waveCount,
                             @Nullable Holder<MobEffect> rewardEffect, int maxDurationTicks, int maxAmplifier,
                             ItemStack resultItem) {

    public CauldronRecipe(int baseTemperature, Map<Item, Integer> solidIngredients,
                          Map<Item, Integer> liquidIngredients, int waveCount,
                          Holder<MobEffect> rewardEffect, int maxDurationTicks, int maxAmplifier) {
        this(baseTemperature, solidIngredients, liquidIngredients, waveCount,
                rewardEffect, maxDurationTicks, maxAmplifier, ItemStack.EMPTY);
    }

    public static CauldronRecipe item(int baseTemperature, Map<Item, Integer> solidIngredients,
                                      Map<Item, Integer> liquidIngredients, int waveCount, ItemStack result) {
        return new CauldronRecipe(baseTemperature, solidIngredients, liquidIngredients, waveCount,
                null, 0, 0, result);
    }

    public boolean makesItem() {
        return !resultItem.isEmpty();
    }
}