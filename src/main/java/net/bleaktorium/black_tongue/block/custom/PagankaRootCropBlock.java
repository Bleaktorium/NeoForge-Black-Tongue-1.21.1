package net.bleaktorium.black_tongue.block.custom;

import net.bleaktorium.black_tongue.block.entity.PagankaRootCropBlockEntity;
import net.bleaktorium.black_tongue.entity.ModEntities;
import net.bleaktorium.black_tongue.entity.custom.PagankaRootEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PagankaRootCropBlock extends HerbCropBlock implements EntityBlock {

    public static final BooleanProperty WAITED_ONE_NIGHT = BooleanProperty.create("waited_one_night");

    public PagankaRootCropBlock(BlockBehaviour.Properties properties, int maxAge, Supplier<? extends Item> seedItem) {
        super(properties, maxAge, seedItem);
        registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(WAITED_ONE_NIGHT, false));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WAITED_ONE_NIGHT);
    }

    @Override
    protected boolean canGrowThisTick(ServerLevel level, BlockPos pos, BlockState state) {
        return level.isNight();
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isMaxAge(state) && !level.isNight() && !state.getValue(WAITED_ONE_NIGHT)) {
            level.setBlock(pos, state.setValue(WAITED_ONE_NIGHT, true), Block.UPDATE_ALL);
            return;
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    protected void onFullyRipeTick(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(WAITED_ONE_NIGHT)) {
            transformIntoEntity(level, pos);
        }
    }

    private void transformIntoEntity(ServerLevel level, BlockPos pos) {
        PagankaRootEntity entity = ModEntities.PAGANKA_ROOT.get().create(level);
        if (entity == null) return;

        entity.setGardenPos(pos.immutable());
        entity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.getRandom().nextFloat() * 360f, 0f);
        entity.setPersistenceRequired();
        level.addFreshEntity(entity);

        level.removeBlock(pos, false);
    }

    // GeckoLib

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PagankaRootCropBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

}