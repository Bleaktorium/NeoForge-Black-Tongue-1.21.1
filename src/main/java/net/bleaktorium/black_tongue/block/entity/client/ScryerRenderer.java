package net.bleaktorium.black_tongue.block.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.bleaktorium.black_tongue.block.entity.ScryerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ScryerRenderer extends GeoBlockRenderer<ScryerBlockEntity> {
    public ScryerRenderer() {
        super(new ScryerModel());
    }

    // Derived from the "liquid" bone's cube in scryer.geo.json, converted
    // from Blockbench units (divide by 16) into a block-fraction offset
    // from the block's own corner. NUDGE THESE after one in-game look --
    // Blockbench-to-world conversion is easy to get slightly off.
    private static final double ICON_X = 0.5;
    private static final double ICON_Y = 17.0 / 16.0;
    private static final double ICON_Z = 0.5;
    private static final float ICON_SCALE = 0.4f; // aims to fit roughly within the 6x6 liquid footprint

    @Override
    public void render(ScryerBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        if (!blockEntity.isScanning()) return;
        ItemStack stack = blockEntity.getScanningItem();
        if (stack.isEmpty() || blockEntity.getLevel() == null) return;

        poseStack.pushPose();
        poseStack.translate(ICON_X, ICON_Y, ICON_Z);

        double time = blockEntity.getLevel().getGameTime() + partialTick;
        poseStack.mulPose(Axis.YP.rotationDegrees((float) (time * 2.0)));
        poseStack.scale(ICON_SCALE, ICON_SCALE, ICON_SCALE);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay,
                poseStack, bufferSource, blockEntity.getLevel(), 0);

        poseStack.popPose();
    }
}