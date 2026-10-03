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
                    .icon(() -> new ItemStack(ModBlocks.MORTAR_PESTLE.get().asItem()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RUNIC_ETCHING_TOOL.get());
                        output.accept(ModItems.RITUAL_TABLE_ITEM.get());
                        output.accept(ModItems.RUNIC_STONE_ITEM.get());
                        output.accept(ModItems.WITCHS_CAULDRON_ITEM.get());
                        output.accept(ModItems.CAULDRON_SCRUB.get());
                        output.accept(ModItems.CAULDRON_TERMINATOR.get());
                        output.accept(ModItems.DEBUG_YAGA_RESET.get());
                        output.accept(ModItems.YAGA_SUMMONING_AMULET.get());
                        output.accept(ModItems.COVEN_THRONE.get());
                        output.accept(ModItems.COVEN_SUMMONING_AMULET.get());
                        output.accept(ModItems.MOON_PHASE_RUNE_ITEM.get());
                        output.accept(ModItems.MORTAR_PESTLE.get());
                        output.accept(ModItems.MOTHLEAF.get());
                        output.accept(ModItems.MOTHLEAF_DRY.get());
                        output.accept(ModItems.MOTHLEAF_SEEDS.get());
                        output.accept(ModItems.MOTHLEAF_DUST.get());
                        output.accept(ModItems.SCRYER.get());
                        output.accept(ModItems.DRYING_RACK.get());
                        output.accept(ModItems.DEVILSTHORN.get());
                        output.accept(ModItems.DEVILSTHORN_DRIED1.get());
                        output.accept(ModItems.DEVILSTHORN_DRIED2.get());
                        output.accept(ModItems.DEVILSTHORN_SEEDS.get());
                        output.accept(ModItems.PAGANKA_ROOT_SEEDS.get());
                        output.accept(ModItems.PAGANKA_ROOT.get());
                        output.accept(ModItems.ANCESTRAL_PILLAR.get());
                        output.accept(ModItems.ANCESTRAL_REMAINS.get());
                        output.accept(ModItems.WITCH_REMAINS.get());
                        output.accept(ModItems.ANCIENT_DUST.get());
                        output.accept(ModItems.EMBALMING_TABLE.get());
                        output.accept(ModItems.BAT_OIL.get());
                        output.accept(ModItems.SPIDER_OIL.get());
                        output.accept(ModItems.DRYAD_OIL.get());
                    })
                    .build());
}