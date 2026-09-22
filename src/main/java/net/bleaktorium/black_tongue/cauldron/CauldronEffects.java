package net.bleaktorium.black_tongue.cauldron;

import net.bleaktorium.black_tongue.ritual.RitualMath;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import java.util.List;
import java.util.Optional;

public class CauldronEffects {

    public static void applyBrewOutcome(RitualMath.RitualOutcome outcome, Player player, CauldronRecipe recipe) {
        double qualityFraction = switch (outcome) {
            case CRITICAL_FAILURE, FAILURE_WITH_SIDE_EFFECT -> 0.0;
            case PARTIAL_SUCCESS_WITH_SIDE_EFFECT -> 0.5;
            case SUCCESS -> 0.8;
            case CRITICAL_SUCCESS -> 1.0;
        };

        if (qualityFraction <= 0.0) {
            return;
        }

        int duration = (int) (recipe.maxDurationTicks() * qualityFraction);
        int amplifier = (int) Math.round(recipe.maxAmplifier() * qualityFraction);
        amplifier = Math.max(0, amplifier);

        ItemStack potion = new ItemStack(Items.POTION);
        potion.set(DataComponents.POTION_CONTENTS, new PotionContents(
                Optional.empty(),
                Optional.empty(),
                List.of(new MobEffectInstance(recipe.rewardEffect(), duration, amplifier))
        ));

        if (!player.getInventory().add(potion)) {
            player.drop(potion, false);
        }
    }
}