package net.bleaktorium.black_tongue.dialog;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DialogOptionView(String label, boolean marksQuest) {
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogOptionView> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DialogOptionView::label,
            ByteBufCodecs.BOOL, DialogOptionView::marksQuest,
            DialogOptionView::new
    );
}