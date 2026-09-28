package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.CovenHutEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CovenHutModel extends GeoModel<CovenHutEntity> {
    @Override
    public ResourceLocation getModelResource(CovenHutEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/cabin_chicken.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CovenHutEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/entity/cabin_chicken.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CovenHutEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/cabin_chicken.animation.json");
    }
}