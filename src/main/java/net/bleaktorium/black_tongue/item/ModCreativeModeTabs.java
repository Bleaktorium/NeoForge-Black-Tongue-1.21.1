package net.bleaktorium.black_tongue.item;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Black_Tongue.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLACK_TONGUE_TAB =
            CREATIVE_MODE_TABS.register("black_tongue_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.black_tongue"))
                    .icon(() -> new ItemStack(ModBlocks.RITUAL_TABLE.get().asItem()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RUNIC_ETCHING_TOOL.get());
                        output.accept(ModItems.RITUAL_TABLE_ITEM.get());
                        output.accept(ModItems.RUNIC_STONE_ITEM.get());
                        output.accept(ModItems.WITCHS_CAULDRON_ITEM.get());
                    })
                    .build());
}