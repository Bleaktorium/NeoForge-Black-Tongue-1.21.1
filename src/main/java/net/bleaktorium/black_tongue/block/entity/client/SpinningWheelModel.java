package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.SpinningWheelBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SpinningWheelModel extends GeoModel<SpinningWheelBlockEntity> {

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, path);
    }

    @Override
    public ResourceLocation getModelResource(SpinningWheelBlockEntity wheel) {
        return id("geo/spinning_wheel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpinningWheelBlockEntity wheel) {
        SpinningWheelBlockEntity.Fiber fiber = wheel.getFiber();
        String suffix = fiber == null ? "empty" : fiber.textureSuffix();
        return id("textures/block/spinning_wheel_" + suffix + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpinningWheelBlockEntity wheel) {
        return id("animations/spinning_wheel.animation.json");
    }

    @Override
    public void setCustomAnimations(SpinningWheelBlockEntity wheel, long instanceId,
                                    AnimationState<SpinningWheelBlockEntity> animationState) {
        super.setCustomAnimations(wheel, instanceId, animationState);
        boolean threaded = wheel.isSpinning() || wheel.isReady();
        show("threadUp", threaded);
        show("threadDown", threaded);
    }

    private void show(String boneName, boolean visible) {
        var bone = getAnimationProcessor().getBone(boneName);
        if (bone != null) bone.setHidden(!visible);
    }
}