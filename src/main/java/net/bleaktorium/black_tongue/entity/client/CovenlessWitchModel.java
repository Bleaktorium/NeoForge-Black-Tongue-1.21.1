package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.entity.custom.CovenlessWitchEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.data.EntityModelData;

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

    @Override
    public void setCustomAnimations(CovenlessWitchEntity animatable, long instanceId, AnimationState<CovenlessWitchEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(data.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(data.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}