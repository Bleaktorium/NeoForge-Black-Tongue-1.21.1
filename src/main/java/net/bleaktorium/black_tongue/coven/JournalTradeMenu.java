package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.item.menu.ModMenuTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.PatchouliAPI;

public class JournalTradeMenu extends AbstractContainerMenu {

    private static final ResourceLocation GRIMOIRE_BOOK_ID =
            ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_grimoire");

    private final ServerPlayer serverPlayer;

    public JournalTradeMenu(int containerId, Inventory inv) {
        this(containerId, inv, new SimpleContainer(1), null);
    }

    public JournalTradeMenu(int containerId, Inventory inv, SimpleContainer inputContainer, ServerPlayer serverPlayer) {
        super(ModMenuTypes.JOURNAL_TRADE.get(), containerId);
        this.serverPlayer = serverPlayer;

        this.addSlot(new Slot(inputContainer, 0, 80, 30) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CovenDialogChecks.isAncientTomeStack(stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (serverPlayer == null) return;

        Slot inputSlot = this.slots.get(0);
        ItemStack held = inputSlot.getItem();

        if (!held.isEmpty() && CovenDialogChecks.isAncientTomeStack(held)) {
            inputSlot.set(ItemStack.EMPTY); // consume the journal

            ItemStack grimoire = PatchouliAPI.get().getBookStack(GRIMOIRE_BOOK_ID);
            if (!serverPlayer.getInventory().add(grimoire)) {
                serverPlayer.drop(grimoire, false);
            }

            CovenPlayerData newData = new CovenPlayerData(CovenRelationshipState.GRIMOIRE_RECEIVED, java.util.List.of());
            serverPlayer.setData(ModAttachments.COVEN_DATA.get(), newData);

            serverPlayer.closeContainer();
        }
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