package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CovenScreen extends Screen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/coven/coven_screen.png");

    private static final int W = 224, H = 165;
    private static final int NAME_CENTER_X = 112, NAME_Y = 6;
    private static final int LABEL_X = 16, LABEL_Y = 19;
    private static final int LIST_X = 16, LIST_Y = 31, LIST_W = 80;
    private static final int ROW_H = 12;
    private static final float LIST_SCALE = 0.85f;
    private static final int FACE = 16;
    private static final int MOTHER_X = 152, MOTHER_Y = 31;
    private static final int[][] SLOTS = {
            {128, 47}, {176, 47},
            {128, 71}, {176, 71},
            {128, 95}, {176, 95},
            {128, 119}, {176, 119}
    };
    private static final int ACCEPT_CENTER_X = 160, ACCEPT_Y = 143;
    private static final int CLOSE_X = 209, CLOSE_Y = 5, CLOSE_SIZE = 9;

    private final boolean canEdit;
    private final String originalName;
    private final CovenMember mother;
    private final CovenMember founder;
    private final List<CovenMember> draftExtras;
    private final List<CovenMember> allCandidates;
    private final StringBuilder nameDraft;
    private boolean editingName = false;

    private int lastRightClickSlot = -1;
    private long lastRightClickTime = 0;
    private final Map<String, Boolean> faceFileExists = new HashMap<>();

    public CovenScreen(CovenMenuSyncPacket packet) {
        super(Component.literal("Coven"));
        this.canEdit = packet.canEdit();
        this.originalName = packet.covenName();
        this.mother = packet.mother();
        this.founder = packet.founder();
        this.draftExtras = new ArrayList<>(packet.extras());
        this.allCandidates = new ArrayList<>(packet.candidates());
        this.nameDraft = new StringBuilder(packet.covenName());
    }

    private int panelX() { return (width - W) / 2; }
    private int panelY() { return (height - H) / 2; }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // DRAWING
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0x99000000);

        int px = panelX(), py = panelY();
        g.blit(BACKGROUND, px, py, 0, 0, W, H, W, H);

        drawName(g, px, py);
        drawListPanel(g, px, py, mouseX, mouseY);
        drawSlots(g, px, py);
        drawButtons(g, px, py, mouseX, mouseY);
        drawHeadTooltip(g, px, py, mouseX, mouseY);

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawName(GuiGraphics g, int px, int py) {
        String shown = canEdit ? nameDraft.toString() : originalName;

        int x = px + NAME_CENTER_X - font.width(shown) / 2;
        g.drawString(font, shown, x, py + NAME_Y, 0xFFE9A8, true);

        if (editingName && (System.currentTimeMillis() / 500) % 2 == 0) {
            g.drawString(font, "_", x + font.width(shown), py + NAME_Y, 0xFFE9A8, true);
        }
    }

    private List<CovenMember> listRows() {
        List<CovenMember> rows = new ArrayList<>();
        if (canEdit) {
            for (CovenMember c : allCandidates) {
                if (draftExtras.stream().noneMatch(d -> d.matches(c.kind(), c.key()))) rows.add(c);
            }
        } else {
            rows.add(mother);
            rows.add(founder);
            rows.addAll(draftExtras);
        }
        return rows;
    }

    private void drawListPanel(GuiGraphics g, int px, int py, int mouseX, int mouseY) {
        g.drawString(font, canEdit ? "Participants" : "Members", px + LIST_X, py + LABEL_Y, 0xE9D9A0, true);

        List<CovenMember> rows = listRows();
        for (int i = 0; i < rows.size(); i++) {
            int rowY = py + LIST_Y + 3 + i * ROW_H;
            boolean hovered = canEdit && inRect(mouseX, mouseY, px + LIST_X, rowY - 1, LIST_W, ROW_H);
            int color = hovered ? 0xFFD800 : 0xE0D8C8;
            String text = font.plainSubstrByWidth(rows.get(i).displayName(), (int) ((LIST_W - 8) / LIST_SCALE));

            g.pose().pushPose();
            g.pose().translate(px + LIST_X + 4, rowY, 0);
            g.pose().scale(LIST_SCALE, LIST_SCALE, 1f);
            g.drawString(font, text, 0, 0, color, false);
            g.pose().popPose();
        }
    }

    private void drawSlots(GuiGraphics g, int px, int py) {
        drawFace(g, mother, px + MOTHER_X, py + MOTHER_Y);
        drawFace(g, founder, px + SLOTS[0][0], py + SLOTS[0][1]);
        for (int i = 0; i < draftExtras.size(); i++) {
            int[] pos = SLOTS[i + 1];
            drawFace(g, draftExtras.get(i), px + pos[0], py + pos[1]);
        }
    }

    private void drawButtons(GuiGraphics g, int px, int py, int mouseX, int mouseY) {
        boolean closeHover = inRect(mouseX, mouseY, px + CLOSE_X, py + CLOSE_Y, CLOSE_SIZE, CLOSE_SIZE);
        g.drawString(font, "X", px + CLOSE_X + 2, py + CLOSE_Y + 1, closeHover ? 0xFFFFFF : 0xE9D9A0, true);

        if (!canEdit) return;
        int w = font.width("Accept");
        int x = px + ACCEPT_CENTER_X - w / 2;
        boolean hover = inRect(mouseX, mouseY, x - 2, py + ACCEPT_Y - 2, w + 4, 12);
        g.drawString(font, "Accept", x, py + ACCEPT_Y, hover ? 0xFFD800 : 0xEFE6C8, true);
    }

    private void drawHeadTooltip(GuiGraphics g, int px, int py, int mouseX, int mouseY) {
        if (inRect(mouseX, mouseY, px + MOTHER_X, py + MOTHER_Y, FACE, FACE)) {
            g.renderTooltip(font, Component.literal(mother.displayName() + " (Coven Mother)"), mouseX, mouseY);
            return;
        }
        if (inRect(mouseX, mouseY, px + SLOTS[0][0], py + SLOTS[0][1], FACE, FACE)) {
            g.renderTooltip(font, Component.literal(founder.displayName() + " (Founder)"), mouseX, mouseY);
            return;
        }
        for (int i = 0; i < draftExtras.size(); i++) {
            int[] pos = SLOTS[i + 1];
            if (inRect(mouseX, mouseY, px + pos[0], py + pos[1], FACE, FACE)) {
                String hint = canEdit ? "  (double right-click to remove)" : "";
                g.renderTooltip(font, Component.literal(draftExtras.get(i).displayName() + hint), mouseX, mouseY);
                return;
            }
        }
    }

    // FACES
    private void drawFace(GuiGraphics g, CovenMember m, int x, int y) {
        if (m.isPlayer()) {
            UUID id;
            try {
                id = UUID.fromString(m.key());
            } catch (IllegalArgumentException e) {
                id = Util.NIL_UUID;
            }

            PlayerSkin skin = null;
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                PlayerInfo info = connection.getPlayerInfo(id);
                if (info != null) skin = info.getSkin();
            }
            if (skin == null) skin = DefaultPlayerSkin.get(id);

            PlayerFaceRenderer.draw(g, skin, x, y, FACE);
            return;
        }

        ResourceLocation icon = witchFace(m.displayName());
        if (faceFileExists.computeIfAbsent(icon.toString(),
                k -> Minecraft.getInstance().getResourceManager().getResource(icon).isPresent())) {
            g.blit(icon, x, y, 0, 0, FACE, FACE, FACE, FACE);
            return;
        }

        g.fill(x, y, x + FACE, y + FACE, 0xFF5A3F75);
        String initial = m.displayName().isEmpty() ? "?" : m.displayName().substring(0, 1);
        g.drawString(font, initial, x + (FACE - font.width(initial)) / 2, y + 4, 0xFFFFFF, false);
    }

    private static ResourceLocation witchFace(String name) {
        String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        return ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/coven/face_" + slug + ".png");
    }

    // INPUT
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int px = panelX(), py = panelY();
        int nameW = 100;
        int nameX = px + NAME_CENTER_X - nameW / 2;
        if (button == 0 && (canEdit) && inRect(mx, my, nameX, py + NAME_Y - 2, nameW, 12)) {
            editingName = true;
            return true;
        }

        if (button == 0 && inRect(mx, my, px + CLOSE_X, py + CLOSE_Y, CLOSE_SIZE, CLOSE_SIZE)) {
            onClose();
            return true;
        }
        if (!canEdit) return super.mouseClicked(mx, my, button);

        if (button == 0) {
            int w = font.width("Accept");
            int x = px + ACCEPT_CENTER_X - w / 2;
            if (inRect(mx, my, x - 2, py + ACCEPT_Y - 2, w + 4, 12)) {
                ModMessages.sendToServer(new CovenMenuAcceptPacket(nameDraft.toString(), List.copyOf(draftExtras)));
                onClose();
                return true;
            }

            List<CovenMember> rows = listRows();
            for (int i = 0; i < rows.size(); i++) {
                int rowY = py + LIST_Y + 3 + i * ROW_H;
                if (inRect(mx, my, px + LIST_X, rowY - 1, LIST_W, ROW_H)) {
                    if (draftExtras.size() < Coven.MAX_EXTRA_MEMBERS) draftExtras.add(rows.get(i));
                    return true;
                }
            }
        }

        // Double right-click a head to take them out
        if (button == 1) {
            for (int i = 0; i < draftExtras.size(); i++) {
                int[] pos = SLOTS[i + 1];
                if (inRect(mx, my, px + pos[0], py + pos[1], FACE, FACE)) {
                    long now = System.currentTimeMillis();
                    if (lastRightClickSlot == i && now - lastRightClickTime < 400) {
                        draftExtras.remove(i);
                        lastRightClickSlot = -1;
                    } else {
                        lastRightClickSlot = i;
                        lastRightClickTime = now;
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (canEdit && StringUtil.isAllowedChatCharacter(c) && nameDraft.length() < Coven.MAX_NAME_LENGTH) {
            nameDraft.append(c);
            return true;
        }
        return super.charTyped(c, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (canEdit && editingName && keyCode == GLFW.GLFW_KEY_BACKSPACE && nameDraft.length() > 0) {
            nameDraft.deleteCharAt(nameDraft.length() - 1);
            return true;
        }
        if (canEdit && editingName && keyCode == GLFW.GLFW_KEY_ENTER) {
            editingName = false;
            return true;
        }
        if (canEdit && keyCode == GLFW.GLFW_KEY_BACKSPACE && nameDraft.length() > 0) {
            nameDraft.deleteCharAt(nameDraft.length() - 1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}