package net.chaosatom.thechaosengine.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CompactPulverizerScreen extends AbstractContainerScreen<CompactPulverizerMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/compact_pulverizer/compact_pulverizer_gui.png");
    private static final ResourceLocation PULVERIZE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/compact_pulverizer/pulverizer_progress.png");

    public CompactPulverizerScreen(CompactPulverizerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 40;
        this.titleLabelY = 8;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderPulverizeProgressArrow(guiGraphics, x, y);
    }

    private void renderPulverizeProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(PULVERIZE_TEXTURE, x + 72, y + 39, 0, 0, menu.getScaledArrowProgress(),
                    12, 28, 12);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
