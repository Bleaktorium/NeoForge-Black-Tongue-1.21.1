package net.bleaktorium.black_tongue.item;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.item.custom.*;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Black_Tongue.MOD_ID);


    // RITUAL =================================================================================================
    public static final DeferredItem<RunicEtchingTool> RUNIC_ETCHING_TOOL = ITEMS.registerItem("runic_etching_tool",
            RunicEtchingTool::new, new Item.Properties().durability(64));

    public static final DeferredItem<BlockItem> RITUAL_TABLE_ITEM = ITEMS.registerSimpleBlockItem("ritual_table", ModBlocks.RITUAL_TABLE);
    public static final DeferredItem<BlockItem> RUNIC_STONE_ITEM = ITEMS.registerSimpleBlockItem("runic_stone", ModBlocks.RUNIC_STONE);
    public static final DeferredItem<BlockItem> MOON_PHASE_RUNE_ITEM =
            ITEMS.registerSimpleBlockItem("moon_phase_rune", ModBlocks.MOON_PHASE_RUNE);

    // DEBUG ==================================================================================================
    public static final DeferredItem<Item> OFFERING_GIFT = ITEMS.registerItem("offering_gift",
            properties -> new Item(properties.food(
                    new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(0.3f)
                            .build()
            )),
            new Item.Properties());
    public static final DeferredItem<YagaMemoryWipeItem> DEBUG_YAGA_RESET = ITEMS.registerItem("debug_yaga_reset",
            YagaMemoryWipeItem::new,
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).build()));

    // COVEN ==================================================================================================
    public static final DeferredItem<CovenSummoningAmuletItem> YAGA_SUMMONING_AMULET = ITEMS.registerItem(
            "yaga_summoning_amulet", CovenSummoningAmuletItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<BlockItem> COVEN_THRONE = ITEMS.registerSimpleBlockItem(
            "coven_throne", ModBlocks.COVEN_THRONE);
    public static final DeferredItem<CovenSummoningAmuletItem> COVEN_SUMMONING_AMULET = ITEMS.registerItem(
            "coven_summoning_amulet", CovenSummoningAmuletItem::new, new Item.Properties().stacksTo(1));

    // CAULDRON ===============================================================================================
    public static final DeferredItem<WitchsCauldronItem> WITCHS_CAULDRON_ITEM =
            ITEMS.registerItem("witchs_cauldron", WitchsCauldronItem::new, new Item.Properties());

    public static final DeferredItem<CauldronScrubItem> CAULDRON_SCRUB =
            ITEMS.registerItem("cauldron_scrub", CauldronScrubItem::new, new Item.Properties().durability(16));
    public static final DeferredItem<CauldronTerminatorItem> CAULDRON_TERMINATOR =
            ITEMS.registerItem("cauldron_terminator", CauldronTerminatorItem::new, new Item.Properties());

    // ALCHEMY ================================================================================================
    public static final DeferredItem<BlockItem> SCRYER = ITEMS.registerSimpleBlockItem(
            "scryer", ModBlocks.SCRYER);

    // FARMING ================================================================================================
    public static final DeferredItem<BlockItem> MORTAR_PESTLE = ITEMS.registerSimpleBlockItem(
            "mortar_pestle", ModBlocks.MORTAR_PESTLE);
    public static final DeferredItem<BlockItem> DRYING_RACK = ITEMS.registerSimpleBlockItem(
            "drying_rack", ModBlocks.DRYING_RACK);


    // SEEDS
    public static final DeferredItem<BlockItem> MOTHLEAF_SEEDS =
            ITEMS.registerSimpleBlockItem("mothleaf_seeds", ModBlocks.MOTHLEAF_CROP);
    public static final DeferredItem<BlockItem> DEVILSTHORN_SEEDS =
            ITEMS.registerSimpleBlockItem("devilsthorn_seeds", ModBlocks.DEVILSTHORN_CROP);
    public static final DeferredItem<BlockItem> PAGANKA_ROOT_SEEDS =
            ITEMS.registerSimpleBlockItem("paganka_root_seeds", ModBlocks.PAGANKA_ROOT);

    // PLANTS
    public static final DeferredItem<Item> MOTHLEAF = ITEMS.registerSimpleItem("mothleaf", new Item.Properties());
    public static final DeferredItem<Item> MOTHLEAF_DRY = ITEMS.registerSimpleItem("mothleaf_dry", new Item.Properties());

    public static final DeferredItem<Item> DEVILSTHORN = ITEMS.registerSimpleItem("devilsthorn", new Item.Properties());
    public static final DeferredItem<Item> DEVILSTHORN_DRIED1 = ITEMS.registerSimpleItem("devilsthorn_dried1", new Item.Properties());
    public static final DeferredItem<Item> DEVILSTHORN_DRIED2 = ITEMS.registerSimpleItem("devilsthorn_dried2", new Item.Properties());

    public static final DeferredItem<Item> PAGANKA_ROOT = ITEMS.registerSimpleItem("paganka_root", new Item.Properties());

    // GRINDED MATS
    public static final DeferredItem<Item> MOTHLEAF_DUST = ITEMS.registerSimpleItem("mothleaf_dust", new Item.Properties());

}