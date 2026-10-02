package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.WitchsCauldronBlockEntity;
import net.bleaktorium.black_tongue.item.custom.CauldronScrubItem;
import net.bleaktorium.black_tongue.item.custom.CauldronTerminatorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class WitchsCauldronBlock extends BaseEntityBlock {

    public static final MapCodec<WitchsCauldronBlock> CODEC = simpleCodec(WitchsCauldronBlock::new);

    public WitchsCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static final VoxelShape BASE    = Block.box(1, 0, 1, 15, 3, 15);
    private static final VoxelShape BODY    = Block.box(1, 3, 1, 15, 16, 15);
    private static final VoxelShape BULGE_X = Block.box(0, 5, 3, 16, 14, 13);
    private static final VoxelShape BULGE_Z = Block.box(3, 5, 0, 13, 14, 16);
    private static final VoxelShape INSIDE  = Block.box(2, 4, 2, 14, 16, 14);

    private static final VoxelShape SHAPE = Shapes.join(
            Shapes.or(BASE, BODY, BULGE_X, BULGE_Z),
            INSIDE,
            BooleanOp.ONLY_FIRST);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return INSIDE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {

        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WitchsCauldronBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() == Items.WATER_BUCKET
                && level.getBlockEntity(pos) instanceof WitchsCauldronBlockEntity cauldron
                && !cauldron.isActive()) {

            if (cauldron.isDirty()) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal("This cauldron needs a scrub before it can be used again."), true);
                }
                return ItemInteractionResult.SUCCESS;
            }

            if (!level.isClientSide) {
                cauldron.startBrewing();
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    if (!player.getInventory().add(new ItemStack(Items.BUCKET))) {
                        player.drop(new ItemStack(Items.BUCKET), false);
                    }
                }
                player.displayClientMessage(Component.literal("The cauldron begins to brew."), true);
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.getItem() instanceof CauldronScrubItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof WitchsCauldronBlockEntity cauldron) {
            cauldron.setLastInteractingPlayer(player);
            cauldron.stir();
            player.displayClientMessage(Component.literal(cauldron.getLastResult()), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof WitchsCauldronBlockEntity cauldron && !lvl.isClientSide) {
                cauldron.tickWave();

                AABB pickupArea = new AABB(pos.above()).inflate(0.3, 0.2, 0.3);
                List<ItemEntity> items = lvl.getEntitiesOfClass(ItemEntity.class, pickupArea);

                for (ItemEntity itemEntity : items) {
                    ItemStack stack = itemEntity.getItem();
                    Item item = stack.getItem();

                    if (item == Items.WATER_BUCKET) continue;

                    if (item instanceof CauldronTerminatorItem) {
                        if (cauldron.getStage() == WitchsCauldronBlockEntity.Stage.BREWING_WAVES) {
                            cauldron.terminateBrewing();
                            stack.shrink(1);
                            if (stack.isEmpty()) itemEntity.discard();
                        }
                        continue;
                    }

                    if (cauldron.tryAddIngredient(item)) {
                        stack.shrink(1);
                        if (stack.isEmpty()) {
                            itemEntity.discard();
                        }
                    }
                }
            }
        };
    }
}