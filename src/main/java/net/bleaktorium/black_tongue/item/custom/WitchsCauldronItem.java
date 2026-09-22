package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WitchsCauldronItem extends Item {
    public WitchsCauldronItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);

        boolean isLitCampfire = clickedState.getBlock() instanceof CampfireBlock
                && clickedState.getValue(CampfireBlock.LIT);

        if (!isLitCampfire) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            level.setBlock(pos, ModBlocks.WITCHS_CAULDRON.get().defaultBlockState(), 3);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);

            Player player = context.getPlayer();
            if (player != null && !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }
}