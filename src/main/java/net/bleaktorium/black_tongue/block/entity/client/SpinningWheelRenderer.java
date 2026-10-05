package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.SpinningWheelBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SpinningWheelRenderer extends GeoBlockRenderer<SpinningWheelBlockEntity> {
    public SpinningWheelRenderer() {
        super(new SpinningWheelModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Nullable
            @Override
            protected RenderType getRenderType(SpinningWheelBlockEntity wheel, @Nullable MultiBufferSource bufferSource) {
                return wheel.getFiber() == SpinningWheelBlockEntity.Fiber.WISP
                        ? super.getRenderType(wheel, bufferSource)
                        : null;
            }
        });
    }

    @Override
    public AABB getRenderBoundingBox(SpinningWheelBlockEntity wheel) {
        return new AABB(wheel.getBlockPos()).inflate(1);
    }
}