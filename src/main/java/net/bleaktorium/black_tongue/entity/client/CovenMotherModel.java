package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CovenMotherModel extends GeoModel<CovenMotherEntity> {
    @Override
    public ResourceLocation getModelResource(CovenMotherEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/coven_mother_yaga.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CovenMotherEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/entity/coven_mother_yaga.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CovenMotherEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/yaga.animation.json");
    }
}