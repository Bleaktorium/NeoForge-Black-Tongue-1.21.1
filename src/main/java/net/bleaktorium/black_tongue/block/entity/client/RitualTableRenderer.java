package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.RitualTableBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RitualTableRenderer extends GeoBlockRenderer<RitualTableBlockEntity> {
    public RitualTableRenderer() {
        super(new RitualTableModel());
    }
}