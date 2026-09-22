package net.bleaktorium.black_tongue.block.custom;

import net.minecraft.util.StringRepresentable;

public enum RuneType implements StringRepresentable {
    BLANK, NESTING, MOON_PHASE, POTENCY;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}