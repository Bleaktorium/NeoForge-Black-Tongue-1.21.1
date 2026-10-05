package net.bleaktorium.black_tongue.entity;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.*;
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
                    .sized(0.6f, 1.95f)
                    .build("coven_mother"));

    public static final DeferredHolder<EntityType<?>, EntityType<CovenlessWitchEntity>> COVENLESS_WITCH =
            ENTITY_TYPES.register("covenless_witch", () -> EntityType.Builder.of(CovenlessWitchEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build("covenless_witch"));

    public static final DeferredHolder<EntityType<?>, EntityType<CovenHutEntity>> COVEN_HUT =
            ENTITY_TYPES.register("coven_hut", () -> EntityType.Builder.of(CovenHutEntity::new, MobCategory.CREATURE)
                    .sized(3.0f, 8.0f)
                    .build("coven_hut"));

    public static final DeferredHolder<EntityType<?>, EntityType<CovenMotherCatEntity>> COVEN_MOTHER_CAT =
            ENTITY_TYPES.register("coven_mother_cat", () -> EntityType.Builder.of(CovenMotherCatEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .build("coven_mother_cat"));

    public static final DeferredHolder<EntityType<?>, EntityType<PagankaRootEntity>> PAGANKA_ROOT =
            ENTITY_TYPES.register("paganka_root", () -> EntityType.Builder.of(PagankaRootEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.6f)
                    .build("paganka_root"));

    public static final DeferredHolder<EntityType<?>, EntityType<HauntingSoulEntity>> HAUNTING_SOUL =
            ENTITY_TYPES.register("haunting_soul", () -> EntityType.Builder.of(HauntingSoulEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.3f) // covers the floating head
                    .build("haunting_soul"));

    public static final DeferredHolder<EntityType<?>, EntityType<BansheeEntity>> BANSHEE =
            ENTITY_TYPES.register("banshee", () -> EntityType.Builder.of(BansheeEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.9f)
                    .build("banshee"));
}