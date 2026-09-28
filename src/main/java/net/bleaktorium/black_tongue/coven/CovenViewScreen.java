package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class CovenViewScreen extends Screen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/coven/coven_view_screen.png");

    private static final int W = 126, H = 165;
    private static final int NAME_CENTER_X = 63, NAME_Y = 8;
    private static final int MOTHER_X = 54, MOTHER_Y = 31;
    private static final int[][] SLOTS = {
            {30, 47}, {78, 47}, {30, 71}, {78, 71}, {30, 95}, {78, 95}, {30, 119}, {78, 119}
    };
    private static final int FACE = 16;
    private static final int ABANDON_Y = 143;
    private static final int CLOSE_X = 111, CLOSE_Y = 5, CLOSE_SIZE = 9;
    private static final long DOUBLE_CLICK_MS = 400;

    private final boolean isFounder;
    private final CovenMember mother;
    private final CovenMember founder;
    private final List<CovenMember> extras;
    private final StringBuilder nameDraft;
    private boolean editingName = false;

    private int lastKickSlot = -1;
    private long lastKickTime = 0;
    private long lastAbandonClickTime = 0;

    public CovenViewScreen(CovenMenus.ViewerRole role, String covenName, CovenMember mother,
                           CovenMember founder, List<CovenMember> extras) {
        super(Component.literal("Coven"));
        this.isFounder = role == CovenMenus.ViewerRole.FOUNDER;
        this.mother = mother;
        this.founder = founder;
        this.extras = extras;
        this.nameDraft = new StringBuilder(covenName);
    }

    private int panelX() { return (width - W) / 2; }
    private int panelY() { return (height - H) / 2; }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0x99000000);
        int px = panelX(), py = panelY();
        g.blit(BACKGROUND, px, py, 0, 0, W, H, W, H);

        drawName(g, px, py);
        drawTree(g, px, py, mouseX, mouseY);
        drawFooter(g, px, py, mouseX, mouseY);
        drawTooltip(g, px, py, mouseX, mouseY);

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawName(GuiGraphics g, int px, int py) {
        String shown = nameDraft.toString();

        int x = px + NAME_CENTER_X - font.width(shown) / 2;
        g.drawString(font, shown, x, py + NAME_Y, 0xFFE9A8, true);

        if (editingName && (System.currentTimeMillis() / 500) % 2 == 0) {
            g.drawString(font, "_", x + font.width(shown), py + NAME_Y, 0xFFE9A8, true);
        }
    }

    private void drawTree(GuiGraphics g, int px, int py, int mouseX, int mouseY) {
        drawFace(g, mother, px + MOTHER_X, py + MOTHER_Y);
        drawFace(g, founder, px + SLOTS[0][0], py + SLOTS[0][1]);
        for (int i = 0; i < extras.size(); i++) {
            int[] pos = SLOTS[i + 1];
            drawFace(g, extras.get(i), px + pos[0], py + pos[1]);
        }
    }

    private void drawFace(GuiGraphics g, CovenMember m, int x, int y) {
        CovenFaces.draw(this, g, m, x, y, FACE);
    }

    private void drawFooter(GuiGraphics g, int px, int py, int mouseX, int mouseY) {
        boolean closeHover = inRect(mouseX, mouseY, px + CLOSE_X, py + CLOSE_Y, CLOSE_SIZE, CLOSE_SIZE);
        g.drawString(font, "X", px + CLOSE_X + 2, py + CLOSE_Y + 1, closeHover ? 0xFFFFFF : 0xE9D9A0, true);

        String label = isFounder ? "Abandon Coven" : "Leave Coven";
        int w = font.width(label);
        int x = px + W / 2 - w / 2;
        boolean hover = inRect(mouseX, mouseY, x - 2, py + ABANDON_Y - 2, w + 4, 12);
        g.drawString(font, label, x, py + ABANDON_Y, hover ? 0xFF4A4A : 0xEFC8C8, true); // red glow on hover
    }

    private void drawTooltip(GuiGraphics g, int px, int py, int mouseX, int mouseY) {
        if (inRect(mouseX, mouseY, px + MOTHER_X, py + MOTHER_Y, FACE, FACE)) {
            g.renderTooltip(font, Component.literal(mother.displayName() + " (Coven Mother)"), mouseX, mouseY);
            return;
        }
        if (inRect(mouseX, mouseY, px + SLOTS[0][0], py + SLOTS[0][1], FACE, FACE)) {
            g.renderTooltip(font, Component.literal(founder.displayName() + " (Founder)"), mouseX, mouseY);
            return;
        }
        for (int i = 0; i < extras.size(); i++) {
            int[] pos = SLOTS[i + 1];
            if (inRect(mouseX, mouseY, px + pos[0], py + pos[1], FACE, FACE)) {
                String hint = isFounder ? "  (double right-click to kick)" : "";
                g.renderTooltip(font, Component.literal(extras.get(i).displayName() + hint), mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int px = panelX(), py = panelY();
        int nameW = 100; // a fixed clickable width around the name, generous enough to always cover it
        int nameX = px + NAME_CENTER_X - nameW / 2;
        if (button == 0 && (isFounder) && inRect(mx, my, nameX, py + NAME_Y - 2, nameW, 12)) {
            editingName = true;
            return true;
        }

        if (button == 0 && inRect(mx, my, px + CLOSE_X, py + CLOSE_Y, CLOSE_SIZE, CLOSE_SIZE)) {
            onClose();
            return true;
        }

        if (button == 0) {
            String label = isFounder ? "Abandon Coven" : "Leave Coven";
            int w = font.width(label);
            int x = px + W / 2 - w / 2;
            if (inRect(mx, my, x - 2, py + ABANDON_Y - 2, w + 4, 12)) {
                long now = System.currentTimeMillis();
                if (now - lastAbandonClickTime < DOUBLE_CLICK_MS) {
                    ModMessages.sendToServer(new CovenLeaveOrAbandonPacket());
                    onClose();
                } else {
                    lastAbandonClickTime = now;
                }
                return true;
            }
        }

        if (button == 1 && isFounder) {
            for (int i = 0; i < extras.size(); i++) {
                int[] pos = SLOTS[i + 1];
                if (inRect(mx, my, px + pos[0], py + pos[1], FACE, FACE)) {
                    long now = System.currentTimeMillis();
                    if (lastKickSlot == i && now - lastKickTime < DOUBLE_CLICK_MS) {
                        CovenMember m = extras.get(i);
                        ModMessages.sendToServer(new CovenKickPacket(m.isPlayer(), m.key()));
                        extras.remove(i); // optimistic: the server will confirm on the next open
                        lastKickSlot = -1;
                    } else {
                        lastKickSlot = i;
                        lastKickTime = now;
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (isFounder && StringUtil.isAllowedChatCharacter(c) && nameDraft.length() < Coven.MAX_NAME_LENGTH) {
            nameDraft.append(c);
            return true;
        }
        return super.charTyped(c, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFounder && editingName && keyCode == GLFW.GLFW_KEY_BACKSPACE && nameDraft.length() > 0) {
            nameDraft.deleteCharAt(nameDraft.length() - 1);
            return true;
        }
        if (isFounder && editingName && keyCode == GLFW.GLFW_KEY_ENTER) {
            ModMessages.sendToServer(new CovenRenamePacket(nameDraft.toString()));
            editingName = false;
            return true;
        }
        if (isFounder && keyCode == GLFW.GLFW_KEY_BACKSPACE && nameDraft.length() > 0) {
            nameDraft.deleteCharAt(nameDraft.length() - 1);
            return true;
        }
        if (isFounder && keyCode == GLFW.GLFW_KEY_ENTER) {
            ModMessages.sendToServer(new CovenRenamePacket(nameDraft.toString()));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}