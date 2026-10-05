package net.bleaktorium.black_tongue.item.menu;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.custom.DryingRackMenu;
import net.bleaktorium.black_tongue.block.custom.WitchShelfMenu;
import net.bleaktorium.black_tongue.block.entity.DryingRackBlockEntity;
import net.bleaktorium.black_tongue.coven.JournalTradeMenu;
import net.bleaktorium.black_tongue.coven.WitchTradeMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, Black_Tongue.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<JournalTradeMenu>> JOURNAL_TRADE =
            MENU_TYPES.register("journal_trade", () -> new MenuType<>(JournalTradeMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<WitchTradeMenu>> WITCH_TRADE =
            MENU_TYPES.register("witch_trade", () -> IMenuTypeExtension.create(WitchTradeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DryingRackMenu>> DRYING_RACK =
            MENU_TYPES.register("drying_rack", () -> IMenuTypeExtension.create(
                    (containerId, inv, buf) -> new DryingRackMenu(containerId, inv,
                            (DryingRackBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()))));

    public static final DeferredHolder<MenuType<?>, MenuType<WitchShelfMenu>> WITCH_SHELF =
            MENU_TYPES.register("witch_shelf", () -> new MenuType<>(WitchShelfMenu::new, FeatureFlags.DEFAULT_FLAGS));
}