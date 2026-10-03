package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.AncestralPillarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class AncestralPillarBlock extends BaseEntityBlock {
    public static final MapCodec<AncestralPillarBlock> CODEC = simpleCodec(AncestralPillarBlock::new);

    public AncestralPillarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(PART, PillarPart.BOTTOM)
                .setValue(FACING, Direction.NORTH));
    }

    public enum PillarPart implements StringRepresentable {
        BOTTOM("bottom"), MIDDLE("middle"), TOP("top");
        private final String name;
        PillarPart(String name) {
            this.name = name; }
        @Override public String getSerializedName() {
            return name; }
    }

    public static final EnumProperty<PillarPart> PART = EnumProperty.create("part", PillarPart.class);

    private static final VoxelShape BOTTOM_SHAPE = Block.box(1, 0, 2, 15, 16, 14);
    private static final VoxelShape MIDDLE_SHAPE = Block.box(2, 0, 3, 14, 16, 13);
    private static final VoxelShape TOP_SHAPE = Shapes.or(
            Block.box(3, 0, 4, 13, 16, 12),
            Block.box(5, 16, 5.25, 11, 17, 11.25));
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape BOTTOM_NS = Block.box(1, 0, 2, 15, 16, 14);
    private static final VoxelShape BOTTOM_EW = Block.box(2, 0, 1, 14, 16, 15);
    private static final VoxelShape MIDDLE_NS = Block.box(2, 0, 3, 14, 16, 13);
    private static final VoxelShape MIDDLE_EW = Block.box(3, 0, 2, 13, 16, 14);
    private static final VoxelShape TOP_NS = Shapes.or(Block.box(3, 0, 4, 13, 16, 12), Block.box(5, 16, 5.25, 11, 17, 11.25));
    private static final VoxelShape TOP_EW = Shapes.or(Block.box(4, 0, 3, 12, 16, 13), Block.box(5.25, 16, 5, 11.25, 17, 11));

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() + 2 >= level.getMaxBuildHeight()) return null;
        if (!level.getBlockState(pos.above()).canBeReplaced(context)) return null;
        if (!level.getBlockState(pos.above(2)).canBeReplaced(context)) return null;
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        PillarPart part = state.getValue(PART);
        boolean needsAbove = part != PillarPart.TOP;
        boolean needsBelow = part != PillarPart.BOTTOM;
        if ((direction == Direction.UP && needsAbove && !isPartOfMe(neighbor))
                || (direction == Direction.DOWN && needsBelow && !isPartOfMe(neighbor))) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean eastWest = state.getValue(FACING).getAxis() == Direction.Axis.X;
        return switch (state.getValue(PART)) {
            case BOTTOM -> eastWest ? BOTTOM_EW : BOTTOM_NS;
            case MIDDLE -> eastWest ? MIDDLE_EW : MIDDLE_NS;
            case TOP -> eastWest ? TOP_EW : TOP_NS;
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    private boolean isPartOfMe(BlockState neighbor) {
        return neighbor.is(this);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(PART) != PillarPart.BOTTOM) {
            level.destroyBlock(basePos(state, pos), !player.isCreative(), player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    public static BlockPos basePos(BlockState state, BlockPos pos) {
        return switch (state.getValue(PART)) {
            case BOTTOM -> pos;
            case MIDDLE -> pos.below();
            case TOP -> pos.below(2);
        };
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(PART, PillarPart.MIDDLE), Block.UPDATE_ALL);
        level.setBlock(pos.above(2), state.setValue(PART, PillarPart.TOP), Block.UPDATE_ALL);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == PillarPart.BOTTOM ? new AncestralPillarBlockEntity(pos, state) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == PillarPart.BOTTOM ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    // TEMPORARY test hook until the binding ritual exists (milestone 5):
    // creative only, bone = coven witch, skeleton skull = ancestor, sneak + empty hand = clear
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.isCreative() || !(level.getBlockEntity(basePos(state, pos)) instanceof AncestralPillarBlockEntity pillar)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.is(Items.BONE)) {
            if (!level.isClientSide) pillar.bind(AncestralPillarBlockEntity.Occupant.WITCH, "Test Witch");
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.is(Items.SKELETON_SKULL)) {
            if (!level.isClientSide) pillar.bind(AncestralPillarBlockEntity.Occupant.ANCESTOR, "Test Ancestor");
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isCreative() && player.isShiftKeyDown()
                && level.getBlockEntity(basePos(state, pos)) instanceof AncestralPillarBlockEntity pillar) {
            if (!level.isClientSide) pillar.clear();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}