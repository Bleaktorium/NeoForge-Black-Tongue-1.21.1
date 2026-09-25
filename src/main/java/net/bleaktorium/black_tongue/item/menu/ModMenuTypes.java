package net.bleaktorium.black_tongue.item.menu;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.coven.JournalTradeMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, Black_Tongue.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<JournalTradeMenu>> JOURNAL_TRADE =
            MENU_TYPES.register("journal_trade", () -> new MenuType<>(JournalTradeMenu::new, FeatureFlags.DEFAULT_FLAGS));
}