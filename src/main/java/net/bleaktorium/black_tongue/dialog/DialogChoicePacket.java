package net.bleaktorium.black_tongue.dialog;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record DialogChoicePacket(int optionIndex) implements CustomPacketPayload {
    public static final Type<DialogChoicePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "dialog_choice"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogChoicePacket> STREAM_CODEC = StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT, DialogChoicePacket::optionIndex,
            DialogChoicePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
