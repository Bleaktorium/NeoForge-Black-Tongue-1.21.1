package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.ScryerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ScryerBlock extends BaseEntityBlock {

    public static final MapCodec<ScryerBlock> CODEC = simpleCodec(ScryerBlock::new);

    public ScryerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScryerBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ScryerBlockEntity scryer)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.getItem() == Items.WATER_BUCKET && !scryer.hasWater()) {
            if (!level.isClientSide) {
                scryer.pourWater();
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    if (!player.getInventory().add(new ItemStack(Items.BUCKET))) {
                        player.drop(new ItemStack(Items.BUCKET), false);
                    }
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (scryer.hasWater() && !scryer.isScanning() && scryer.getResult() == ScryerBlockEntity.Result.NONE) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                if (scryer.beginScan(stack, serverPlayer)) {
                    stack.shrink(1);
                    player.displayClientMessage(Component.literal("The waters begin to read..."), true);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof ScryerBlockEntity scryer && scryer.hasWater()) {
            if (!level.isClientSide) {
                scryer.empty();
                player.displayClientMessage(Component.literal("The scryer is emptied."), true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof ScryerBlockEntity scryer && lvl instanceof ServerLevel serverLevel) {
                scryer.tickServer(serverLevel);
            }
        };
    }
}