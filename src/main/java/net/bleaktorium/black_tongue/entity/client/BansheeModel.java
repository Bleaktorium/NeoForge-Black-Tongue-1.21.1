package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.BansheeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BansheeModel extends GeoModel<BansheeEntity> {
    @Override
    public ResourceLocation getModelResource(BansheeEntity banshee) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/banshee.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BansheeEntity banshee) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/entity/banshee.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BansheeEntity banshee) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/banshee.animation.json");
    }

    @Override
    public void setCustomAnimations(BansheeEntity banshee, long instanceId, AnimationState<BansheeEntity> animationState) {
        super.setCustomAnimations(banshee, instanceId, animationState);
        var hair = getAnimationProcessor().getBone("hair");
        if (hair != null) hair.setHidden(!banshee.canBeCombed());
    }
}