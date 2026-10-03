package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.EmbalmingTableBlockEntity;
import net.bleaktorium.black_tongue.item.custom.OilItem;
import net.bleaktorium.black_tongue.item.custom.RemainsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EmbalmingTableBlock extends BaseEntityBlock {
    public static final MapCodec<EmbalmingTableBlock> CODEC = simpleCodec(EmbalmingTableBlock::new);

    public enum TablePart implements StringRepresentable {
        MAIN("main"), SIDE("side");
        private final String name;
        TablePart(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
    }

    public static final EnumProperty<TablePart> PART = EnumProperty.create("part", TablePart.class);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 11, 16);

    public EmbalmingTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(PART, TablePart.MAIN)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }


    public static Direction sideDirection(Direction facing) {
        return facing.getCounterClockWise();
    }

    public static BlockPos mainPos(BlockState state, BlockPos pos) {
        return state.getValue(PART) == TablePart.MAIN
                ? pos
                : pos.relative(sideDirection(state.getValue(FACING)).getOpposite());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos sidePos = context.getClickedPos().relative(sideDirection(facing));
        if (!context.getLevel().getBlockState(sidePos).canBeReplaced(context)) return null;
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.relative(sideDirection(state.getValue(FACING))),
                state.setValue(PART, TablePart.SIDE), Block.UPDATE_ALL);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction side = sideDirection(state.getValue(FACING));
        Direction toPartner = state.getValue(PART) == TablePart.MAIN ? side : side.getOpposite();
        if (direction == toPartner && !neighbor.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(PART) == TablePart.SIDE) {
            level.destroyBlock(mainPos(state, pos), !player.isCreative(), player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())
                && level.getBlockEntity(pos) instanceof EmbalmingTableBlockEntity table && !table.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, table.getRemains());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof RemainsItem)
                || !(level.getBlockEntity(mainPos(state, pos)) instanceof EmbalmingTableBlockEntity table)
                || !table.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            table.placeRemains(stack.split(1));
            level.playSound(null, pos, SoundEvents.BONE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
        }
        if (stack.getItem() instanceof OilItem oil
                && level.getBlockEntity(mainPos(state, pos)) instanceof EmbalmingTableBlockEntity table
                && table.canPour()) {
            if (level instanceof ServerLevel serverLevel) {
                table.startPour(serverLevel, oil.tier());
                stack.consume(1, player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(mainPos(state, pos)) instanceof EmbalmingTableBlockEntity table
                && !table.isEmpty() && !table.isPouring()) {
            if (!level.isClientSide) {
                ItemStack taken = table.takeRemains();
                if (!player.addItem(taken)) player.drop(taken, false);
                level.playSound(null, pos, SoundEvents.BONE_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 0.8F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == TablePart.MAIN ? new EmbalmingTableBlockEntity(pos, state) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == TablePart.MAIN ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, st, be) -> {
            if (be instanceof EmbalmingTableBlockEntity table && lvl instanceof ServerLevel serverLevel) {
                table.tickServer(serverLevel);
            }
        };
    }
}