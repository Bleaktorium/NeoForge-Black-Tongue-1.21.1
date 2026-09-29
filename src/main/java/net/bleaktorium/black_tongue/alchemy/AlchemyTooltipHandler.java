package net.bleaktorium.black_tongue.alchemy;

import com.mojang.datafixers.util.Either;
import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.cauldron.CauldronIngredientData;
import net.bleaktorium.black_tongue.cauldron.CauldronIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID, value = Dist.CLIENT)
public class AlchemyTooltipHandler {

    @SubscribeEvent
    public static void onGatherTooltip(RenderTooltipEvent.GatherComponents event) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (!ClientAlchemyKnowledge.knows(itemId)) return;

        CauldronIngredientData data = CauldronIngredients.get(event.getItemStack().getItem());
        if (data == null) return;

        Component line = Component.literal("Cauldron reading: " + temperatureLabel(data.temperatureValue()))
                .withStyle(ChatFormatting.LIGHT_PURPLE);
        event.getTooltipElements().add(Either.left(line));
    }

    private static String temperatureLabel(int temp) {
        return switch (Math.max(-3, Math.min(3, temp))) {
            case -3 -> "Frozen";
            case -2 -> "Ice";
            case -1 -> "Cold";
            case 0 -> "Lukewarm";
            case 1 -> "Warm";
            case 2 -> "Hot";
            default -> "Molten"; // 3
        };
    }
}
