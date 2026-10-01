package net.bleaktorium.black_tongue.block.custom;

import net.bleaktorium.black_tongue.block.entity.DryingRackBlockEntity;
import net.bleaktorium.black_tongue.farming.HerbDryingProfiles;
import net.bleaktorium.black_tongue.item.menu.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DryingRackMenu extends AbstractContainerMenu {

    private final DryingRackBlockEntity rack;

    public DryingRackMenu(int containerId, Inventory inv, DryingRackBlockEntity rack) {
        super(ModMenuTypes.DRYING_RACK.get(), containerId);
        this.rack = rack;

        addSlot(new Slot(rack, 0, 62, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return HerbDryingProfiles.canDry(stack.getItem());
            }
        });
        addSlot(new Slot(rack, 1, 80, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return HerbDryingProfiles.canDry(stack.getItem());
            }
        });
        addSlot(new Slot(rack, 2, 98, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return HerbDryingProfiles.canDry(stack.getItem());
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 109));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return rack.stillValid(player);
    }
}