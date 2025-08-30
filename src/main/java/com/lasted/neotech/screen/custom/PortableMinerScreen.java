package com.lasted.neotech.screen.custom;

import com.lasted.neotech.NeoTech;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PortableMinerScreen extends AbstractContainerScreen<PortableMinerMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(NeoTech.MODID, "textures/gui/portable_miner/portable_miner_gui.png");

    public PortableMinerScreen(PortableMinerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Draw vertical progress bar to the right of the output slot (80,35)
        float fraction = this.menu.getProgressFraction();
        int barX = x + 80 + 18 + 4; // 4px gap to the right of the slot
        int barY = y + 35;
        int barW = 4;
        int barH = 16; // same height as slot
        // Background of bar
        guiGraphics.fill(barX, barY, barX + barW, barY + barH, 0xFF333333);
        // Filled portion grows upwards
        int filled = Math.max(0, Math.min(barH, (int) Math.floor(fraction * barH)));
        if (filled > 0) {
            guiGraphics.fill(barX, barY + (barH - filled), barX + barW, barY + barH, 0xFF4CAF50);
        }
    }
}
