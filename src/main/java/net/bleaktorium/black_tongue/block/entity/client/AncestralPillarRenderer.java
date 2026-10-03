package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.block.entity.AncestralPillarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AncestralPillarRenderer extends GeoBlockRenderer<AncestralPillarBlockEntity> {
    public AncestralPillarRenderer() {
        super(new AncestralPillarModel()); }

    @Override
    public AABB getRenderBoundingBox(AncestralPillarBlockEntity pillar) {
        BlockPos p = pillar.getBlockPos();
        return new AABB(p.getX() - 2, p.getY(), p.getZ() - 2, p.getX() + 3, p.getY() + 4, p.getZ() + 3);
    }
}