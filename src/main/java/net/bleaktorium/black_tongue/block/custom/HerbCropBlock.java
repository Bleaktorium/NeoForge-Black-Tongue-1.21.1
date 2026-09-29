package net.bleaktorium.black_tongue.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CropBlock;

import java.util.function.Supplier;

public class HerbCropBlock extends CropBlock {

    private final int maxAge;
    private final Supplier<? extends Item> seedItem;

    public HerbCropBlock(BlockBehaviour.Properties properties, int maxAge, Supplier<? extends Item> seedItem) {
        super(properties);
        this.maxAge = maxAge;
        this.seedItem = seedItem;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public ItemLike getBaseSeedId() {
        return seedItem.get();
    }

    protected boolean canGrowThisTick(ServerLevel level, BlockPos pos, BlockState state) {
        return true;
    }

    protected void onFullyRipeTick(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {

    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canGrowThisTick(level, pos, state)) return;

        super.randomTick(state, level, pos, random);

        BlockState current = level.getBlockState(pos);
        if (current.getValue(AGE) == maxAge) {
            onFullyRipeTick(level, pos, current, random);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) != maxAge) return;
        if (random.nextInt(10) != 0) return;

        double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
        double y = pos.getY() + 0.4 + random.nextDouble() * 0.3;
        double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
        level.addParticle(net.minecraft.core.particles.ParticleTypes.GLOW,
                x, y, z, 0.0, 0.015, 0.0);
    }
}