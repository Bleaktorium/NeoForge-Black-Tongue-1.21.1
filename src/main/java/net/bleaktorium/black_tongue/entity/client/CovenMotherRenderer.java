package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CovenMotherRenderer extends GeoEntityRenderer<CovenMotherEntity> {
    public CovenMotherRenderer(EntityRendererProvider.Context context) {
        super(context, new CovenMotherModel());
    }
}