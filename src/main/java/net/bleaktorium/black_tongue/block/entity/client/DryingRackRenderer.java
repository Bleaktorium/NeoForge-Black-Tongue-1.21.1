package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.DryingRackBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DryingRackRenderer extends GeoBlockRenderer<DryingRackBlockEntity> {
    public DryingRackRenderer() {
        super(new DryingRackModel());
    }
}