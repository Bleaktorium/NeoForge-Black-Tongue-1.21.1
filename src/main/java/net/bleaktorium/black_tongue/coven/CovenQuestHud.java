package net.bleaktorium.black_tongue.coven;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.Optional;

@EventBusSubscriber(modid = "black_tongue", value = Dist.CLIENT)
public class CovenQuestHud {

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        // Only draw while the player's own inventory is actually open —
        // not crafting tables, not chests, not Yaga's own dialog screen.
        if (!(event.getScreen() instanceof InventoryScreen)) return;
        if (!ClientCovenQuestState.isActive()) return;

        var graphics = event.getGuiGraphics();
        var font = Minecraft.getInstance().font;

        int x = 6;
        int y = 6;

        graphics.drawString(font, Component.literal("Bring Yaga one of:"), x, y, 0xFFD8C8, true);
        y += 10;

        for (ResourceLocation id : ClientCovenQuestState.getAssignedPotions()) {
            Component name = potionDisplayName(id);
            graphics.drawString(font, Component.literal("- ").append(name), x, y, 0xE0E0E0, true);
            y += 10;
        }
    }

    private static Component potionDisplayName(ResourceLocation id) {
        Optional<Potion> potion = BuiltInRegistries.POTION.getOptional(id);
        if (potion.isEmpty()) return Component.literal(id.toString());

        ItemStack display = new ItemStack(Items.POTION);
        display.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion.get())));
        return display.getHoverName();
    }
}