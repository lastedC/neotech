package com.lasted.neotech.item;

import com.lasted.neotech.util.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier LEAD = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_LEAD_TOOL,
            1350, 5f, 2f, 12, () -> Ingredient.of(ModItems.LEAD_INGOT));
}
