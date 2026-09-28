package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.resources.ResourceLocation;

public record WitchIdentity(String name, ResourceLocation portrait, ResourceLocation model,
                            ResourceLocation texture, boolean isRandomlyAssignable) {

    public WitchIdentity(String name, ResourceLocation portrait, ResourceLocation model, ResourceLocation texture) {
        this(name, portrait, model, texture, true);
    }

}