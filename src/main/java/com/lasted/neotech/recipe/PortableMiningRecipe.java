package com.lasted.neotech.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

// Deprecated placeholder to avoid breaking changes; not used by logic.
public class PortableMiningRecipe {
    private final ResourceLocation id;
    private final ItemStack result;
    private final TagKey<Block> blockTag; // optional
    private final List<Block> blocks;     // optional

    public PortableMiningRecipe(ResourceLocation id, ItemStack result, TagKey<Block> blockTag, List<Block> blocks) {
        this.id = id;
        this.result = result;
        this.blockTag = blockTag;
        this.blocks = blocks;
    }

    public boolean matchesBlock(BlockState state) {
        if (blockTag != null && state.is(blockTag)) return true;
        if (blocks != null) {
            for (Block b : blocks) if (state.is(b)) return true;
        }
        return false;
    }

    public ItemStack getResultItemCopy() { return result.copy(); }

    public ResourceLocation getId() { return id; }
}
