package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.WitchShelfBlockEntity;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WitchShelfRenderer extends GeoBlockRenderer<WitchShelfBlockEntity> {
    public WitchShelfRenderer() {
        super(new WitchShelfModel());
    }

    @Override
    public AABB getRenderBoundingBox(WitchShelfBlockEntity shelf) {
        return new AABB(shelf.getBlockPos()).expandTowards(0, 1, 0);
    }
}