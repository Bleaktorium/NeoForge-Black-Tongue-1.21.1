package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class RemainsItem extends Item {
    private final RemainsData.Origin origin;

    public RemainsItem(RemainsData.Origin origin, Properties properties) {
        super(properties);
        this.origin = origin;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!stack.has(ModDataComponents.REMAINS_DATA.get())) {
            String name = origin == RemainsData.Origin.ANCESTOR ? "Unknown Ancestor" : "Unknown Witch";
            stack.set(ModDataComponents.REMAINS_DATA.get(), RemainsData.collectedNow(origin, name, level));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        RemainsData data = stack.get(ModDataComponents.REMAINS_DATA.get());

        if (data == null) {
            tooltip.add(Component.literal("Nameless bones.").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Remains of: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(data.name()).withStyle(ChatFormatting.GOLD)));

            Level level = context.level();
            if (level != null) {
                RemainsData.DecayTier tier = data.tier(level);
                MutableComponent line = Component.literal("Condition: ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(tier.displayName()).withStyle(tier.color()));
                if (data.anointedTier().isPresent()) {
                    line.append(Component.literal(" (Anointed)").withStyle(ChatFormatting.AQUA));
                }
                tooltip.add(line);
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}