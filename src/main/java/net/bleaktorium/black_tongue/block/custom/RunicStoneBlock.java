package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.RunicStoneBlockEntity;
import net.bleaktorium.black_tongue.item.custom.RunicEtchingTool;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class RunicStoneBlock extends BaseEntityBlock {
    public static final MapCodec<RunicStoneBlock> CODEC = simpleCodec(RunicStoneBlock::new);
    public static final EnumProperty<RuneType> RUNE = EnumProperty.create("rune", RuneType.class);

    public RunicStoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(RUNE, RuneType.BLANK));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RUNE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RunicStoneBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(RUNE) == RuneType.NESTING && level.getBlockEntity(pos) instanceof RunicStoneBlockEntity be) {
            if (!be.getStoredItem().isEmpty() && !level.isClientSide) {
                player.getInventory().placeItemBackInInventory(be.getStoredItem());
                be.setStoredItem(ItemStack.EMPTY);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof RunicEtchingTool) {
            if (!level.isClientSide) {
                RuneType current = state.getValue(RUNE);
                RuneType next = RuneType.values()[(current.ordinal() + 1) % RuneType.values().length];
                level.setBlock(pos, state.setValue(RUNE, next), 3);
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (!stack.isEmpty() && state.getValue(RUNE) == RuneType.NESTING
                && level.getBlockEntity(pos) instanceof RunicStoneBlockEntity be) {
            if (be.getStoredItem().isEmpty()) {
                if (!level.isClientSide) {
                    be.setStoredItem(stack.copyWithCount(1));
                    stack.shrink(1);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}