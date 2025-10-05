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

public class SuspensionMixerScreen extends AbstractContainerScreen<SuspensionMixerMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/suspension_mixer/suspension_mixer_gui.png");
    private static final ResourceLocation MAIN_PROGRESS_ARROW =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/suspension_mixer/long_intersect_arrow.png");
    private static final ResourceLocation MIXER_PROGRESS =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/suspension_mixer/mixer_progress.png");

    private FluidTankRenderer fluidRenderer;
    private EnergyDisplayTooltipArea energyInfoArea;

    public SuspensionMixerScreen(SuspensionMixerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        // GUI is larger than average, adjusted to accommodate change
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    // Meter Locations (for ease of testing)
    private static final int vertEnergyBarLocX = 156;
    private static final int vertEnergyBarLocY = 26;
    private static final int vertFluidInputBarLocX = 30;
    private static final int vertFluidInputBarLocY = 26;
    private static final int vertFluidOutputBarLocX = 138;
    private static final int vertFluidOutputBarLocY = 26;

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 46;
        this.titleLabelY = 8;
        this.inventoryLabelY = (this.imageHeight / 2) - 1;

        assignEnergyInfoArea();
        assignFluidRenderer();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + vertEnergyBarLocX,
                ((height - imageHeight) / 2) + vertEnergyBarLocY, menu.blockEntity.getEnergyStorage(null), 8, 56);
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, vertEnergyBarLocX, vertEnergyBarLocY, 8, 56)) {
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

    private void renderHorizontalProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isMixing()) {
            guiGraphics.blit(MAIN_PROGRESS_ARROW, x + 40, y + 46, 0, 0, menu.getScaledArrowProgress(92),
                    18, 92, 18);
        }
    }

    private void renderMixerIconProgress(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isMixing()) {
            guiGraphics.blit(MIXER_PROGRESS, x + 79, y + 28, 0, 0, 18,
                    menu.getScaledMixerProgress(50), 18, 50);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltip(guiGraphics, mouseX, mouseY, x, y);
        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.getInputFluid(),
                vertFluidInputBarLocX, vertFluidInputBarLocY, fluidRenderer);
        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.getOutputFluid(),
                vertFluidOutputBarLocX, vertFluidOutputBarLocY, fluidRenderer);
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
        fluidRenderer.render(guiGraphics, x + vertFluidInputBarLocX, y + vertFluidInputBarLocY, menu.getInputFluid());
        fluidRenderer.render(guiGraphics, x + vertFluidOutputBarLocX, y + vertFluidOutputBarLocY, menu.getOutputFluid());

        renderHorizontalProgressArrow(guiGraphics, x, y);
        renderMixerIconProgress(guiGraphics, x, y);
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
