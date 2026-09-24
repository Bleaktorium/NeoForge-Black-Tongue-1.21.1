package net.bleaktorium.black_tongue.entity;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Black_Tongue.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<CovenMotherEntity>> COVEN_MOTHER =
            ENTITY_TYPES.register("coven_mother", () -> EntityType.Builder.of(CovenMotherEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f) // width/height — placeholder, adjust to match her actual model bounds
                    .build("coven_mother"));
}