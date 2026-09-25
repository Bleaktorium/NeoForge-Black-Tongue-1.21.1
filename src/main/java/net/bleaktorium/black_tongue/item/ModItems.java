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


    // RITUAL
    public static final DeferredItem<RunicEtchingTool> RUNIC_ETCHING_TOOL = ITEMS.registerItem("runic_etching_tool",
            RunicEtchingTool::new, new Item.Properties().durability(64));

    public static final DeferredItem<BlockItem> RITUAL_TABLE_ITEM = ITEMS.registerSimpleBlockItem("ritual_table", ModBlocks.RITUAL_TABLE);
    public static final DeferredItem<BlockItem> RUNIC_STONE_ITEM = ITEMS.registerSimpleBlockItem("runic_stone", ModBlocks.RUNIC_STONE);

    // DEBUG
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

    // YAGA
    public static final DeferredItem<Item> YAGA_SUMMONING_AMULET = ITEMS.registerSimpleItem("yaga_summoning_amulet",
            new Item.Properties().stacksTo(1));

    // CAULDRON
    public static final DeferredItem<WitchsCauldronItem> WITCHS_CAULDRON_ITEM =
            ITEMS.registerItem("witchs_cauldron", WitchsCauldronItem::new, new Item.Properties());

    public static final DeferredItem<CauldronScrubItem> CAULDRON_SCRUB =
            ITEMS.registerItem("cauldron_scrub", CauldronScrubItem::new, new Item.Properties().durability(16));
    public static final DeferredItem<CauldronTerminatorItem> CAULDRON_TERMINATOR =
            ITEMS.registerItem("cauldron_terminator", CauldronTerminatorItem::new, new Item.Properties());
}