package net.bleaktorium.black_tongue.cauldron;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import java.util.Map;

public record CauldronRecipe(int baseTemperature, Map<Item, Integer> solidIngredients,
                             Map<Item, Integer> liquidIngredients, int waveCount,
                             Holder<MobEffect> rewardEffect, int maxDurationTicks, int maxAmplifier) {
}