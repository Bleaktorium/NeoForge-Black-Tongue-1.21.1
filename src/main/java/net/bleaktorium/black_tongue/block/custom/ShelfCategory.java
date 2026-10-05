package net.bleaktorium.black_tongue.block.custom;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.item.custom.OilItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public enum ShelfCategory {
    ALL("all"),
    REAGENTS("reagents"),
    CONSUMABLES("consumables"),
    EQUIPMENT("equipment");

    // Optional tag files that override the automatic sorting below.
    public static final TagKey<Item> REAGENTS_TAG = tag("shelf_reagents");
    public static final TagKey<Item> CONSUMABLES_TAG = tag("shelf_consumables");
    public static final TagKey<Item> EQUIPMENT_TAG = tag("shelf_equipment");

    public final String name;

    ShelfCategory(String name) {
        this.name = name;
    }

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, path));
    }

    public static ShelfCategory of(ItemStack stack) {
        if (stack.is(EQUIPMENT_TAG)) return EQUIPMENT;
        if (stack.is(CONSUMABLES_TAG)) return CONSUMABLES;
        if (stack.is(REAGENTS_TAG)) return REAGENTS;

        // Tools, armor, amulets, combs: anything that doesn't stack or wears out.
        if (stack.getMaxStackSize() == 1 || stack.isDamageableItem()) return EQUIPMENT;
        // Things you eat, drink or pour.
        UseAnim anim = stack.getUseAnimation();
        if (stack.has(DataComponents.FOOD) || anim == UseAnim.DRINK || anim == UseAnim.EAT
                || stack.getItem() instanceof OilItem) return CONSUMABLES;
        // Everything else is an ingredient.
        return REAGENTS;
    }

    public boolean matches(ItemStack stack) {
        return this == ALL || of(stack) == this;
    }
}