package net.bleaktorium.black_tongue.coven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public record AmuletBinding(SummonedWitchType type, Optional<String> witchName, Optional<UUID> soulId) {
    private static final Codec<SummonedWitchType> TYPE_CODEC =
            Codec.STRING.xmap(SummonedWitchType::valueOf, Enum::name);

    public AmuletBinding(SummonedWitchType type, Optional<String> witchName) {
        this(type, witchName, Optional.empty());
    }

    public static final Codec<AmuletBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TYPE_CODEC.fieldOf("type").forGetter(AmuletBinding::type),
            Codec.STRING.optionalFieldOf("witch_name").forGetter(AmuletBinding::witchName),
            UUIDUtil.CODEC.optionalFieldOf("soul_id").forGetter(AmuletBinding::soulId)
    ).apply(instance, AmuletBinding::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AmuletBinding> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(SummonedWitchType::valueOf, Enum::name), AmuletBinding::type,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), AmuletBinding::witchName,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), AmuletBinding::soulId,
            AmuletBinding::new
    );

    public String displayName() {
        return switch (type()) {
            case COVEN_MOTHER -> "Coven Mother Yaga";
            case COVENLESS_WITCH -> witchName().orElse("an unknown witch");
        };
    }
}