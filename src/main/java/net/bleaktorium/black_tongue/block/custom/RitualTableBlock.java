package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.RitualTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RitualTableBlock extends BaseEntityBlock {

    public static final MapCodec<RitualTableBlock> CODEC = simpleCodec(RitualTableBlock::new);

    // The 4 required cardinal stones, one block BELOW the table
    public static final List<BlockPos> CORE_OFFSETS = List.of(
            new BlockPos(0, -1, -4),
            new BlockPos(0, -1, 4),
            new BlockPos(-4, -1, 0),
            new BlockPos(4, -1, 0)
    );

    public static final List<BlockPos> PILLAR_OFFSETS = List.of(
            new BlockPos(0, 0, -6),
            new BlockPos(0, 0, 6),
            new BlockPos(-6, 0, 0),
            new BlockPos(6, 0, 0)
    );

    // The other 20 positions that complete the circle's curve
    public static final List<BlockPos> RING_OFFSETS = List.of(
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
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof RitualTableBlockEntity table) {
            table.beginRitual(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean isCoreCircleValid(Level level, BlockPos tablePos) {
        for (BlockPos offset : CORE_OFFSETS) {
            if (!(level.getBlockState(tablePos.offset(offset)).getBlock() instanceof RunicStoneBlock)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof RitualTableBlockEntity table && !lvl.isClientSide) {
                table.tickServer();
            }
        };
    }
}