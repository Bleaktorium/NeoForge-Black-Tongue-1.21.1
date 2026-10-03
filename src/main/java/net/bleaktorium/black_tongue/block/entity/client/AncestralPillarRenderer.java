package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.AncestralPillarBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AncestralPillarRenderer extends GeoBlockRenderer<AncestralPillarBlockEntity> {
    public AncestralPillarRenderer() {
        super(new AncestralPillarModel()); }
}