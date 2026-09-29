package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.MortarAndPestleBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MortarAndPestleRenderer extends GeoBlockRenderer<MortarAndPestleBlockEntity> {
    public MortarAndPestleRenderer() {
        super(new MortarAndPestleModel());
    }
}