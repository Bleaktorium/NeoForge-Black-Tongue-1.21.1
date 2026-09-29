package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.ScryerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ScryerModel extends GeoModel<ScryerBlockEntity> {
    @Override
    public ResourceLocation getModelResource(ScryerBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/scryer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScryerBlockEntity animatable) {
        String suffix = textureSuffix(animatable);
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/scryer" + suffix + ".png");
    }

    private String textureSuffix(ScryerBlockEntity be) {
        if (!be.hasWater()) return "_empty";
        return switch (be.getResult()) {
            case NONE -> "_on";
            case ICE -> "_ice";
            case COLD -> "_cold";
            case LUKEWARM -> "_lukewarm";
            case WARM -> "_warm";
            case HOT -> "_hot";
            case FAILED -> "_failed";
        };
    }

    @Override
    public ResourceLocation getAnimationResource(ScryerBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/scryer.animation.json");
    }

    @Override
    public void setCustomAnimations(ScryerBlockEntity animatable, long instanceId,
                                    software.bernie.geckolib.animation.AnimationState<ScryerBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        var liquid = getAnimationProcessor().getBone("liquid");
        if (liquid != null) {
            liquid.setHidden(!animatable.hasWater());
        }
    }
}