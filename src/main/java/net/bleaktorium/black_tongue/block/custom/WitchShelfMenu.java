package net.bleaktorium.black_tongue.block.custom;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.WitchShelfBlockEntity;
import net.bleaktorium.black_tongue.item.menu.ModMenuTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class WitchShelfMenu extends AbstractContainerMenu {
    public static final int COLUMNS = 9;
    public static final int VISIBLE_ROWS = 4;
    public static final int VIEW_SLOTS = COLUMNS * VISIBLE_ROWS; // the 36 slots you can see
    public static final int SCROLL_BUTTON = 100; // button id 100 + n means "scroll to row n"
    // button ids 0-3 pick a tab

    // Vanilla items that are allowed on the shelf (data/black_tongue/tags/item/witch_shelf_extras.json)
    public static final TagKey<Item> EXTRAS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "witch_shelf_extras"));

    @Nullable
    private final WitchShelfBlockEntity shelf; // only known on the server
    private static final Container NONE = new SimpleContainer(0); // shelf slots don't use a vanilla container
    private final int[] view = new int[VIEW_SLOTS]; // which storage slot each visible slot shows
    private final ContainerData data; // 0 = scroll row, 1 = total rows, 2 = tab
    private int scrollRow = 0;
    private int totalRows = 0;
    private ShelfCategory tab = ShelfCategory.ALL;

    // Client side: the visible slots are a plain 36-slot mirror the server fills in.
    public WitchShelfMenu(int containerId, Inventory inv) {
        this(containerId, inv, null, new ItemStackHandler(VIEW_SLOTS));
    }

    // Server side: the visible slots point into the real shelf storage.
    public WitchShelfMenu(int containerId, Inventory inv, WitchShelfBlockEntity shelf) {
        this(containerId, inv, shelf, shelf.getStorage());
    }

    private WitchShelfMenu(int containerId, Inventory inv, @Nullable WitchShelfBlockEntity shelf, IItemHandlerModifiable backing) {
        super(ModMenuTypes.WITCH_SHELF.get(), containerId);
        this.shelf = shelf;
        this.data = shelf == null ? new SimpleContainerData(3) : new ContainerData() {
            @Override public int get(int i) {
                return switch (i) {
                    case 0 -> scrollRow;
                    case 1 -> totalRows;
                    default -> tab.ordinal();
                };
            }
            @Override public void set(int i, int value) { }
            @Override public int getCount() { return 3; }
        };
        addDataSlots(data);

        for (int i = 0; i < VIEW_SLOTS; i++) {
            final int viewIndex = i;
            IntSupplier index = shelf == null ? () -> viewIndex : () -> view[viewIndex];
            Runnable changed = shelf == null ? () -> { } : shelf::setChanged;
            addSlot(new ShelfSlot(backing, index, changed, 8 + (i % COLUMNS) * 18, 36 + (i / COLUMNS) * 18));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 122 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 180));
        }

        rebuildView();
    }

    // Black Tongue items, plus any vanilla items listed in the extras tag.
    public static boolean accepts(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(Black_Tongue.MOD_ID)
                || stack.is(EXTRAS);
    }

    // Works out which storage slots the current tab shows, in order.
    // "All" shows every slot. A category shows its items first, then empty slots to put new ones in.
    private void rebuildView() {
        if (shelf == null) return;
        List<Integer> shown = new ArrayList<>();
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < WitchShelfBlockEntity.SIZE; i++) {
            ItemStack stack = shelf.getStorage().getStackInSlot(i);
            if (tab == ShelfCategory.ALL) shown.add(i);
            else if (stack.isEmpty()) empty.add(i);
            else if (tab.matches(stack)) shown.add(i);
        }
        shown.addAll(empty);

        totalRows = (shown.size() + COLUMNS - 1) / COLUMNS;
        scrollRow = Mth.clamp(scrollRow, 0, Math.max(0, totalRows - VISIBLE_ROWS));
        for (int i = 0; i < VIEW_SLOTS; i++) {
            int pos = scrollRow * COLUMNS + i;
            view[i] = pos < shown.size() ? shown.get(pos) : -1;
        }
    }

    // Items move between tabs as they come and go, so re-sort before every sync.
    @Override
    public void broadcastChanges() {
        rebuildView();
        super.broadcastChanges();
    }

    public int scrollRow() { return data.get(0); }

    public int maxScroll() { return Math.max(0, data.get(1) - VISIBLE_ROWS); }

    public ShelfCategory tab() { return ShelfCategory.values()[Mth.clamp(data.get(2), 0, ShelfCategory.values().length - 1)]; }

    // The screen calls these right away so it reacts before the server answers.
    public void setScrollRowClient(int row) { setData(0, row); }

    public void setTabClient(ShelfCategory category) {
        setData(2, category.ordinal());
        setData(0, 0);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= SCROLL_BUTTON) {
            scrollRow = id - SCROLL_BUTTON;
            rebuildView();
            return true;
        }
        if (id >= 0 && id < ShelfCategory.values().length) {
            tab = ShelfCategory.values()[id];
            scrollRow = 0;
            rebuildView();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < VIEW_SLOTS) {
            // Shelf -> player
            if (!moveItemStackTo(stack, VIEW_SLOTS, VIEW_SLOTS + 36, true)) return ItemStack.EMPTY;
        } else {
            // Player -> shelf. Goes into the whole shelf, not just the rows on screen.
            if (shelf == null || !accepts(stack)) return ItemStack.EMPTY;
            ItemStack left = ItemHandlerHelper.insertItemStacked(shelf.getStorage(), stack.copy(), false);
            if (left.getCount() == stack.getCount()) return ItemStack.EMPTY;
            stack.setCount(left.getCount());
        }

        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return shelf != null && Container.stillValidBlockEntity(shelf, player);
    }

    // A slot whose storage index can change, so 36 slots can show any part of the 108.
    private class ShelfSlot extends Slot {
        private final IItemHandlerModifiable handler;
        private final IntSupplier index;
        private final Runnable changed;

        ShelfSlot(IItemHandlerModifiable handler, IntSupplier index, Runnable changed, int x, int y) {
            super(NONE, 0, x, y);
            this.handler = handler;
            this.index = index;
            this.changed = changed;
        }

        @Override
        public ItemStack getItem() {
            int i = index.getAsInt();
            return i < 0 ? ItemStack.EMPTY : handler.getStackInSlot(i);
        }

        @Override
        public void set(ItemStack stack) {
            int i = index.getAsInt();
            if (i >= 0) handler.setStackInSlot(i, stack);
            setChanged();
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            set(newStack);
        }

        @Override
        public ItemStack remove(int amount) {
            int i = index.getAsInt();
            return i < 0 ? ItemStack.EMPTY : handler.extractItem(i, amount, false);
        }

        @Override
        public void setChanged() {
            changed.run();
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return index.getAsInt() >= 0 && accepts(stack) && tab().matches(stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            return index.getAsInt() >= 0;
        }

        @Override
        public boolean isActive() {
            return index.getAsInt() >= 0;
        }
    }
}