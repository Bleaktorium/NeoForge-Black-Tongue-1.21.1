package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.PagankaRootCropBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PagankaRootCropRenderer extends GeoBlockRenderer<PagankaRootCropBlockEntity> {
    public PagankaRootCropRenderer() {
        super(new PagankaRootCropModel());
    }
}