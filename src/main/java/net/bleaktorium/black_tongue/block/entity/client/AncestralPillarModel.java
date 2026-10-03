package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.AncestralPillarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class AncestralPillarModel extends GeoModel<AncestralPillarBlockEntity> {
    @Override
    public ResourceLocation getModelResource(AncestralPillarBlockEntity a) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/ancestral_pillar.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(AncestralPillarBlockEntity a) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/ancestral_pillar.png");
    }
    @Override
    public ResourceLocation getAnimationResource(AncestralPillarBlockEntity a) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/ancestral_pillar.animation.json");
    }

    @Override
    public void setCustomAnimations(AncestralPillarBlockEntity pillar, long instanceId,
                                    AnimationState<AncestralPillarBlockEntity> animationState) {
        super.setCustomAnimations(pillar, instanceId, animationState);
        var occupant = pillar.getOccupant();
        show("chains", occupant != AncestralPillarBlockEntity.Occupant.NONE);
        show("witch", occupant == AncestralPillarBlockEntity.Occupant.WITCH);
        show("ancestor", occupant == AncestralPillarBlockEntity.Occupant.ANCESTOR);
    }

    private void show(String boneName, boolean visible) {
        var bone = getAnimationProcessor().getBone(boneName);
        if (bone != null) bone.setHidden(!visible);
    }
}