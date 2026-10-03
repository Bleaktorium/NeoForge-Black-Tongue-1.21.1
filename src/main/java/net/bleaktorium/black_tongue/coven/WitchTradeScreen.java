package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.entity.custom.WitchIdentity;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentityPool;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WitchTradeScreen extends AbstractContainerScreen<WitchTradeMenu> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/trade/coven_trade_screen.png");
    private static final ResourceLocation ARROW =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/trade/coven_trade_arrow.png");

    public WitchTradeScreen(WitchTradeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 276;
        this.imageHeight = 214;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {

    }

    private int lastSeenReputation = -1;
    private long flairTextUntil = 0;

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int currentRep = menu.getReputation();
        if (lastSeenReputation != -1 && currentRep > lastSeenReputation) {
            flairTextUntil = System.currentTimeMillis() + 3000;
        }
        lastSeenReputation = currentRep;

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderPortrait(graphics, x, y);
        renderReputation(graphics, x, y);
        renderName(graphics, x, y);
        renderTextBox(graphics, x, y);
        renderArrowIfActive(graphics, x, y);
        renderOfferList(graphics, x, y);
        renderOutputSlotIcon(graphics, x, y);
        renderOutputSlotHighlight(graphics, x, y, mouseX, mouseY);
    }

    private void renderOutputSlotHighlight(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        ItemStack output = menu.slots.get(2).getItem();
        if (output.isEmpty()) return;

        int slotX = x + 216, slotY = y + 81;
        boolean hovering = mouseX >= slotX && mouseX < slotX + 23 && mouseY >= slotY && mouseY < slotY + 23;
        if (!hovering) return;

        graphics.fill(slotX, slotY, slotX + 24, slotY + 24, 0x80FFFFFF);
    }

    private void renderPortrait(GuiGraphics graphics, int x, int y) {
        WitchIdentity identity = WitchIdentityPool.getByName(menu.getWitchName());
        graphics.blit(identity.portrait(), x + 4, y + 4, 0, 0, 94, 78, 94, 78);
    }

    private void renderReputation(GuiGraphics graphics, int x, int y) {
        graphics.drawString(font, "Rep: " + menu.getReputation(), x + 8, y + 70, 0xFFFFFF, true);
    }

    private void renderName(GuiGraphics graphics, int x, int y) {
        graphics.drawString(font, menu.getWitchName(), x + 109, y + 9, 0xE8B84B, true);
    }

    private void renderTextBox(GuiGraphics graphics, int x, int y) {
        String text = System.currentTimeMillis() < flairTextUntil
                ? "Sure, want anything else?"
                : "What would you like to trade?";
        graphics.drawWordWrap(font, Component.literal(text), x + 109, y + 24, 152, 0xCEC7BA);
    }

    private void renderArrowIfActive(GuiGraphics graphics, int x, int y) {
        if (!menu.hasActiveOffer()) return;
        graphics.blit(ARROW, x + 186, y + 86, 0, 0, 22, 15, 22, 15);
    }

    private void renderOfferList(GuiGraphics graphics, int x, int y) {
        List<WitchTradeOffer> offers = WitchTradePool.getAvailableOffers(menu.getWitchName(), menu.getReputation());
        int listX = x + 8;
        int listY = y + 96;
        int rowHeight = 18;

        for (WitchTradeOffer offer : offers) {
            List<ItemStack> inputs = offer.inputs();

            graphics.renderItem(inputs.get(0), listX, listY);
            graphics.renderItemDecorations(font, inputs.get(0), listX, listY);

            if (inputs.size() > 1) {
                graphics.pose().pushPose();
                graphics.pose().translate(listX + 10, listY + 8, 200);
                graphics.pose().scale(0.6f, 0.6f, 1f);
                graphics.renderItem(inputs.get(1), 0, 0);
                graphics.renderItemDecorations(font, inputs.get(1), 0, 0);
                graphics.pose().popPose();
            }

            graphics.drawString(font, "\u2192", listX + 20, listY + 4, 0xCEC7BA, false);

            ItemStack resultStack = offer.resolveOutput(menu.getWitchName(), null);
            graphics.renderItem(resultStack, listX + 34, listY);
            graphics.renderItemDecorations(font, resultStack, listX + 34, listY);

            listY += rowHeight;
        }
    }

    @Override
    protected void renderSlot(GuiGraphics graphics, Slot slot) {
        if (slot == menu.slots.get(2)) return;
        super.renderSlot(graphics, slot);
    }

    private void renderOutputSlotIcon(GuiGraphics graphics, int x, int y) {
        ItemStack output = menu.slots.get(2).getItem();
        if (output.isEmpty()) return;

        int slotX = x + 216, slotY = y + 81;
        float scale = 23f / 16f;

        graphics.pose().pushPose();
        graphics.pose().translate(slotX, slotY, 200);
        graphics.pose().scale(scale, scale, 1f);
        graphics.renderItem(output, 0, 0);
        graphics.pose().popPose();

        graphics.renderItemDecorations(font, output, slotX, slotY);
    }
}