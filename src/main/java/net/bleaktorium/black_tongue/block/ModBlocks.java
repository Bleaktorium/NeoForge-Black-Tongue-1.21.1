package net.bleaktorium.black_tongue.block;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.custom.*;
import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Black_Tongue.MOD_ID);

    public static final DeferredBlock<RitualTableBlock> RITUAL_TABLE = BLOCKS.register("ritual_table",
            () -> new RitualTableBlock(BlockBehaviour.Properties.of().strength(3.0f).noOcclusion()));

    public static final DeferredBlock<RunicStoneBlock> RUNIC_STONE = BLOCKS.register("runic_stone",
            () -> new RunicStoneBlock(BlockBehaviour.Properties.of().strength(2.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<WitchsCauldronBlock> WITCHS_CAULDRON = BLOCKS.register("witchs_cauldron",
            () -> new WitchsCauldronBlock(BlockBehaviour.Properties.of().strength(3.0f).noOcclusion()));

    public static final DeferredBlock<Block> COVEN_THRONE = BLOCKS.register("coven_throne",
            () -> new Block(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<MoonPhaseRuneBlock> MOON_PHASE_RUNE = BLOCKS.register("moon_phase_rune",
            () -> new MoonPhaseRuneBlock(BlockBehaviour.Properties.of().strength(2.0f)));

    public static final DeferredBlock<MortarAndPestleBlock> MORTAR_PESTLE = BLOCKS.register("mortar_pestle",
            () -> new MortarAndPestleBlock(BlockBehaviour.Properties.of().strength(3.0f).noOcclusion()));

    public static final DeferredBlock<HerbCropBlock> MOTHLEAF_CROP = BLOCKS.register("mothleaf_crop",
            () -> new HerbCropBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.CROP)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY),
                    3, ModItems.MOTHLEAF_SEEDS));

    public static final DeferredBlock<HerbCropBlock> DEVILSTHORN_CROP = BLOCKS.register("devilsthorn_crop",
            () -> new HerbCropBlock(BlockBehaviour.Properties.of()
                    .noCollission().randomTicks().instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.CROP)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY),
                    3, ModItems.DEVILSTHORN_SEEDS));

    public static final DeferredBlock<ScryerBlock> SCRYER = BLOCKS.register("scryer",
            () -> new ScryerBlock(BlockBehaviour.Properties.of().strength(3.0f).noOcclusion()));

    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = BLOCKS.register("drying_rack",
            () -> new DryingRackBlock(BlockBehaviour.Properties.of().strength(3.0f).noOcclusion()));

    public static final DeferredBlock<PagankaRootCropBlock> PAGANKA_ROOT = BLOCKS.register("paganka_root",
            () -> new PagankaRootCropBlock(BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion()
                    .noCollission()
                    .randomTicks(),
                    5, ModItems.PAGANKA_ROOT_SEEDS));

    public static final DeferredBlock<AncestralPillarBlock> ANCESTRAL_PILLAR = BLOCKS.register("ancestral_pillar",
            () -> new AncestralPillarBlock(BlockBehaviour.Properties.of().strength(4.0f).noOcclusion()));
    public static final DeferredBlock<EmbalmingTableBlock> EMBALMING_TABLE = BLOCKS.register("embalming_table",
            () -> new EmbalmingTableBlock(BlockBehaviour.Properties.of().strength(2.5f).noOcclusion()
                    .lightLevel(state -> 12)));

    public static final DeferredBlock<SpiderGrassCropBlock> SPIDER_SILK_CROP = BLOCKS.register("spider_silk_crop",
            () -> new SpiderGrassCropBlock(BlockBehaviour.Properties.of()
                    .noCollission().randomTicks().instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.CROP)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY),
                    ModItems.SPIDER_SILK_SEEDS));

    public static final DeferredBlock<DevilsCottonCropBlock> DEVILS_COTTON_CROP = BLOCKS.register("devils_cotton_crop",
            () -> new DevilsCottonCropBlock(BlockBehaviour.Properties.of()
                    .noCollission().randomTicks().instabreak()
                    .sound(net.minecraft.world.level.block.SoundType.CROP)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY),
                    ModItems.DEVILS_COTTON_SEEDS));

    public static final DeferredBlock<SpinningWheelBlock> SPINNING_WHEEL = BLOCKS.register("spinning_wheel",
            () -> new SpinningWheelBlock(BlockBehaviour.Properties.of().strength(2.0f).noOcclusion()
                    .sound(net.minecraft.world.level.block.SoundType.WOOD)));

    public static final DeferredBlock<WitchShelfBlock> WITCH_SHELF = BLOCKS.register("witch_shelf",
            () -> new WitchShelfBlock(BlockBehaviour.Properties.of().strength(2.5f).noOcclusion().sound(SoundType.WOOD)));
}