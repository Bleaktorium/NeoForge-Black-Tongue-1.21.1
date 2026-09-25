package net.bleaktorium.black_tongue.coven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record CovenPlayerData(CovenRelationshipState state, List<ResourceLocation> assignedPotions) {

    private static final Codec<CovenRelationshipState> STATE_CODEC =
            Codec.STRING.xmap(CovenRelationshipState::valueOf, Enum::name);

    public static final Codec<CovenPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            STATE_CODEC.fieldOf("state").forGetter(CovenPlayerData::state),
            ResourceLocation.CODEC.listOf().fieldOf("assigned_potions").forGetter(CovenPlayerData::assignedPotions)
    ).apply(instance, CovenPlayerData::new));

    public static CovenPlayerData initial() {
        return new CovenPlayerData(CovenRelationshipState.NEVER_ASKED, List.of());
    }
}