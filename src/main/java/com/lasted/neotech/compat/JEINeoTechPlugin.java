package com.lasted.neotech.compat;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.block.ModBlocks;
import com.lasted.neotech.recipe.PortableMiningManager;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEINeoTechPlugin implements IModPlugin {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PortableMinerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<PortableMinerRecipeCategory.Recipe> recipes = new ArrayList<>();
        for (PortableMiningManager.Entry e : PortableMiningManager.INSTANCE.getEntries()) {
            List<ItemStack> inputs = new ArrayList<>();
            // Resolve tag to block items
            if (e.blockTag != null) {
                BuiltInRegistries.BLOCK.getTag(e.blockTag).ifPresent(set -> {
                    for (Holder<Block> holder : set) {
                        addBlockItem(inputs, holder.value());
                    }
                });
            }
            // Resolve explicit block list to block items
            if (e.blocks != null) {
                for (Block b : e.blocks) addBlockItem(inputs, b);
            }
            if (!inputs.isEmpty() && !e.result.isEmpty()) {
                recipes.add(new PortableMinerRecipeCategory.Recipe(inputs, e.result.copy()));
            }
        }
        registration.addRecipes(PortableMinerRecipeCategory.TYPE, recipes);
    }

    private static void addBlockItem(List<ItemStack> list, Block b) {
        Item it = b.asItem();
        if (it != null && it != net.minecraft.world.item.Items.AIR) {
            ItemStack stack = new ItemStack(it);
            // de-duplicate while preserving order
            for (ItemStack existing : list) {
                if (ItemStack.isSameItemSameComponents(existing, stack)) return;
            }
            list.add(stack);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PORTABLE_MINER.get()), PortableMinerRecipeCategory.TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        // no-op
    }
}
