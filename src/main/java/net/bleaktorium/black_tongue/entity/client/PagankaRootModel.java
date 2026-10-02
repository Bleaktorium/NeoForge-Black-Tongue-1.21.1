package net.bleaktorium.black_tongue.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.PagankaRootEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PagankaRootModel extends GeoModel<PagankaRootEntity> {
    @Override
    public ResourceLocation getModelResource(PagankaRootEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/paganka_root_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PagankaRootEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/entity/paganka_entity.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PagankaRootEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/pagankaroot_entity.animation.json");
    }
}