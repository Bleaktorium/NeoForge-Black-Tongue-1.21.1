package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.block.entity.ModBlockEntities;
import net.bleaktorium.black_tongue.block.entity.RitualTableBlockEntity;
import net.bleaktorium.black_tongue.block.entity.RunicStoneBlockEntity;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.ritual.RitualEffects;
import net.bleaktorium.black_tongue.ritual.RitualMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RitualTableBlock extends BaseEntityBlock {

    public static final MapCodec<RitualTableBlock> CODEC = simpleCodec(RitualTableBlock::new);

    // The 4 required cardinal positions — one block BELOW the table
    private static final List<BlockPos> CORE_OFFSETS = List.of(
            new BlockPos(0, -1, -4),
            new BlockPos(0, -1, 4),
            new BlockPos(-4, -1, 0),
            new BlockPos(4, -1, 0)
    );

    // The remaining 20 positions that complete the circle's curve — recognized, not yet required
    private static final List<BlockPos> RING_OFFSETS = List.of(
            new BlockPos(-2, -1, -4), new BlockPos(-1, -1, -4), new BlockPos(1, -1, -4), new BlockPos(2, -1, -4),
            new BlockPos(-3, -1, -3), new BlockPos(3, -1, -3),
            new BlockPos(-4, -1, -2), new BlockPos(4, -1, -2),
            new BlockPos(-4, -1, -1), new BlockPos(4, -1, -1),
            new BlockPos(-4, -1, 1), new BlockPos(4, -1, 1),
            new BlockPos(-4, -1, 2), new BlockPos(4, -1, 2),
            new BlockPos(-3, -1, 3), new BlockPos(3, -1, 3),
            new BlockPos(-2, -1, 4), new BlockPos(-1, -1, 4), new BlockPos(1, -1, 4), new BlockPos(2, -1, 4)
    );

    public RitualTableBlock(BlockBehaviour.Properties properties) {

        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {

        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new RitualTableBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (!isCoreCircleValid(level, pos)) {
            level.addFreshEntity(new LightningBolt(EntityType.LIGHTNING_BOLT, level) {{
                setPos(player.getX(), player.getY(), player.getZ());
                setVisualOnly(true);
            }});
            player.displayClientMessage(Component.literal("The circle is incomplete."), true);
            return InteractionResult.SUCCESS;
        }

        int totalAmplification = RitualMath.PLAYER_BASE_AMPLIFICATION;
        double stabilitySum = RitualMath.PLAYER_BASE_STABILITY;
        int stabilityContributors = 1;

        List<RunicStoneBlockEntity> filledNestingStones = new ArrayList<>();

        for (BlockPos offset : RING_OFFSETS) {
            BlockPos checkPos = pos.offset(offset);
            BlockState ringState = level.getBlockState(checkPos);

            if (!(ringState.getBlock() instanceof RunicStoneBlock)) continue;

            RuneType rune = ringState.getValue(RunicStoneBlock.RUNE);

            switch (rune) {
                case NESTING -> {
                    if (level.getBlockEntity(checkPos) instanceof RunicStoneBlockEntity be && !be.getStoredItem().isEmpty()) {
                        totalAmplification += RitualMath.NESTING_FILLED_AMPLIFICATION;
                        stabilitySum += RitualMath.NESTING_STABILITY;
                        stabilityContributors++;
                        filledNestingStones.add(be);
                    }
                }
                case POTENCY -> {
                    totalAmplification += RitualMath.POTENCY_AMPLIFICATION;
                    stabilitySum += RitualMath.POTENCY_STABILITY;
                    stabilityContributors++;
                }
                case BLANK, MOON_PHASE -> {}
            }
        }

        double weightedStability = stabilitySum / stabilityContributors;
        int requiredAmplification = 50;

        RitualMath.RitualOutcome outcome = RitualMath.rollRitual(
                totalAmplification, requiredAmplification, weightedStability, level.getRandom());

        if (outcome == null) {
            player.displayClientMessage(Component.literal(
                    "Not enough power (" + totalAmplification + "/" + requiredAmplification + ")."), true);
        } else {
            ItemStack rewardTemplate = new ItemStack(ModItems.OFFERING_GIFT.get());
            RitualEffects.applyConsumableOutcome(outcome, player, filledNestingStones, rewardTemplate, 1);
            player.displayClientMessage(Component.literal("Ritual outcome: " + outcome), true);
        }

        return InteractionResult.SUCCESS;
    }

    private boolean isCoreCircleValid(Level level, BlockPos tablePos) {
        for (BlockPos offset : CORE_OFFSETS) {
            BlockPos checkPos = tablePos.offset(offset);
            if (!(level.getBlockState(checkPos).getBlock() instanceof RunicStoneBlock)) {
                return false;
            }
        }
        return true;
    }

    private int countRingStones(Level level, BlockPos tablePos) {
        int count = 0;
        for (BlockPos offset : RING_OFFSETS) {
            BlockPos checkPos = tablePos.offset(offset);
            if (level.getBlockState(checkPos).getBlock() instanceof RunicStoneBlock) {
                count++;
            }
        }
        return count;
    }
}