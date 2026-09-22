package net.bleaktorium.black_tongue.block;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.custom.RitualTableBlock;
import net.bleaktorium.black_tongue.block.custom.RunicStoneBlock;
import net.bleaktorium.black_tongue.block.custom.WitchsCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Black_Tongue.MOD_ID);

    public static final DeferredBlock<RitualTableBlock> RITUAL_TABLE = BLOCKS.register("ritual_table",
            () -> new RitualTableBlock(BlockBehaviour.Properties.of().strength(3.5f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<RunicStoneBlock> RUNIC_STONE = BLOCKS.register("runic_stone",
            () -> new RunicStoneBlock(BlockBehaviour.Properties.of().strength(2.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<WitchsCauldronBlock> WITCHS_CAULDRON = BLOCKS.register("witchs_cauldron",
            () -> new WitchsCauldronBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops()));
}