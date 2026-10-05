package net.bleaktorium.black_tongue.block.custom;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class WitchShelfScreen extends AbstractContainerScreen<WitchShelfMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/gui/witch_shelf/witch_shelf_gui.png");
    private static final int TEX_W = 186, TEX_H = 204;

    // Scrollbar, in GUI pixels
    private static final int BAR_X = 173, BAR_Y = 36, BAR_W = 8, BAR_H = 70, THUMB_H = 15;
    private boolean draggingBar = false;

    // Tabs, in GUI pixels: 16x16 icons along the top
    private static final int TAB_Y = 11, TAB_SIZE = 16;
    private static final int[] TAB_X = {16, 40, 64, 88};

    public WitchShelfScreen(WitchShelfMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = TEX_W;
        this.imageHeight = TEX_H;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, TEX_W, TEX_H);

        int x = leftPos + BAR_X;
        int y = topPos + BAR_Y;
        graphics.fill(x, y, x + BAR_W, y + BAR_H, 0xFF373737);
        int thumbY = y + thumbOffset();
        int thumbColor = menu.maxScroll() > 0 ? 0xFFC8A870 : 0xFF8B8B8B;
        graphics.fill(x, thumbY, x + BAR_W, thumbY + THUMB_H, thumbColor);

        ShelfCategory selected = menu.tab();
        for (ShelfCategory category : ShelfCategory.values()) {
            graphics.blit(tabTexture(category, category == selected),
                    leftPos + TAB_X[category.ordinal()], topPos + TAB_Y, 0, 0, TAB_SIZE, TAB_SIZE, TAB_SIZE, TAB_SIZE);
        }
    }

    private static ResourceLocation tabTexture(ShelfCategory category, boolean selected) {
        String file = "tab_" + category.name + (selected ? "_selected" : "") + ".png";
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/gui/witch_shelf/" + file);
    }

    // Which tab the mouse is over, or null.
    private ShelfCategory tabAt(double mouseX, double mouseY) {
        double x = mouseX - leftPos, y = mouseY - topPos;
        if (y < TAB_Y || y >= TAB_Y + TAB_SIZE) return null;
        for (ShelfCategory category : ShelfCategory.values()) {
            int tabX = TAB_X[category.ordinal()];
            if (x >= tabX && x < tabX + TAB_SIZE) return category;
        }
        return null;
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        ShelfCategory hovered = tabAt(mouseX, mouseY);
        if (hovered != null && menu.getCarried().isEmpty()) {
            graphics.renderTooltip(font, Component.translatable("gui.black_tongue.witch_shelf.tab." + hovered.name), mouseX, mouseY);
            return;
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    // No title text: the top of your GUI is for the tabs.
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { }

    private int thumbOffset() {
        int max = menu.maxScroll();
        return max == 0 ? 0 : menu.scrollRow() * (BAR_H - THUMB_H) / max;
    }

    private void scrollTo(int row) {
        row = Mth.clamp(row, 0, menu.maxScroll());
        if (row == menu.scrollRow() || minecraft == null || minecraft.gameMode == null) return;
        menu.setScrollRowClient(row);
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, WitchShelfMenu.SCROLL_BUTTON + row);
    }

    private boolean overBar(double mouseX, double mouseY) {
        double x = mouseX - leftPos, y = mouseY - topPos;
        return x >= BAR_X && x < BAR_X + BAR_W && y >= BAR_Y && y < BAR_Y + BAR_H;
    }

    private void scrollToMouse(double mouseY) {
        float t = (float) (mouseY - topPos - BAR_Y - THUMB_H / 2.0) / (BAR_H - THUMB_H);
        scrollTo(Math.round(Mth.clamp(t, 0.0F, 1.0F) * menu.maxScroll()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (menu.maxScroll() > 0 && scrollY != 0) {
            scrollTo(menu.scrollRow() - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ShelfCategory clickedTab = tabAt(mouseX, mouseY);
        if (button == 0 && clickedTab != null) {
            if (clickedTab != menu.tab() && minecraft != null && minecraft.gameMode != null) {
                menu.setTabClient(clickedTab);
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, clickedTab.ordinal());
            }
            return true;
        }
        if (button == 0 && overBar(mouseX, mouseY)) {
            draggingBar = true;
            scrollToMouse(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingBar) {
            scrollToMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingBar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}