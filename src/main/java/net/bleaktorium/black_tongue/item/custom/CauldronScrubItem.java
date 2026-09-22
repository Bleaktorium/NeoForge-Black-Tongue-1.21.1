package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.block.entity.WitchsCauldronBlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CauldronScrubItem extends Item {
    private static final int USE_DURATION_TICKS = 3 * 20; // 3 seconds

    public CauldronScrubItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return stack;

        HitResult hit = player.pick(5.0, 0.0f, false);

        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel
                && hit instanceof BlockHitResult blockHit) {
            if (level.getBlockEntity(blockHit.getBlockPos()) instanceof WitchsCauldronBlockEntity cauldron) {
                cauldron.scrub();
                level.playSound(null, blockHit.getBlockPos(), SoundEvents.MUD_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }

        return stack;
    }
}