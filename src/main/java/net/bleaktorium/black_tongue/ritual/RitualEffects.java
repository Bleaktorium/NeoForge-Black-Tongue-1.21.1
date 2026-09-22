package net.bleaktorium.black_tongue.ritual;

import net.bleaktorium.black_tongue.block.entity.RunicStoneBlockEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RitualEffects {

    public static void applyConsumableOutcome(RitualMath.RitualOutcome outcome, Player player,
                                              List<RunicStoneBlockEntity> nestingStonesWithItems,
                                              ItemStack rewardItem, int criticalSuccessCap) {
        switch (outcome) {
            case CRITICAL_FAILURE -> {
                consumeIngredients(nestingStonesWithItems);
                emptyHunger(player);
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 0));
            }
            case FAILURE_WITH_SIDE_EFFECT -> {
                consumeIngredients(nestingStonesWithItems);
                emptyHunger(player);
            }
            case PARTIAL_SUCCESS_WITH_SIDE_EFFECT -> {
                consumeIngredients(nestingStonesWithItems);
                emptyHunger(player);
                giveItem(player, rewardItem, 1);
            }
            case SUCCESS -> {
                consumeIngredients(nestingStonesWithItems);
                giveItem(player, rewardItem, 1);
            }
            case CRITICAL_SUCCESS -> {
                int count = Math.min(3, criticalSuccessCap);
                giveItem(player, rewardItem, count);
            }
        }
    }

    private static void consumeIngredients(List<RunicStoneBlockEntity> stones) {
        for (RunicStoneBlockEntity be : stones) {
            be.setStoredItem(ItemStack.EMPTY);
        }
    }

    private static void emptyHunger(Player player) {
        player.getFoodData().setFoodLevel(0);
        player.getFoodData().setSaturation(0f);
    }

    private static void giveItem(Player player, ItemStack template, int count) {
        ItemStack toGive = template.copyWithCount(count);
        if (!player.getInventory().add(toGive)) {
            player.drop(toGive, false);
        }
    }
}