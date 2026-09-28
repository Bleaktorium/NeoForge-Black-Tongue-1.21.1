package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.resources.ResourceLocation;

public record WitchIdentity(String name, ResourceLocation portrait, ResourceLocation model, ResourceLocation texture,
                            boolean isRandomlyAssignable, int ritualAmplification, int ritualStability,
                            boolean covenMotherTier) {

    public static final int DEFAULT_AMPLIFICATION = 20;
    public static final int DEFAULT_STABILITY = 80;

    public WitchIdentity(String name, ResourceLocation portrait, ResourceLocation model, ResourceLocation texture) {
        this(name, portrait, model, texture, true, DEFAULT_AMPLIFICATION, DEFAULT_STABILITY, false);
    }

    public WitchIdentity(String name, ResourceLocation portrait, ResourceLocation model, ResourceLocation texture,
                         boolean isRandomlyAssignable) {
        this(name, portrait, model, texture, isRandomlyAssignable, DEFAULT_AMPLIFICATION, DEFAULT_STABILITY, false);
    }

    public WitchIdentity(String name, ResourceLocation portrait, ResourceLocation model, ResourceLocation texture,
                         boolean isRandomlyAssignable, int ritualAmplification, int ritualStability) {
        this(name, portrait, model, texture, isRandomlyAssignable, ritualAmplification, ritualStability, false);
    }
}