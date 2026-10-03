package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.EmbalmingTableBlockEntity;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EmbalmingTableRenderer extends GeoBlockRenderer<EmbalmingTableBlockEntity> {
    public EmbalmingTableRenderer() {
        super(new EmbalmingTableModel());
    }

    @Override
    public AABB getRenderBoundingBox(EmbalmingTableBlockEntity table) {
        return new AABB(table.getBlockPos()).inflate(2, 0, 2).expandTowards(0, 2, 0);
    }
}