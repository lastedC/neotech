package com.lasted.neotech.screen.custom;

import com.lasted.neotech.block.ModBlocks;
import com.lasted.neotech.block.entity.PortableMinerBlockEntity;
import com.lasted.neotech.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PortableMinerMenu extends AbstractContainerMenu {
    public final PortableMinerBlockEntity blockEntity;
    private final Level level;
    private int clientTickCounter;
    private int clientIntervalTicks;

    public PortableMinerMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public PortableMinerMenu(int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(ModMenuTypes.PORTABLE_MINER_MENU.get(), containerId);
        this.blockEntity = (PortableMinerBlockEntity) blockEntity;
        this.level = inventory.player.level();

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 80, 35));

        // Sync tick counter and interval to client via DataSlots
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return PortableMinerMenu.this.blockEntity.getTickCounter();
            }

            @Override
            public void set(int value) {
                clientTickCounter = value;
            }
        });
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return PortableMinerMenu.this.blockEntity.getIntervalTicks();
            }

            @Override
            public void set(int value) {
                clientIntervalTicks = value;
            }
        });
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots and the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0-8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0-8)
    //  9-35 = player inventory slots (which map to the InventoryPlayer slot numbers 9-35)
    //  36-44 = TileInventory slots, which map to our TileEntity slot numbers 0-8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 1;  // must be the number of slots you have!

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.PORTABLE_MINER.get());
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    public float getProgressFraction() {
        int interval = Math.max(1, clientIntervalTicks);
        int ticks = Math.max(0, Math.min(clientTickCounter, interval));
        return (float) ticks / (float) interval;
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }
}
