package net.bleaktorium.black_tongue.dialog;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogScreen extends Screen {

    private static final ResourceLocation PORTRAIT =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/coven_mother_yaga_portrait.png");
    private static final ResourceLocation SCROLL_UP =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/dialog_scroll_up_button.png");
    private static final ResourceLocation SCROLL_DOWN =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/dialog_scroll_down_button.png");
    private static final ResourceLocation TRADE_NORMAL =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/dialog_trade_button.png");
    private static final ResourceLocation TRADE_HOVER =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/dialog_trade_button_hover.png");
    private static final ResourceLocation TRADE_PRESSED =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/dialog_trade_button_pressed.png");
    private static final ResourceLocation QUEST_MARKER =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/dialog_quest_marker.png");

    private static final Map<String, ResourceLocation> THEME_BACKGROUNDS = Map.of(
            "witchcraft", ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/panel_witchcraft.png")
    );

    private static final int PANEL_WIDTH = 276;
    private static final int PANEL_HEIGHT = 166;
    private static final int PORTRAIT_X = 13, PORTRAIT_Y = 15, PORTRAIT_W = 96, PORTRAIT_H = 83;
    private static final int TEXTBOX_X = 123, TEXTBOX_Y = 21, TEXTBOX_W = 135, TEXTBOX_H = 60;
    private static final int TRADE_BTN_X = 50, TRADE_BTN_Y = 126, TRADE_BTN_W = 22, TRADE_BTN_H = 21;
    private static final int OPTIONS_X = TEXTBOX_X;
    private static final int OPTIONS_Y_START = TEXTBOX_Y + TEXTBOX_H + 6;
    private static final int NAME_Y = PORTRAIT_Y + PORTRAIT_H + 3;
    private static final float TEXT_SCALE = 0.85f;
    private static final int SCALED_LINE_HEIGHT = Math.round(10 * TEXT_SCALE);

    private final String npcText;
    private final String speakerName;
    private final String themeId; // NEW — was missing as an actual field before
    private final List<DialogOptionView> options;
    private final LivingEntity portraitEntity;

    private int textScrollOffset = 0;
    private List<FormattedCharSequence> wrappedTextLines;
    private List<List<FormattedCharSequence>> wrappedOptionLines;

    public DialogScreen(String npcText, String speakerName, String themeId, List<DialogOptionView> options) {
        this(npcText, speakerName, themeId, options, null);
    }

    public DialogScreen(String npcText, String speakerName, String themeId, List<DialogOptionView> options, LivingEntity portraitEntity) {
        super(Component.literal("Dialog"));
        this.npcText = npcText;
        this.speakerName = speakerName;
        this.themeId = themeId;
        this.options = options;
        this.portraitEntity = portraitEntity;
    }

    private ResourceLocation resolveBackground() {
        return THEME_BACKGROUNDS.getOrDefault(themeId, THEME_BACKGROUNDS.get("witchcraft"));
    }

    @Override
    protected void init() {
        super.init();
        int effectiveWrapWidth = (int) ((TEXTBOX_W - 8) / TEXT_SCALE);
        wrappedTextLines = font.split(Component.literal(npcText), effectiveWrapWidth);
        textScrollOffset = 0;

        wrappedOptionLines = new ArrayList<>();
        int optionWrapWidth = (int) ((PANEL_WIDTH - OPTIONS_X - 12) / TEXT_SCALE);
        for (DialogOptionView option : options) {
            wrappedOptionLines.add(font.split(Component.literal(option.label()), optionWrapWidth));
        }
    }

    private int panelX() { return (width - PANEL_WIDTH) / 2; }
    private int panelY() { return (height - PANEL_HEIGHT) / 2; }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0x99000000);

        int x = panelX();
        int y = panelY();

        graphics.blit(resolveBackground(), x, y, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, PANEL_WIDTH, PANEL_HEIGHT);

        renderPortrait(graphics, x, y);
        renderCharacterName(graphics, x, y);
        renderTextBox(graphics, x, y, mouseX, mouseY);
        renderTradeButton(graphics, x, y, mouseX, mouseY);
        renderOptions(graphics, x, y, mouseX, mouseY);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderPortrait(GuiGraphics graphics, int panelX, int panelY) {
        int px = panelX + PORTRAIT_X;
        int py = panelY + PORTRAIT_Y;
        graphics.blit(PORTRAIT, px, py, 0, 0, PORTRAIT_W, PORTRAIT_H, PORTRAIT_W, PORTRAIT_H);
    }

    private void renderCharacterName(GuiGraphics graphics, int panelX, int panelY) {
        int nx = panelX + PORTRAIT_X;
        int ny = panelY + NAME_Y;
        graphics.drawString(font, speakerName, nx, ny, 0x7A5C1E, false);
    }

    private void renderTextBox(GuiGraphics graphics, int panelX, int panelY, int mouseX, int mouseY) {
        int bx = panelX + TEXTBOX_X;
        int by = panelY + TEXTBOX_Y;

        graphics.fill(bx, by, bx + TEXTBOX_W, by + TEXTBOX_H, 0x291d31);

        graphics.enableScissor(bx, by, bx + TEXTBOX_W, by + TEXTBOX_H);
        graphics.pose().pushPose();
        graphics.pose().translate(bx + 4, by + 4 - (textScrollOffset * SCALED_LINE_HEIGHT), 0);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1f);

        int lineY = 0;
        for (FormattedCharSequence line : wrappedTextLines) {
            graphics.drawString(font, line, 0, lineY, 0xcec7ba, false);
            lineY += 10;
        }
        graphics.pose().popPose();
        graphics.disableScissor();

        int visibleLines = TEXTBOX_H / SCALED_LINE_HEIGHT;
        int maxScroll = Math.max(0, wrappedTextLines.size() - visibleLines);

        int arrowX = bx + TEXTBOX_W + 2;
        drawIconButton(graphics, SCROLL_UP, arrowX, by, textScrollOffset > 0);
        drawIconButton(graphics, SCROLL_DOWN, arrowX, by + TEXTBOX_H - 9, textScrollOffset < maxScroll);
    }

    private void drawIconButton(GuiGraphics graphics, ResourceLocation texture, int x, int y, boolean enabled) {
        RenderSystem.enableBlend();
        graphics.setColor(1f, 1f, 1f, enabled ? 1f : 0.4f);
        graphics.blit(texture, x, y, 0, 0, 9, 9, 9, 9);
        graphics.setColor(1f, 1f, 1f, 1f);
    }

    private void renderTradeButton(GuiGraphics graphics, int panelX, int panelY, int mouseX, int mouseY) {
        int bx = panelX + TRADE_BTN_X;
        int by = panelY + TRADE_BTN_Y;

        boolean hovered = mouseX >= bx - 1 && mouseX < bx + TRADE_BTN_W + 1
                && mouseY >= by - 1 && mouseY < by + TRADE_BTN_H + 1;
        boolean pressed = hovered && GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (pressed) {
            graphics.blit(TRADE_PRESSED, bx, by, 0, 0, TRADE_BTN_W, TRADE_BTN_H, TRADE_BTN_W, TRADE_BTN_H);
        } else if (hovered) {
            graphics.blit(TRADE_HOVER, bx - 1, by - 1, 0, 0, 25, 25, 25, 25);
        } else {
            graphics.blit(TRADE_NORMAL, bx, by, 0, 0, TRADE_BTN_W, TRADE_BTN_H, TRADE_BTN_W, TRADE_BTN_H);
        }
    }

    private void renderOptions(GuiGraphics graphics, int panelX, int panelY, int mouseX, int mouseY) {
        int lineY = panelY + OPTIONS_Y_START;

        for (int i = 0; i < options.size(); i++) {
            DialogOptionView option = options.get(i);
            List<FormattedCharSequence> lines = wrappedOptionLines.get(i);
            int optionHeight = lines.size() * SCALED_LINE_HEIGHT;

            boolean hovered = mouseX >= panelX + OPTIONS_X && mouseX < panelX + PANEL_WIDTH - 8
                    && mouseY >= lineY && mouseY < lineY + optionHeight;
            int color = hovered ? 0xFFD800 : 0xd6d1b5;

            int textX = panelX + OPTIONS_X;
            if (option.marksQuest()) {
                graphics.blit(QUEST_MARKER, textX, lineY - 2, 0, 0, 4, 9, 4, 9);
                textX += 6;
            }

            graphics.pose().pushPose();
            graphics.pose().translate(textX, lineY, 0);
            graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1f);
            int wrapLineY = 0;
            for (FormattedCharSequence line : lines) {
                graphics.drawString(font, line, 0, wrapLineY, color, false);
                wrapLineY += 10;
            }
            graphics.pose().popPose();

            lineY += optionHeight;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int panelX = panelX();
        int panelY = panelY();

        int tbx = panelX + TRADE_BTN_X, tby = panelY + TRADE_BTN_Y;
        if (mouseX >= tbx - 1 && mouseX < tbx + TRADE_BTN_W + 1 && mouseY >= tby - 1 && mouseY < tby + TRADE_BTN_H + 1) {
            return true;
        }

        int bx = panelX + TEXTBOX_X, by = panelY + TEXTBOX_Y;
        int visibleLines = TEXTBOX_H / SCALED_LINE_HEIGHT;
        int maxScroll = Math.max(0, wrappedTextLines.size() - visibleLines);
        int arrowX = bx + TEXTBOX_W + 2;
        if (mouseX >= arrowX && mouseX < arrowX + 9 && mouseY >= by && mouseY < by + 9 && textScrollOffset > 0) {
            textScrollOffset--; return true;
        }
        if (mouseX >= arrowX && mouseX < arrowX + 9 && mouseY >= by + TEXTBOX_H - 9 && mouseY < by + TEXTBOX_H && textScrollOffset < maxScroll) {
            textScrollOffset++; return true;
        }

        int lineY = panelY + OPTIONS_Y_START;
        for (int i = 0; i < options.size(); i++) {
            int optionHeight = wrappedOptionLines.get(i).size() * SCALED_LINE_HEIGHT;
            if (mouseX >= panelX + OPTIONS_X && mouseX < panelX + PANEL_WIDTH - 8
                    && mouseY >= lineY && mouseY < lineY + optionHeight) {
                ModMessages.sendToServer(new DialogChoicePacket(i));
                return true;
            }
            lineY += optionHeight;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}