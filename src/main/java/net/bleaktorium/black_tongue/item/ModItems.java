package net.bleaktorium.black_tongue.item;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.entity.ModEntities;
import net.bleaktorium.black_tongue.item.custom.*;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
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
    public static final DeferredItem<ItemNameBlockItem> SPIDER_SILK_SEEDS =
            ITEMS.registerItem("spider_silk_seeds",
            props -> new ItemNameBlockItem(ModBlocks.SPIDER_SILK_CROP.get(), props));
    public static final DeferredItem<ItemNameBlockItem> DEVILS_COTTON_SEEDS =
            ITEMS.registerItem("devils_cotton_seeds",
            props -> new ItemNameBlockItem(ModBlocks.DEVILS_COTTON_CROP.get(), props));

    // PLANTS
    public static final DeferredItem<Item> MOTHLEAF = ITEMS.registerSimpleItem("mothleaf", new Item.Properties());
    public static final DeferredItem<Item> MOTHLEAF_DRY = ITEMS.registerSimpleItem("mothleaf_dry", new Item.Properties());

    public static final DeferredItem<Item> DEVILSTHORN = ITEMS.registerSimpleItem("devilsthorn", new Item.Properties());
    public static final DeferredItem<Item> DEVILSTHORN_DRIED1 = ITEMS.registerSimpleItem("devilsthorn_dried1", new Item.Properties());
    public static final DeferredItem<Item> DEVILSTHORN_DRIED2 = ITEMS.registerSimpleItem("devilsthorn_dried2", new Item.Properties());

    public static final DeferredItem<Item> PAGANKA_ROOT = ITEMS.registerSimpleItem("paganka_root", new Item.Properties());

    public static final DeferredItem<Item> SPIDER_SILK = ITEMS.registerSimpleItem("spider_silk", new Item.Properties());

    public static final DeferredItem<Item> DEVILS_COTTON = ITEMS.registerSimpleItem("devils_cotton", new Item.Properties());

    // GRINDED MATS
    public static final DeferredItem<Item> MOTHLEAF_DUST = ITEMS.registerSimpleItem("mothleaf_dust", new Item.Properties());

    // ANCESTORS
    public static final DeferredItem<BlockItem> ANCESTRAL_PILLAR = ITEMS.registerSimpleBlockItem(
            "ancestral_pillar", ModBlocks.ANCESTRAL_PILLAR);
    public static final DeferredItem<BlockItem> EMBALMING_TABLE = ITEMS.registerSimpleBlockItem(
            "embalming_table", ModBlocks.EMBALMING_TABLE);

    public static final DeferredItem<OilItem> SPIDER_OIL = ITEMS.registerItem("spider_oil",
            props -> new OilItem(RemainsData.DecayTier.FRESH, props), new Item.Properties().stacksTo(16));
    public static final DeferredItem<OilItem> BAT_OIL = ITEMS.registerItem("bat_oil",
            props -> new OilItem(RemainsData.DecayTier.ROTTEN, props), new Item.Properties().stacksTo(16));
    public static final DeferredItem<OilItem> DRYAD_OIL = ITEMS.registerItem("dryad_oil",
            props -> new OilItem(RemainsData.DecayTier.CRUMBLING, props), new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> ANCESTOR_WRAP = ITEMS.registerSimpleItem("ancestor_wrap",
            new Item.Properties().stacksTo(16));

    public static final DeferredItem<RemainsItem> ANCESTRAL_REMAINS = ITEMS.registerItem("ancestral_remains",
            props -> new RemainsItem(RemainsData.Origin.ANCESTOR, props), new Item.Properties().stacksTo(1));
    public static final DeferredItem<RemainsItem> WITCH_REMAINS = ITEMS.registerItem("witch_remains",
            props -> new RemainsItem(RemainsData.Origin.WITCH, props), new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ANCIENT_DUST = ITEMS.registerSimpleItem("ancient_dust", new Item.Properties());
    public static final DeferredItem<ConsecratedRemainsItem> CONSECRATED_REMAINS = ITEMS.registerItem("consecrated_remains",
            ConsecratedRemainsItem::new, new Item.Properties().stacksTo(1));

    // ARMOR CRAFTING
    public static final DeferredItem<Item> BANSHEES_WISP = ITEMS.registerSimpleItem("banshees_wisp", new Item.Properties());
    public static final DeferredItem<Item> SPIDER_SILK_THREAD = ITEMS.registerSimpleItem("spider_silk_thread", new Item.Properties());
    public static final DeferredItem<Item> DEVILS_COTTON_THREAD = ITEMS.registerSimpleItem("devils_cotton_thread", new Item.Properties());
    public static final DeferredItem<Item> BANSHEES_WISP_THREAD = ITEMS.registerSimpleItem("banshees_wisp_thread", new Item.Properties());
    public static final DeferredItem<Item> WOOL_THREAD = ITEMS.registerSimpleItem("wool_thread", new Item.Properties());
    public static final DeferredItem<Item> SPIDER_SILK_FABRIC = ITEMS.registerSimpleItem("spider_silk_fabric", new Item.Properties());
    public static final DeferredItem<Item> DEVILS_COTTON_FABRIC = ITEMS.registerSimpleItem("devils_cotton_fabric", new Item.Properties());
    public static final DeferredItem<Item> BANSHEES_WISP_FABRIC = ITEMS.registerSimpleItem("banshees_wisp_fabric", new Item.Properties());
    public static final DeferredItem<Item> SILK_FABRIC_ROLL = ITEMS.registerSimpleItem("silk_fabric_roll", new Item.Properties());
    public static final DeferredItem<Item> COTTON_FABRIC_ROLL = ITEMS.registerSimpleItem("cotton_fabric_roll", new Item.Properties());
    public static final DeferredItem<Item> WISP_FABRIC_ROLL = ITEMS.registerSimpleItem("wisp_fabric_roll", new Item.Properties());
    public static final DeferredItem<Item> HELMET_LINING = ITEMS.registerSimpleItem("helmet_lining", new Item.Properties());
    public static final DeferredItem<Item> CHESTPLATE_LINING = ITEMS.registerSimpleItem("chestplate_lining", new Item.Properties());
    public static final DeferredItem<Item> LEGGINGS_LINING = ITEMS.registerSimpleItem("leggings_lining", new Item.Properties());
    public static final DeferredItem<Item> BOOTS_LINING = ITEMS.registerSimpleItem("boots_lining", new Item.Properties());
    public static final DeferredItem<Item> SEED_OIL = ITEMS.registerSimpleItem("seed_oil", new Item.Properties());
    public static final DeferredItem<Item> HAUNTING_GLASS = ITEMS.registerSimpleItem("haunting_glass", new Item.Properties());
    public static final DeferredItem<Item> GOLDEN_COMB = ITEMS.registerSimpleItem("golden_comb",
            new Item.Properties().durability(64));
    public static final DeferredItem<Item> INFUSED_LEATHER = ITEMS.registerSimpleItem("infused_leather",
            new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final DeferredItem<Item> INFUSED_IRON_INGOT = ITEMS.registerSimpleItem("infused_iron_ingot",
            new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final DeferredItem<Item> INFUSED_DIAMOND = ITEMS.registerSimpleItem("infused_diamond",
            new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final DeferredItem<BlockItem> SPINNING_WHEEL = ITEMS.registerSimpleBlockItem(
            "spinning_wheel", ModBlocks.SPINNING_WHEEL);

    // MOBS
    public static final DeferredItem<DeferredSpawnEggItem> HAUNTING_SOUL_SPAWN_EGG = ITEMS.registerItem("haunting_soul_spawn_egg",
            props -> new DeferredSpawnEggItem(ModEntities.HAUNTING_SOUL, 0x1B1B2F, 0x8FD3FF, props));
    public static final DeferredItem<DeferredSpawnEggItem> COVENLESS_WITCH_SPAWN_EGG = ITEMS.registerItem("covenless_witch_spawn_egg",
            props -> new DeferredSpawnEggItem(ModEntities.COVENLESS_WITCH, 0x3B2A4A, 0x7FA65A, props));
    public static final DeferredItem<DeferredSpawnEggItem> COVEN_HUT_SPAWN_EGG = ITEMS.registerItem("coven_hut_spawn_egg",
            props -> new DeferredSpawnEggItem(ModEntities.COVEN_HUT, 0x5C4030, 0x6B7F3A, props));
    public static final DeferredItem<DeferredSpawnEggItem> COVEN_MOTHER_SPAWN_EGG = ITEMS.registerItem("coven_mother_spawn_egg",
            props -> new DeferredSpawnEggItem(ModEntities.COVEN_MOTHER, 0x1E1A1A, 0xD8CFB8, props));
    public static final DeferredItem<DeferredSpawnEggItem> BANSHEE_SPAWN_EGG = ITEMS.registerItem("banshee_spawn_egg",
            props -> new DeferredSpawnEggItem(ModEntities.BANSHEE, 0xD9DEE3, 0x5E7FA3, props));

}