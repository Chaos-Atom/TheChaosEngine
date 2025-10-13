package net.chaosatom.thechaosengine.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.screen.renderer.EnergyDisplayTooltipArea;
import net.chaosatom.thechaosengine.screen.renderer.FluidTankRenderer;
import net.chaosatom.thechaosengine.util.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class CompactRefineryScreen extends AbstractContainerScreen<CompactRefineryMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/compact_refinery/compact_refinery_gui.png");
    private static final ResourceLocation PROGRESS_METER =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/compact_refinery/refinery_combined_progress.png");

    private FluidTankRenderer fluidRenderer;
    private EnergyDisplayTooltipArea energyInfoArea;

    public CompactRefineryScreen(CompactRefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 45;
        this.titleLabelY = 8;

        assignEnergyInfoArea();
        assignFluidRenderer();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + 159,
                ((height - imageHeight) / 2) + 9, menu.blockEntity.getEnergyStorage(null), 8, 64);
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, 159, 9, 8, 64)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(),
                    Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    private void assignFluidRenderer() {
        fluidRenderer = new FluidTankRenderer(8000, true, 8, 56);
    }

    private void renderFluidTooltipArea(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y,
                                        FluidStack fluidStack, int offsetX, int offsetY, FluidTankRenderer renderer) {
        if (isMouseAboveFluidArea(mouseX, mouseY, x, y, offsetX, offsetY, renderer)) {
            guiGraphics.renderTooltip(this.font, renderer.getTooltip(fluidStack, TooltipFlag.Default.NORMAL),
                    Optional.empty(), mouseX - x, mouseY - y );
        }
    }

    private void renderRefineryProgress(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isRefining()) {
            guiGraphics.blit(PROGRESS_METER, x + 21, y + 35, 0, 0, menu.getScaledProgress(78),
                    21, 78, 21);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltip(guiGraphics, mouseX, mouseY, x, y);
        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.getFluid(),
                14, 9, fluidRenderer);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        energyInfoArea.render(guiGraphics);
        fluidRenderer.render(guiGraphics, x + 9, y + 9, menu.getFluid());

        renderRefineryProgress(guiGraphics, x, y);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public static boolean isMouseAboveFluidArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidTankRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    public static boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
