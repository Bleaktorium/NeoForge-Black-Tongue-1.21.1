package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.HauntingSoulEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HauntingSoulRenderer extends GeoEntityRenderer<HauntingSoulEntity> {
    public HauntingSoulRenderer(EntityRendererProvider.Context context) {
        super(context, new HauntingSoulModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.25F;
    }
}