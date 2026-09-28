package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.item.menu.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public class WitchTradeMenu extends AbstractContainerMenu {

    private final String witchName;
    private final ServerPlayer serverPlayer;
    private final SimpleContainer inputContainer = new SimpleContainer(2);
    private final SimpleContainer outputContainer = new SimpleContainer(1);
    private WitchTradeOffer activeOffer = null;
    private final ContainerData reputationData = new SimpleContainerData(1);

    public WitchTradeMenu(int containerId, Inventory inv, RegistryFriendlyByteBuf buf) {
        this(containerId, inv, buf.readUtf(), null);
    }

    public WitchTradeMenu(int containerId, Inventory inv, String witchName, ServerPlayer serverPlayer) {
        super(ModMenuTypes.WITCH_TRADE.get(), containerId);
        this.witchName = witchName;
        this.serverPlayer = serverPlayer;

        if (serverPlayer != null) {
            reputationData.set(0, WitchReputationHelper.getReputation(serverPlayer, witchName));
        }
        addDataSlots(reputationData);

        this.addSlot(new Slot(inputContainer, 0, 136, 85) {
            @Override
            public void setChanged() {
                super.setChanged();
                recomputeMatch();
            }
        });
        this.addSlot(new Slot(inputContainer, 1, 162, 85) {
            @Override
            public void setChanged() {
                super.setChanged();
                recomputeMatch();
            }
        });
        this.addSlot(new Slot(outputContainer, 0, 216, 81) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                completeTrade();
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 108 + col * 18, 132 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 108 + col * 18, 190));
        }
    }

    public int getReputation() {
        return reputationData.get(0);
    }
    public String getWitchName() {
        return witchName;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == inputContainer) {
            recomputeMatch();
        }
    }

    public boolean hasActiveOffer() {
        return !this.slots.get(2).getItem().isEmpty();
    }

    private void recomputeMatch() {
        if (serverPlayer == null) return;

        ItemStack in0 = inputContainer.getItem(0);
        ItemStack in1 = inputContainer.getItem(1);

        int reputation = WitchReputationHelper.getReputation(serverPlayer, witchName);
        List<WitchTradeOffer> available = WitchTradePool.getAvailableOffers(witchName, reputation);

        activeOffer = available.stream()
                .filter(offer -> offer.matches(in0, in1))
                .findFirst()
                .orElse(null);

        outputContainer.setItem(0, activeOffer != null ? activeOffer.resolveOutput(witchName) : ItemStack.EMPTY);
    }

    private void completeTrade() {
        if (activeOffer == null || serverPlayer == null) return;

        List<ItemStack> required = activeOffer.inputs();
        inputContainer.getItem(0).shrink(required.get(0).getCount());
        if (required.size() > 1) {
            inputContainer.getItem(1).shrink(required.get(1).getCount());
        }

        WitchReputationHelper.adjustReputation(serverPlayer, witchName, 1);
            reputationData.set(0, WitchReputationHelper.getReputation(serverPlayer, witchName));

        recomputeMatch();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}