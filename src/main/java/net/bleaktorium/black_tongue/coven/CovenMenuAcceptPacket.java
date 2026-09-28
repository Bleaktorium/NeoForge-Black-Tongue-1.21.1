package net.bleaktorium.black_tongue.coven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record CovenMenuAcceptPacket(String covenName, List<CovenMember> extras) implements CustomPacketPayload {

    public static final Type<CovenMenuAcceptPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_menu_accept"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenMenuAcceptPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(64), CovenMenuAcceptPacket::covenName,
            CovenMember.STREAM_CODEC.apply(ByteBufCodecs.list(16)), CovenMenuAcceptPacket::extras,
            CovenMenuAcceptPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}