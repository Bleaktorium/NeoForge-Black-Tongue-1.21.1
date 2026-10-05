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
                        output.accept(ModItems.RUNIC_ETCHING_TOOL.get());  //tool
                        output.accept(ModItems.RITUAL_TABLE_ITEM.get());  //station
                        output.accept(ModItems.RUNIC_STONE_ITEM.get());  //object
                        output.accept(ModItems.WITCHS_CAULDRON_ITEM.get());  //station
                        output.accept(ModItems.CAULDRON_SCRUB.get()); //tool
                        output.accept(ModItems.CAULDRON_TERMINATOR.get()); //item
                        output.accept(ModItems.DEBUG_YAGA_RESET.get()); //debug
                        output.accept(ModItems.YAGA_SUMMONING_AMULET.get()); //item
                        output.accept(ModItems.COVEN_THRONE.get()); //object
                        output.accept(ModItems.COVEN_SUMMONING_AMULET.get()); //item
                        output.accept(ModItems.MOON_PHASE_RUNE_ITEM.get()); //object
                        output.accept(ModItems.MORTAR_PESTLE.get()); //station
                        output.accept(ModItems.MOTHLEAF.get()); //plant
                        output.accept(ModItems.MOTHLEAF_DRY.get()); //dry plant
                        output.accept(ModItems.MOTHLEAF_SEEDS.get()); //seed
                        output.accept(ModItems.MOTHLEAF_DUST.get()); //grinded plant
                        output.accept(ModItems.SCRYER.get()); //station
                        output.accept(ModItems.DRYING_RACK.get()); //station
                        output.accept(ModItems.DEVILSTHORN.get()); //plant
                        output.accept(ModItems.DEVILSTHORN_DRIED1.get()); //plant dry
                        output.accept(ModItems.DEVILSTHORN_DRIED2.get()); //plant dry
                        output.accept(ModItems.DEVILSTHORN_SEEDS.get()); //seed
                        output.accept(ModItems.PAGANKA_ROOT_SEEDS.get()); //seed
                        output.accept(ModItems.PAGANKA_ROOT.get()); //plant
                        output.accept(ModItems.ANCESTRAL_PILLAR.get()); //object
                        output.accept(ModItems.ANCESTRAL_REMAINS.get()); //item
                        output.accept(ModItems.WITCH_REMAINS.get()); //item
                        output.accept(ModItems.ANCIENT_DUST.get()); //item
                        output.accept(ModItems.EMBALMING_TABLE.get()); //station
                        output.accept(ModItems.BAT_OIL.get()); //item
                        output.accept(ModItems.SPIDER_OIL.get()); //item
                        output.accept(ModItems.DRYAD_OIL.get()); //item
                        output.accept(ModItems.ANCESTOR_WRAP.get()); //item
                        output.accept(ModItems.DEVILS_COTTON_SEEDS.get()); //seed
                        output.accept(ModItems.SPIDER_SILK_SEEDS.get()); //seed
                        output.accept(ModItems.BANSHEES_WISP.get()); //material
                        output.accept(ModItems.SPIDER_SILK_THREAD.get()); //thread
                        output.accept(ModItems.DEVILS_COTTON_THREAD.get()); //thread
                        output.accept(ModItems.BANSHEES_WISP_THREAD.get()); //thread
                        output.accept(ModItems.WOOL_THREAD.get()); //thread
                        output.accept(ModItems.SPIDER_SILK_FABRIC.get()); //fabric
                        output.accept(ModItems.DEVILS_COTTON_FABRIC.get()); //fabric
                        output.accept(ModItems.BANSHEES_WISP_FABRIC.get()); //fabric
                        output.accept(ModItems.SILK_FABRIC_ROLL.get()); //roll
                        output.accept(ModItems.COTTON_FABRIC_ROLL.get()); //roll
                        output.accept(ModItems.WISP_FABRIC_ROLL.get()); //roll
                        output.accept(ModItems.HELMET_LINING.get()); //lining
                        output.accept(ModItems.CHESTPLATE_LINING.get()); //lining
                        output.accept(ModItems.LEGGINGS_LINING.get()); //lining
                        output.accept(ModItems.BOOTS_LINING.get()); //lining
                        output.accept(ModItems.SEED_OIL.get()); //material
                        output.accept(ModItems.HAUNTING_GLASS.get()); //material
                        output.accept(ModItems.INFUSED_LEATHER.get()); //infused
                        output.accept(ModItems.INFUSED_IRON_INGOT.get()); //infused
                        output.accept(ModItems.INFUSED_DIAMOND.get()); //infused
                        output.accept(ModItems.GOLDEN_COMB.get()); //tool
                        output.accept(ModItems.SPINNING_WHEEL.get()); //station
                        output.accept(ModItems.HAUNTING_SOUL_SPAWN_EGG.get()); //mob
                        output.accept(ModItems.COVENLESS_WITCH_SPAWN_EGG.get()); //mob
                        output.accept(ModItems.COVEN_HUT_SPAWN_EGG.get()); //mob
                        output.accept(ModItems.COVEN_MOTHER_SPAWN_EGG.get()); //mob
                        output.accept(ModItems.BANSHEE_SPAWN_EGG.get()); //mob

                    })
                    .build());
}