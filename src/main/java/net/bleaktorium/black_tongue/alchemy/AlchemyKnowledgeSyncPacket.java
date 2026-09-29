package net.bleaktorium.black_tongue.alchemy;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AlchemyKnowledgeSyncPacket(List<ResourceLocation> knownItems) implements CustomPacketPayload {
    public static final Type<AlchemyKnowledgeSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "alchemy_knowledge_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyKnowledgeSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), AlchemyKnowledgeSyncPacket::knownItems,
            AlchemyKnowledgeSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}