package com.lasted.neotech.compat;

import com.lasted.neotech.NeoTech;
import com.lasted.neotech.block.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;

@ParametersAreNonnullByDefault
public class PortableMinerRecipeCategory implements IRecipeCategory<PortableMinerRecipeCategory.Recipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "portable_mining");
    public static final RecipeType<Recipe> TYPE = RecipeType.create(NeoTech.MODID, "portable_mining", Recipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable shadowDrawable;
    private final IDrawable arrowDrawable;

    // Cache a randomly selected input per recipe for the current JEI session
    private static final Map<Recipe, ItemStack> chosenInput = new WeakHashMap<>();

    // Widgets texture (shadow + arrow) provided by the mod at assets/neotech/textures/gui/jei/widgets.png
    private static final ResourceLocation WIDGETS_TEX = ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "textures/gui/jei/widgets.png");
    // NOTE: If the cutout positions in your widgets.png change, adjust these UV/size constants.
    private static final int SHADOW_U = 3, SHADOW_V = 19, SHADOW_W = 32, SHADOW_H = 16;

    private static final int ARROW_U = 19, ARROW_V = 13, ARROW_W = 41, ARROW_H = 9;
    private static final int SLOT_U = 0, SLOT_V = 0, SLOT_W = 18, SLOT_H = 18;

    private static final int ING_SLOT_X = 20, ING_SLOT_Y = 12;
    private static final int RES_SLOT_X = 90, RES_SLOT_Y = 12;

    public PortableMinerRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(120, 40);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.PORTABLE_MINER.get()));
        this.slotDrawable = guiHelper.createDrawable(WIDGETS_TEX, SLOT_U, SLOT_V, SLOT_W, SLOT_H);
        this.shadowDrawable = guiHelper.createDrawable(WIDGETS_TEX, SHADOW_U, SHADOW_V, SHADOW_W, SHADOW_H);
        this.arrowDrawable = guiHelper.createDrawable(WIDGETS_TEX, 19, 7, ARROW_W, ARROW_H);
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        // Use the provided lang key in assets to avoid missing translation
        return Component.translatable("jei.neotech.portable_mining");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        // Provide all possible inputs so JEI cycles through them and hover shows the full set
        if (recipe.inputs() != null && !recipe.inputs().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, ING_SLOT_X + 1, ING_SLOT_Y + 1)
                    .addIngredients(VanillaTypes.ITEM_STACK, recipe.inputs());
        }
        if (recipe.output() != null && !recipe.output().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, RES_SLOT_X + 1, RES_SLOT_Y + 1)
                    .addItemStack(recipe.output());
        }
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, net.minecraft.client.gui.GuiGraphics guiGraphics, double mouseX, double mouseY) {
        slotDrawable.draw(guiGraphics, ING_SLOT_X, ING_SLOT_Y);
        slotDrawable.draw(guiGraphics, RES_SLOT_X, RES_SLOT_Y);

        int arrowX = (background.getWidth() - 32) / 2;
        int arrowY = (background.getHeight()) / 2;
        arrowDrawable.draw(guiGraphics, arrowX, arrowY);

        // Pick a random input for this recipe (once per JEI session) and render it in 3D with the miner above it
        if (!recipe.inputs().isEmpty()) {
            ItemStack toShow = chosenInput.computeIfAbsent(recipe, r -> {
                List<ItemStack> list = r.inputs();
                int idx = ThreadLocalRandom.current().nextInt(list.size());
                return list.get(idx);
            });

            Item item = toShow.getItem();
            if (item != Items.AIR) {
                Block block = Block.byItem(item);
                if (block != null) {
                    BlockState baseState = block.defaultBlockState();
                    BlockState minerState = ModBlocks.PORTABLE_MINER.get().defaultBlockState();

                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    PoseStack pose = guiGraphics.pose();
                    var buffer = guiGraphics.bufferSource();

                    pose.pushPose();
                    // Draw a soft shadow under the input block using widgets.png
                    // Place the shadow so it visually sits beneath the rendered base block
//                    int shadowX = 20; // tune if needed to align with the 3D render
//                    int shadowY = 22;
//                    shadowDrawable.draw(guiGraphics, shadowX, shadowY);

                    // Position the scene around x ~ 28px, y ~ 34px within the 120x40 area
                    pose.translate(28.0f, 34.0f, 100.0f);
                    // Scale GUI pixels to block units; negative Y flips to screen space
                    float scale = 16.0f; // 16px per block unit
                    pose.scale(scale, -scale, scale);
                    // Nice isometric-ish angle
                    pose.mulPose(Axis.XP.rotationDegrees(25.0f));
                    pose.mulPose(Axis.YP.rotationDegrees(225.0f));

                    int light = LightTexture.FULL_BRIGHT;
                    int overlay = OverlayTexture.NO_OVERLAY;

                    try {
                        // Render base/input block at y=0
//                        dispatcher.renderSingleBlock(baseState, pose, buffer, light, overlay);
                        // Render the portable miner above it at y=1
                        pose.translate(0.0f, 1.0f, 0.0f);
//                        dispatcher.renderSingleBlock(minerState, pose, buffer, light, overlay);
                    } catch (Throwable ignored) {
                        // Avoid crashing JEI if something goes wrong with rendering
                    }

                    pose.popPose();
                    // Flush buffered render calls
                    guiGraphics.flush();
                }
            }
        }
    }

    public static void clearCache() {
        chosenInput.clear();
    }


    public record Recipe(List<ItemStack> inputs, ItemStack output) {
    }
}
