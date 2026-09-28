package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.RunicStoneBlockEntity;
import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.item.custom.CovenSummoningAmuletItem;
import net.bleaktorium.black_tongue.item.custom.RunicEtchingTool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
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
import org.jetbrains.annotations.Nullable;

public class MoonPhaseRuneBlock extends BaseEntityBlock {
    public static final MapCodec<MoonPhaseRuneBlock> CODEC = simpleCodec(MoonPhaseRuneBlock::new);
    public static final EnumProperty<MoonPhase> PHASE = EnumProperty.create("phase", MoonPhase.class);

    public MoonPhaseRuneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(PHASE, MoonPhase.NEW_MOON));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PHASE);
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
        if (level.getBlockEntity(pos) instanceof RunicStoneBlockEntity be && !be.getStoredItem().isEmpty()) {
            if (!level.isClientSide) {
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
                MoonPhase next = MoonPhase.values()[(state.getValue(PHASE).ordinal() + 1) % MoonPhase.values().length];
                level.setBlock(pos, state.setValue(PHASE, next), 3);
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (!(stack.getItem() instanceof CovenSummoningAmuletItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        if (stack.get(ModDataComponents.AMULET_BINDING.get()) == null) {
            player.displayClientMessage(Component.literal("This amulet isn't bound to anyone."), true);
            return ItemInteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof RunicStoneBlockEntity be) {
            if (!be.getStoredItem().isEmpty()) {
                player.displayClientMessage(Component.literal("This rune already holds an amulet."), true);
            } else {
                be.setStoredItem(stack.copyWithCount(1));
                stack.shrink(1);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())
                && level.getBlockEntity(pos) instanceof RunicStoneBlockEntity be) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.getStoredItem());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}