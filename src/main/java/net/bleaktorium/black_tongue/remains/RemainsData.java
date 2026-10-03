package net.bleaktorium.black_tongue.remains;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RemainsData(Origin origin, String name, long dayCollected, Optional<DecayTier> anointedTier) {

    public enum Origin {
        ANCESTOR, WITCH;
        public static final Codec<Origin> CODEC = Codec.STRING.xmap(Origin::valueOf, Enum::name);
        public static final StreamCodec<ByteBuf, Origin> STREAM_CODEC =
                ByteBufCodecs.STRING_UTF8.map(Origin::valueOf, Enum::name);
    }

    public enum DecayTier {
        FRESH("Fresh", ChatFormatting.GREEN),
        ROTTEN("Rotten", ChatFormatting.YELLOW),
        CRUMBLING("Crumbling", ChatFormatting.RED),
        DUST("Ancient Dust", ChatFormatting.DARK_GRAY);

        private final String displayName;
        private final ChatFormatting color;

        DecayTier(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String displayName() { return displayName; }
        public ChatFormatting color() { return color; }

        public static DecayTier fromDaysPassed(long daysPassed) {
            if (daysPassed <= 0) return FRESH;
            if (daysPassed == 1) return ROTTEN;
            if (daysPassed == 2) return CRUMBLING;
            return DUST;
        }

        public static final Codec<DecayTier> CODEC = Codec.STRING.xmap(DecayTier::valueOf, Enum::name);
        public static final StreamCodec<ByteBuf, DecayTier> STREAM_CODEC =
                ByteBufCodecs.STRING_UTF8.map(DecayTier::valueOf, Enum::name);
    }

    public static final Codec<RemainsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Origin.CODEC.fieldOf("origin").forGetter(RemainsData::origin),
            Codec.STRING.fieldOf("name").forGetter(RemainsData::name),
            Codec.LONG.fieldOf("day_collected").forGetter(RemainsData::dayCollected),
            DecayTier.CODEC.optionalFieldOf("anointed_tier").forGetter(RemainsData::anointedTier)
    ).apply(instance, RemainsData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemainsData> STREAM_CODEC = StreamCodec.composite(
            Origin.STREAM_CODEC, RemainsData::origin,
            ByteBufCodecs.STRING_UTF8, RemainsData::name,
            ByteBufCodecs.VAR_LONG, RemainsData::dayCollected,
            ByteBufCodecs.optional(DecayTier.STREAM_CODEC), RemainsData::anointedTier,
            RemainsData::new
    );

    public static long currentDay(Level level) {
        return level.getDayTime() / 24000L;
    }

    public static RemainsData collectedNow(Origin origin, String name, Level level) {
        return new RemainsData(origin, name, currentDay(level), Optional.empty());
    }

    public DecayTier tier(Level level) {
        if (anointedTier.isPresent()) return anointedTier.get();
        return DecayTier.fromDaysPassed(currentDay(level) - dayCollected);
    }
}
