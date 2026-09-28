package net.bleaktorium.black_tongue.block.custom;

import net.minecraft.util.StringRepresentable;

public enum MoonPhase implements StringRepresentable {
    NEW_MOON, WAXING_CRESCENT, FIRST_QUARTER, WAXING_GIBBOUS,
    FULL_MOON, WANING_GIBBOUS, LAST_QUARTER, WANING_CRESCENT;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}