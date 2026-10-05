package net.bleaktorium.black_tongue.ritual;

import net.bleaktorium.black_tongue.block.entity.RunicStoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RitualEffects {

    public static void applyConsumableOutcome(RitualMath.RitualOutcome outcome, @Nullable Player player,
                                              List<RunicStoneBlockEntity> nestingStonesWithItems,
                                              ItemStack rewardItem, int criticalSuccessCap,
                                              Level level, BlockPos tablePos) {
        switch (outcome) {
            case CRITICAL_FAILURE -> {
                consumeIngredients(nestingStonesWithItems);
                emptyHunger(player);
                if (player != null) player.addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 0));
            }
            case FAILURE_WITH_SIDE_EFFECT -> {
                consumeIngredients(nestingStonesWithItems);
                emptyHunger(player);
            }
            case PARTIAL_SUCCESS_WITH_SIDE_EFFECT -> {
                consumeIngredients(nestingStonesWithItems);
                emptyHunger(player);
                dropReward(level, tablePos, rewardItem, 1);
            }
            case SUCCESS -> {
                consumeIngredients(nestingStonesWithItems);
                dropReward(level, tablePos, rewardItem, 1);
            }
            case CRITICAL_SUCCESS -> {

                dropReward(level, tablePos, rewardItem, Math.min(3, criticalSuccessCap));
            }
        }
    }

    private static void consumeIngredients(List<RunicStoneBlockEntity> stones) {
        for (RunicStoneBlockEntity be : stones) {
            be.setStoredItem(ItemStack.EMPTY);
        }
    }

    private static void emptyHunger(@Nullable Player player) {
        if (player == null) return;
        player.getFoodData().setFoodLevel(0);
        player.getFoodData().setSaturation(0f);
    }

    private static void dropReward(Level level, BlockPos tablePos, ItemStack template, int count) {
        ItemEntity entity = new ItemEntity(level,
                tablePos.getX() + 0.5, tablePos.getY() + 1.2, tablePos.getZ() + 0.5,
                template.copyWithCount(template.getCount() * count));
        entity.setDeltaMovement(0, 0.2, 0);
        level.addFreshEntity(entity);
    }

    public static void applyFormationBackfire(List<ServerPlayer> players) {
        for (ServerPlayer p : players) {
            emptyHunger(p);
            p.addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 0));
        }
    }
}