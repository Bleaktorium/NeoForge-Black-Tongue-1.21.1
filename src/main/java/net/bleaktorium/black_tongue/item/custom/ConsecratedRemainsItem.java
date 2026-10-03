package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ConsecratedRemainsItem extends Item {
    public ConsecratedRemainsItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        RemainsData data = stack.get(ModDataComponents.REMAINS_DATA.get());
        if (data != null) {
            tooltip.add(Component.literal("Remains of: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(data.name()).withStyle(ChatFormatting.GOLD)));
            data.anointedTier().ifPresent(tier -> tooltip.add(
                    Component.literal("Consecrated while ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(tier.displayName()).withStyle(tier.color()))));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}