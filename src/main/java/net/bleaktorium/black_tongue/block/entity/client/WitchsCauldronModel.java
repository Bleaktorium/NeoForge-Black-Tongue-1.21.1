package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.WitchsCauldronBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WitchsCauldronModel extends GeoModel<WitchsCauldronBlockEntity> {
    @Override
    public ResourceLocation getModelResource(WitchsCauldronBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/witchs_cauldron.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WitchsCauldronBlockEntity animatable) {
        String suffix;
        if (animatable.isDirty()) {
            suffix = "_dirty";
        } else if (!animatable.isActive()) {
            suffix = "";
        } else {
            suffix = "_" + temperatureName(animatable.getCurrentTemperature());
        }
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/witchs_cauldron" + suffix + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(WitchsCauldronBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/witchs_cauldron.animation.json");
    }


    private static String temperatureName(int temp) {
        temp = Math.max(-3, Math.min(3, temp));
        return switch (temp) {
            case -3 -> "frozen";
            case -2 -> "ice";
            case -1 -> "cold";
            case 0 -> "lukewarm";
            case 1 -> "warm";
            case 2 -> "hot";
            default -> "molten"; // 3
        };
    }

    @Override
    public void setCustomAnimations(WitchsCauldronBlockEntity animatable, long instanceId,
                                    software.bernie.geckolib.animation.AnimationState<WitchsCauldronBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        var water = getAnimationProcessor().getBone("cauldron_water");
        if (water != null) {
            water.setHidden(!animatable.isActive());
        }
    }
}