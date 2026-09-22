package net.bleaktorium.black_tongue.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RitualTableBlockEntity extends BlockEntity {
    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_TABLE_BE.get(), pos, state);
    }
}