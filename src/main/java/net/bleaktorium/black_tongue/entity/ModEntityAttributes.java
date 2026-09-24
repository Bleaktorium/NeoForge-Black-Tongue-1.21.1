package net.bleaktorium.black_tongue.entity;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID)
public class ModEntityAttributes {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COVEN_MOTHER.get(), CovenMotherEntity.createAttributes().build());
    }
}