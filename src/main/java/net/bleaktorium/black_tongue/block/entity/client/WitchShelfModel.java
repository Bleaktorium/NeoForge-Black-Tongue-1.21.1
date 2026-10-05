package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.WitchShelfBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class WitchShelfModel extends GeoModel<WitchShelfBlockEntity> {
    private static final String[] STAGES = {"25-full", "50-full", "75-full", "100-full"};

    @Override
    public ResourceLocation getModelResource(WitchShelfBlockEntity shelf) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/witch_shelf.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WitchShelfBlockEntity shelf) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/witch_shelf.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WitchShelfBlockEntity shelf) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/witch_shelf.animation.json");
    }

    @Override
    public void setCustomAnimations(WitchShelfBlockEntity shelf, long instanceId,
                                    AnimationState<WitchShelfBlockEntity> animationState) {
        super.setCustomAnimations(shelf, instanceId, animationState);
        int stage = shelf.fillStage();
        for (int i = 0; i < STAGES.length; i++) {
            var bone = getAnimationProcessor().getBone(STAGES[i]);
            if (bone != null) bone.setHidden(i + 1 != stage); // stacking stages instead: i + 1 > stage
        }
    }
}