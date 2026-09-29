package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.WitchsCauldronBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WitchsCauldronRenderer extends GeoBlockRenderer<WitchsCauldronBlockEntity> {
    public WitchsCauldronRenderer() {
        super(new WitchsCauldronModel());
    }
}