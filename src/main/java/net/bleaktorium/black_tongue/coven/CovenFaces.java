package net.bleaktorium.black_tongue.coven;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CovenFaces {
    private static final Map<String, Boolean> EXISTS_CACHE = new HashMap<>();

    public static void draw(Screen screen, GuiGraphics g, CovenMember m, int x, int y, int size) {
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
            PlayerFaceRenderer.draw(g, skin, x, y, size);
            return;
        }

        ResourceLocation icon = witchFace(m.displayName());
        if (EXISTS_CACHE.computeIfAbsent(icon.toString(),
                k -> Minecraft.getInstance().getResourceManager().getResource(icon).isPresent())) {
            g.blit(icon, x, y, 0, 0, size, size, size, size);
            return;
        }

        g.fill(x, y, x + size, y + size, 0xFF5A3F75);
        String initial = m.displayName().isEmpty() ? "?" : m.displayName().substring(0, 1);
        g.drawString(screen.getMinecraft().font, initial, x + (size - screen.getMinecraft().font.width(initial)) / 2, y + 4, 0xFFFFFF, false);
    }

    private static ResourceLocation witchFace(String name) {
        String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        return ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/coven/face_" + slug + ".png");
    }
}