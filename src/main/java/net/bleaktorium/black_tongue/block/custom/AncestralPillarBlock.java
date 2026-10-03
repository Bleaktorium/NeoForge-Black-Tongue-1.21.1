package net.bleaktorium.black_tongue.block.custom;

import com.mojang.serialization.MapCodec;
import net.bleaktorium.black_tongue.block.entity.AncestralPillarBlockEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class AncestralPillarBlock extends BaseEntityBlock {
    public static final MapCodec<AncestralPillarBlock> CODEC = simpleCodec(AncestralPillarBlock::new);

    public AncestralPillarBlock(BlockBehaviour.Properties properties) {
        super(properties); }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AncestralPillarBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    // TEMPORARY test hook until the binding ritual exists (milestone 5):
    // creative only, bone = coven witch, skeleton skull = ancestor, sneak + empty hand = clear
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.isCreative() || !(level.getBlockEntity(pos) instanceof AncestralPillarBlockEntity pillar)) {
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
                && level.getBlockEntity(pos) instanceof AncestralPillarBlockEntity pillar) {
            if (!level.isClientSide) pillar.clear();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}