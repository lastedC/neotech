package com.lasted.neotech.compat.jei.category;

import com.lasted.neotech.NeoTech;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NeotechRecipeCategory<T extends Recipe<?>> implements IRecipeCategory<T> {
    private static final ResourceLocation WIDGETS_TEXTURE = ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "textures/gui/jei/widgets.png");
    private final Object slotDrawable;

    protected final RecipeType<T> recipeType;
    protected final Component title;
    protected final IDrawable background;
    protected final IDrawable icon;

    private final Supplier<List<T>> recipes;
    private final List<Supplier<? extends ItemStack>> recipeSuppliers;

    public NeotechRecipeCategory(RecipeType<T> recipeType, Component title, IDrawable background, IDrawable icon, Supplier<List<T>> recipes, List<Supplier<? extends ItemStack>> recipeSuppliers, IGuiHelper guiHelper) {
        this.recipeType = recipeType;
        this.title = title;
        this.background = background;
        this.icon = icon;
        this.recipes = recipes;
        this.recipeSuppliers = recipeSuppliers;

        this.slotDrawable = guiHelper.createDrawable(WIDGETS_TEXTURE, 0, 0, 18, 18);
    }


    @Override
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {

    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }
}
