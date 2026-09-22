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
                    () -> BlockEntityType.Builder.of(RitualTableBlockEntity::new, ModBlocks.RITUAL_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RunicStoneBlockEntity>> RUNIC_STONE_BE =
            BLOCK_ENTITIES.register("runic_stone_be",
                    () -> BlockEntityType.Builder.of(RunicStoneBlockEntity::new, ModBlocks.RUNIC_STONE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WitchsCauldronBlockEntity>> WITCHS_CAULDRON_BE =
            BLOCK_ENTITIES.register("witchs_cauldron_be",
                    () -> BlockEntityType.Builder.of(WitchsCauldronBlockEntity::new, ModBlocks.WITCHS_CAULDRON.get()).build(null));
}