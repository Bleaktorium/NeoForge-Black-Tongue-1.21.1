package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.CovenlessWitchEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CovenlessWitchRenderer extends GeoEntityRenderer<CovenlessWitchEntity> {
    public CovenlessWitchRenderer(EntityRendererProvider.Context context) {
        super(context, new CovenlessWitchModel());
    }
}