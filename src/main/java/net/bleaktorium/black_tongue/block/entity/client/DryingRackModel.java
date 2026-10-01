package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.DryingRackBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DryingRackModel extends GeoModel<DryingRackBlockEntity> {
    @Override
    public ResourceLocation getModelResource(DryingRackBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/drying_string.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DryingRackBlockEntity animatable) {
        String suffix = animatable.useAltTexture() ? "2" : "";
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/drying_string" + suffix + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(DryingRackBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/drying_string.animation.json");
    }

    @Override
    public void setCustomAnimations(DryingRackBlockEntity animatable, long instanceId,
                                    software.bernie.geckolib.animation.AnimationState<DryingRackBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        hideIfEmpty(animatable, "herb1", 0);
        hideIfEmpty(animatable, "herb2", 1);
        hideIfEmpty(animatable, "herb3", 2);
    }

    private void hideIfEmpty(DryingRackBlockEntity animatable, String boneName, int slot) {
        var bone = getAnimationProcessor().getBone(boneName);
        if (bone != null) {
            bone.setHidden(animatable.getItem(slot).isEmpty());
        }
    }
}