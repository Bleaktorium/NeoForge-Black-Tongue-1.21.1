package net.bleaktorium.black_tongue;

import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.block.entity.ModBlockEntities;
import net.bleaktorium.black_tongue.block.entity.client.*;
import net.bleaktorium.black_tongue.cauldron.CauldronIngredients;
import net.bleaktorium.black_tongue.cauldron.CauldronRecipes;
import net.bleaktorium.black_tongue.coven.ModAttachments;
import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.entity.ModEntities;
import net.bleaktorium.black_tongue.farming.HerbDryingProfiles;
import net.bleaktorium.black_tongue.farming.HerbGrindingRecipes;
import net.bleaktorium.black_tongue.item.ModCreativeModeTabs;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.item.menu.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.bleaktorium.black_tongue.entity.client.CovenMotherRenderer;
import net.bleaktorium.black_tongue.entity.client.CovenlessWitchRenderer;
import net.bleaktorium.black_tongue.coven.JournalTradeScreen;
import net.bleaktorium.black_tongue.coven.WitchTradeScreen;
import net.bleaktorium.black_tongue.entity.client.CovenHutRenderer;
import net.bleaktorium.black_tongue.entity.client.CovenMotherCatRenderer;
import net.bleaktorium.black_tongue.block.custom.DryingRackScreen;
import net.bleaktorium.black_tongue.entity.client.PagankaRootRenderer;


@Mod(Black_Tongue.MOD_ID)
public class Black_Tongue {

    public static final String MOD_ID = "black_tongue";

    public static final Logger LOGGER = LogUtils.getLogger();


    public Black_Tongue(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CauldronIngredients.bootstrap();
        CauldronRecipes.bootstrap();
        HerbGrindingRecipes.bootstrap();
        HerbDryingProfiles.bootstrap();
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.DEVILSTHORN_CROP.get(),
                        net.minecraft.client.renderer.RenderType.cutout());

                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                        ModBlocks.MOTHLEAF_CROP.get(),
                        net.minecraft.client.renderer.RenderType.cutout());
            });

        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.COVEN_MOTHER.get(), CovenMotherRenderer::new);
            event.registerEntityRenderer(ModEntities.COVENLESS_WITCH.get(), CovenlessWitchRenderer::new);
            event.registerEntityRenderer(ModEntities.COVEN_HUT.get(), CovenHutRenderer::new);
            event.registerEntityRenderer(ModEntities.COVEN_MOTHER_CAT.get(), CovenMotherCatRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.RITUAL_TABLE_BE.get(), context -> new RitualTableRenderer());
            event.registerBlockEntityRenderer(ModBlockEntities.WITCHS_CAULDRON_BE.get(), context -> new WitchsCauldronRenderer());
            event.registerBlockEntityRenderer(ModBlockEntities.MORTAR_AND_PESTLE_BE.get(), context -> new MortarAndPestleRenderer());
            event.registerBlockEntityRenderer(ModBlockEntities.SCRYER_BE.get(), context -> new ScryerRenderer());
            event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK_BE.get(), context -> new DryingRackRenderer());
            event.registerBlockEntityRenderer(ModBlockEntities.PAGANKA_ROOT_CROP_BE.get(), context -> new PagankaRootCropRenderer());
            event.registerEntityRenderer(ModEntities.PAGANKA_ROOT.get(), PagankaRootRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.ANCESTRAL_PILLAR_BE.get(), context -> new AncestralPillarRenderer());
        }

        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.JOURNAL_TRADE.get(), JournalTradeScreen::new);
            event.register(ModMenuTypes.WITCH_TRADE.get(), WitchTradeScreen::new);
            event.register(ModMenuTypes.DRYING_RACK.get(), DryingRackScreen::new);
        }
    }
}
