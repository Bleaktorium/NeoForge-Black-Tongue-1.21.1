package net.bleaktorium.black_tongue.coven;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import java.util.*;

public class CovenPotionPool {
    private static List<ResourceLocation> eligiblePool = null;

    private static List<ResourceLocation> getPool() {
        if (eligiblePool == null) {
            eligiblePool = BuiltInRegistries.POTION.stream()
                    .filter(potion -> !potion.getEffects().isEmpty())
                    .filter(potion -> potion.getEffects().stream()
                            .map(MobEffectInstance::getEffect)
                            .noneMatch(effect -> isForbidden(effect)))
                    .map(BuiltInRegistries.POTION::getKey)
                    .toList();
        }
        return eligiblePool;
    }

    private static boolean isForbidden(Holder<net.minecraft.world.effect.MobEffect> effect) {
        return effect.is(MobEffects.DAMAGE_BOOST) || effect.is(MobEffects.REGENERATION);
    }

    // Picks 3 DISTINCT potions at random from the eligible pool.
    public static List<ResourceLocation> rollThree() {
        List<ResourceLocation> shuffled = new ArrayList<>(getPool());
        Collections.shuffle(shuffled);

        List<ResourceLocation> result = new ArrayList<>();
        Set<Holder<MobEffect>> usedEffects = new HashSet<>();

        for (ResourceLocation id : shuffled) {
            if (result.size() == 3) break;

            Potion potion = BuiltInRegistries.POTION.get(id);
            if (potion == null || potion.getEffects().isEmpty()) continue;

            Holder<MobEffect> primaryEffect = potion.getEffects().get(0).getEffect();
            if (usedEffects.add(primaryEffect)) { // Set.add returns false if it was already present
                result.add(id);
            }
        }

        return result;
    }
}