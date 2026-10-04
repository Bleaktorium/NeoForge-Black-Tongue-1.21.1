package net.bleaktorium.black_tongue.block.custom;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class SpiderGrassCropBlock extends HerbCropBlock {
    public static final int REGROW_AGE = 3; // your "stage4" model

    public SpiderGrassCropBlock(Properties properties, Supplier<? extends Item> seedItem) {
        super(properties, 6, seedItem);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!isMaxAge(state)) return super.useWithoutItem(state, level, pos, player, hit);
        if (!level.isClientSide) {
            popResource(level, pos, new ItemStack(ModItems.SPIDER_SILK.get(), 1 + level.random.nextInt(2)));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState regrown = getStateForAge(REGROW_AGE);
            level.setBlock(pos, regrown, Block.UPDATE_CLIENTS);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, regrown));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}