package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.RitualTableBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RitualTableModel extends GeoModel<RitualTableBlockEntity> {
    @Override
    public ResourceLocation getModelResource(RitualTableBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/ritual_table.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RitualTableBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/ritual_table_geo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RitualTableBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/ritual_table.animation.json");
    }
}