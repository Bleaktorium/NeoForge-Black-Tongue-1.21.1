package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.CovenMotherCatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CovenMotherCatRenderer extends GeoEntityRenderer<CovenMotherCatEntity> {
    public CovenMotherCatRenderer(EntityRendererProvider.Context context) {

        super(context, new CovenMotherCatModel());
    }
}