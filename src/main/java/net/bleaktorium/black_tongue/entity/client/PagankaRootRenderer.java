package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.PagankaRootEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PagankaRootRenderer extends GeoEntityRenderer<PagankaRootEntity> {
    public PagankaRootRenderer(EntityRendererProvider.Context context) {
        super(context, new PagankaRootModel());
    }
}