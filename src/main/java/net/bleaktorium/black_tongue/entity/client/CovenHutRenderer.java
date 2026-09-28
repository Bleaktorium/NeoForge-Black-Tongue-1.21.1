package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.CovenHutEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CovenHutRenderer extends GeoEntityRenderer<CovenHutEntity> {
    public CovenHutRenderer(EntityRendererProvider.Context context) {
        super(context, new CovenHutModel());
    }
}