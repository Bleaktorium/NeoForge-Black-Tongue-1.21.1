package net.bleaktorium.black_tongue.coven;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;

public class CovenDialogChecks {

    public static boolean isHoldingAssignedPotion(ServerPlayer player) {
        return findAssignedPotionInHand(player) != null;
    }
    private static final ResourceLocation ANCIENT_TOME_BOOK_ID =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "ancient_tome");

    private static final ResourceLocation PATCHOULI_BOOK_COMPONENT_ID =
            ResourceLocation.fromNamespaceAndPath("patchouli", "book");

    public static ItemStack findAssignedPotionInHand(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();
        boolean isPotionItem = held.is(Items.POTION) || held.is(Items.SPLASH_POTION);
        if (!isPotionItem) return null;

        PotionContents contents = held.get(DataComponents.POTION_CONTENTS);
        if (contents == null || contents.potion().isEmpty()) return null;

        ResourceLocation heldPotionId = BuiltInRegistries.POTION.getKey(contents.potion().get().value());

        CovenPlayerData data = player.getData(ModAttachments.COVEN_DATA.get());
        return data.assignedPotions().contains(heldPotionId) ? held : null;
    }

    public static boolean isHoldingAncientJournal(ServerPlayer player) {
        return isAncientTomeStack(player.getMainHandItem());
    }

    public static boolean isAncientTomeStack(ItemStack stack) {
        DataComponentType<?> bookComponent = BuiltInRegistries.DATA_COMPONENT_TYPE.get(PATCHOULI_BOOK_COMPONENT_ID);
        if (bookComponent == null) return false;

        Object rawValue = stack.get(bookComponent);
        return rawValue instanceof ResourceLocation bookId && ANCIENT_TOME_BOOK_ID.equals(bookId);
    }
}