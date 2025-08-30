package com.lasted.neotech.compat;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.block.ModBlocks;
import com.lasted.neotech.recipe.PortableMiningManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class NeotechJEI implements IModPlugin {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "jei_plugin");

//    public final List<NeotechRecipeCategory<?>> categories = new ArrayList<>();
//    private IIngredientManager ingredientManager;
//
//    public static IJeiRuntime runtime;
//
//    private void loadCategories() {
//        categories.clear();
//        // load categories here at a later date
//    }
//
//    private <T extends Recipe<? extends RecipeInput>> CategoryBuilder<T> builder(Class<T> recipeClass) {
//        return new CategoryBuilder<>(recipeClass);
//    }
//
//    private class CategoryBuilder<T extends Recipe<?>> extends NeotechRecipeCategory.Builder<T> {
//        public CategoryBuilder(Class<? extends T> recipeClass) {
//            super(recipeClass);
//        }
//    }

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
        // Register whatever we currently have; may be empty on title screen before data packs load.
        registration.addRecipes(PortableMinerRecipeCategory.TYPE, buildRecipes());
    }

    private static List<PortableMinerRecipeCategory.Recipe> buildRecipes() {
        List<PortableMinerRecipeCategory.Recipe> recipes = new ArrayList<>();

        // Prefer dynamic, server-synced registries when available (client world joined),
        // fall back to builtin registry when on title screen.
        net.minecraft.core.Registry<Block> blockRegistry = null;
        try {
            var mc = net.minecraft.client.Minecraft.getInstance();
            var conn = mc.getConnection();
            if (conn != null) {
                blockRegistry = conn.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BLOCK);
            }
        } catch (Throwable ignored) {
        }

        for (PortableMiningManager.Entry e : PortableMiningManager.INSTANCE.getEntries()) {
            if (e.notPortable()) continue; // Do not show in portable miner JEI if marked not portable
            List<ItemStack> inputs = new ArrayList<>();

            // New format: ingredients (items/tags)
            if (e.ingredients() != null && !e.ingredients().isEmpty()) {
                for (net.minecraft.world.item.crafting.Ingredient ing : e.ingredients()) {
                    for (ItemStack stack : ing.getItems()) {
                        // de-duplicate
                        boolean exists = false;
                        for (ItemStack existing : inputs) {
                            if (ItemStack.isSameItemSameComponents(existing, stack)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) inputs.add(stack.copy());
                    }
                }
            }

            // Legacy: Resolve tag to block items using the best available registry access
            if (e.blockTag() != null) {
                if (blockRegistry != null) {
                    blockRegistry.getTag(e.blockTag()).ifPresent(set -> {
                        for (Holder<Block> holder : set) {
                            addBlockItem(inputs, holder.value());
                        }
                    });
                } else {
                    BuiltInRegistries.BLOCK.getTag(e.blockTag()).ifPresent(set -> {
                        for (Holder<Block> holder : set) {
                            addBlockItem(inputs, holder.value());
                        }
                    });
                }
            }
            // Legacy: explicit block list to block items
            if (e.blocks() != null) {
                for (Block b : e.blocks()) addBlockItem(inputs, b);
            }

            // Results: choose the first as primary output for display
            ItemStack out = e.result();
            if (out != null && !out.isEmpty() && !inputs.isEmpty()) {
                recipes.add(new PortableMinerRecipeCategory.Recipe(inputs, out.copy()));
            }
        }
        return recipes;
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
        // Keep initial registration only to avoid duplicate entries; no runtime mutation here.
    }
}
