package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherCatEntity;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CovenMotherCatModel extends GeoModel<CovenMotherCatEntity> {
    @Override
    public ResourceLocation getModelResource(CovenMotherCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/yaga_cat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CovenMotherCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/entity/yaga_cat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CovenMotherCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/yaga_cat.animation.json");
    }
}