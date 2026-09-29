package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.MortarAndPestleBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MortarAndPestleModel extends GeoModel<MortarAndPestleBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MortarAndPestleBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/mortar_pestle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MortarAndPestleBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/mortar_pestle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MortarAndPestleBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/mortar_pestle.animation.json");
    }

    @Override
    public void setCustomAnimations(MortarAndPestleBlockEntity animatable, long instanceId,
                                    software.bernie.geckolib.animation.AnimationState<MortarAndPestleBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        var herb = getAnimationProcessor().getBone("herb");
        if (herb != null) {
            herb.setHidden(!animatable.hasItem());
        }
    }
}