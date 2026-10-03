package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Black_Tongue.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RitualTableBlockEntity>> RITUAL_TABLE_BE =
            BLOCK_ENTITIES.register("ritual_table_be",
                    () -> BlockEntityType.Builder.of(RitualTableBlockEntity::new, ModBlocks.RITUAL_TABLE.get()).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunicStoneBlockEntity>> RUNIC_STONE_BE =
            BLOCK_ENTITIES.register("runic_stone_be",
                    () -> BlockEntityType.Builder.of(RunicStoneBlockEntity::new,
                            ModBlocks.RUNIC_STONE.get(), ModBlocks.MOON_PHASE_RUNE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WitchsCauldronBlockEntity>> WITCHS_CAULDRON_BE =
            BLOCK_ENTITIES.register("witchs_cauldron_be",
                    () -> BlockEntityType.Builder.of(WitchsCauldronBlockEntity::new, ModBlocks.WITCHS_CAULDRON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MortarAndPestleBlockEntity>> MORTAR_AND_PESTLE_BE =
            BLOCK_ENTITIES.register("mortar_and_pestle_be",
                    () -> BlockEntityType.Builder.of(MortarAndPestleBlockEntity::new, ModBlocks.MORTAR_PESTLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScryerBlockEntity>> SCRYER_BE =
            BLOCK_ENTITIES.register("scryer_be",
                    () -> BlockEntityType.Builder.of(ScryerBlockEntity::new, ModBlocks.SCRYER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DryingRackBlockEntity>> DRYING_RACK_BE =
            BLOCK_ENTITIES.register("drying_rack_be",
                    () -> BlockEntityType.Builder.of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PagankaRootCropBlockEntity>> PAGANKA_ROOT_CROP_BE =
            BLOCK_ENTITIES.register("paganka_root_crop_be",
                    () -> BlockEntityType.Builder.of(PagankaRootCropBlockEntity::new, ModBlocks.PAGANKA_ROOT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AncestralPillarBlockEntity>> ANCESTRAL_PILLAR_BE =
            BLOCK_ENTITIES.register("ancestral_pillar_be",
                    () -> BlockEntityType.Builder.of(AncestralPillarBlockEntity::new, ModBlocks.ANCESTRAL_PILLAR.get()).build(null));

}