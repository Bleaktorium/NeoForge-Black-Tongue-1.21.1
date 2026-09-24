package net.bleaktorium.black_tongue.dialog;

import com.mojang.blaze3d.vertex.PoseStack;
import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class DialogScreen extends Screen {
    private final String npcText;
    private final List<String> optionLabels;
    private final LivingEntity portraitEntity;

    public DialogScreen(String npcText, List<String> optionLabels) {
        this(npcText, optionLabels, null);
    }

    public DialogScreen(String npcText, List<String> optionLabels, LivingEntity portraitEntity) {
        super(Component.literal("Dialog"));
        this.npcText = npcText;
        this.optionLabels = optionLabels;
        this.portraitEntity = portraitEntity;
    }

    @Override
    protected void init() {
        super.init();

        int panelWidth = 300;
        int x = (width - panelWidth) / 2;
        int y = height / 2 - 80;
        for (int i = 0; i < optionLabels.size(); i++) {
            int index = i;
            this.addRenderableWidget(Button.builder(Component.literal(optionLabels.get(i)),
                            button -> ModMessages.sendToServer(new DialogChoicePacket(index)))
                    .bounds(x, y + 60 + (i * 22), panelWidth, 20)
                    .build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height, 0x99000000);

        int panelWidth = 300;
        int x = (width - panelWidth) / 2;
        int y = height / 2 - 80;

        guiGraphics.fill(x, y, x + panelWidth, y + 50, 0xCC1A1420);
        guiGraphics.drawWordWrap(this.font, Component.literal(npcText), x + 8, y + 8, panelWidth - 16, 0xE0D8C8);

        renderPortrait(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderPortrait(GuiGraphics guiGraphics) {
        if (portraitEntity == null) return;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}