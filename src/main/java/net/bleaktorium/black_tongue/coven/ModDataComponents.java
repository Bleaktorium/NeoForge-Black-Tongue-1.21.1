package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Black_Tongue.MOD_ID);

    public static final Supplier<DataComponentType<AmuletBinding>> AMULET_BINDING = DATA_COMPONENTS.register(
            "amulet_binding", () -> DataComponentType.<AmuletBinding>builder()
                    .persistent(AmuletBinding.CODEC)
                    .networkSynchronized(AmuletBinding.STREAM_CODEC)
                    .build());
}