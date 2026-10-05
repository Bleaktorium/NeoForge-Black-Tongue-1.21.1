package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.HauntingSoulEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HauntingSoulModel extends GeoModel<HauntingSoulEntity> {
    @Override
    public ResourceLocation getModelResource(HauntingSoulEntity soul) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/haunting_soul.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HauntingSoulEntity soul) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/entity/haunting_soul.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HauntingSoulEntity soul) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/haunting_soul.animation.json");
    }
}
