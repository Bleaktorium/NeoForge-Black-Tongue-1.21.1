package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.PagankaRootCropBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PagankaRootCropModel extends GeoModel<PagankaRootCropBlockEntity> {
    @Override
    public ResourceLocation getModelResource(PagankaRootCropBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/paganka_root.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PagankaRootCropBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/paganka_root.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PagankaRootCropBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/pagankaroot.animation.json");
    }

    @Override
    public void setCustomAnimations(PagankaRootCropBlockEntity animatable, long instanceId,
                                    software.bernie.geckolib.animation.AnimationState<PagankaRootCropBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        int currentStage = animatable.getAge() + 1;
        for (int i = 1; i <= 6; i++) {
            var bone = getAnimationProcessor().getBone("stage" + i);
            if (bone != null) {
                bone.setHidden(i != currentStage);
            }
        }
    }
}