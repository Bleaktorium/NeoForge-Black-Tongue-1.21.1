package net.bleaktorium.black_tongue.coven;

import com.mojang.serialization.Codec;
import net.bleaktorium.black_tongue.Black_Tongue;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class ModAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Black_Tongue.MOD_ID);

    private static final Codec<CovenRelationshipState> STATE_CODEC =
            Codec.STRING.xmap(CovenRelationshipState::valueOf, Enum::name);

    public static final Supplier<AttachmentType<CovenRelationshipState>> COVEN_RELATIONSHIP =
            ATTACHMENT_TYPES.register("coven_relationship", () -> AttachmentType
                    .builder(() -> CovenRelationshipState.NEVER_ASKED) // default for a player who's never talked to her
                    .serialize(STATE_CODEC)            // makes it actually persist in the save file
                    .build());

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}