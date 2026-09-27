package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.CovenlessWitchEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CovenlessWitchModel extends GeoModel<CovenlessWitchEntity> {
    @Override
    public ResourceLocation getModelResource(CovenlessWitchEntity animatable) {
        return animatable.getIdentity().model();
    }

    @Override
    public ResourceLocation getTextureResource(CovenlessWitchEntity animatable) {
        return animatable.getIdentity().texture();
    }

    @Override
    public ResourceLocation getAnimationResource(CovenlessWitchEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("black_tongue", "animations/covenless_witch.animation.json");
    }
}