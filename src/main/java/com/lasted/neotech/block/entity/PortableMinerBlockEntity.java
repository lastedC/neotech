package com.lasted.neotech.block.entity;

import com.lasted.neotech.screen.custom.PortableMinerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class PortableMinerBlockEntity extends BlockEntity implements MenuProvider {
    private int tickCounter = 0;
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Only allow items that can be produced by the portable miner recipes
            return com.lasted.neotech.recipe.PortableMiningManager.INSTANCE.isAllowedResult(stack);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return super.getStackLimit(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public PortableMinerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PORTABLE_MINER_BE.get(), pos, blockState);
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("tickCounter", tickCounter);
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tickCounter = tag.contains("tickCounter") ? tag.getInt("tickCounter") : 0;
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public int getIntervalTicks() {
        return com.lasted.neotech.Config.PORTABLE_MINER_INTERVAL_TICKS.get();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Portable Miner");
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, PortableMinerBlockEntity be) {
        if (level.isClientSide()) return;

        // Check the block below first to determine if mining is valid
        var belowState = level.getBlockState(pos.below());
        var entry = com.lasted.neotech.recipe.PortableMiningManager.INSTANCE.findMatch(belowState);

        if (entry == null) {
            // No valid block: reset/hold progress so the UI bar does not fill
            if (be.tickCounter != 0) {
                be.tickCounter = 0;
                // notify clients so the bar clears promptly
                level.sendBlockUpdated(pos, state, state, 3);
            }
            return;
        }

        // Valid target present, advance progress
        be.tickCounter++;
        int interval = com.lasted.neotech.Config.PORTABLE_MINER_INTERVAL_TICKS.get();
        if (be.tickCounter >= interval) {
            be.tickCounter = 0;
            ItemStack out = entry.result().copy();
            ItemStack slot = be.inventory.getStackInSlot(0);
            if (slot.isEmpty()) {
                be.inventory.setStackInSlot(0, out);
            } else if (ItemStack.isSameItemSameComponents(slot, out)) {
                int limit = Math.min(slot.getMaxStackSize(), be.inventory.getSlotLimit(0));
                int canAdd = Math.min(out.getCount(), limit - slot.getCount());
                if (canAdd > 0) {
                    slot.grow(canAdd);
                    be.inventory.setStackInSlot(0, slot);
                }
            }
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new PortableMinerMenu(i, inventory, this);
    }
}
