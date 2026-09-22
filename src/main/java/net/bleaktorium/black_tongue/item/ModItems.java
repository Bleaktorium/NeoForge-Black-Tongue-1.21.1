package net.bleaktorium.black_tongue.item;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.item.custom.RunicEtchingTool;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Black_Tongue.MOD_ID);

    public static final DeferredItem<RunicEtchingTool> RUNIC_ETCHING_TOOL = ITEMS.registerItem("runic_etching_tool",
            RunicEtchingTool::new, new Item.Properties().durability(64));

    public static final DeferredItem<BlockItem> RITUAL_TABLE_ITEM = ITEMS.registerSimpleBlockItem("ritual_table", ModBlocks.RITUAL_TABLE);
    public static final DeferredItem<BlockItem> RUNIC_STONE_ITEM = ITEMS.registerSimpleBlockItem("runic_stone", ModBlocks.RUNIC_STONE);

    public static final DeferredItem<Item> OFFERING_GIFT = ITEMS.registerItem("offering_gift",
            properties -> new Item(properties.food(
                    new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(0.3f)
                            .build()
            )),
            new Item.Properties());

    public static final DeferredItem<BlockItem> WITCHS_CAULDRON_ITEM = ITEMS.registerSimpleBlockItem("witchs_cauldron", ModBlocks.WITCHS_CAULDRON);
}