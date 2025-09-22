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

public class AtmosphericCondenserScreen extends AbstractContainerScreen<AtmosphericCondenserMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,"textures/gui/atmospheric_condenser_gui.png");
    private static final ResourceLocation EFFECTIVENESS_METER =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/effectiveness_meter.png");

    private FluidTankRenderer fluidRenderer;
    private EnergyDisplayTooltipArea energyInfoArea;

    public AtmosphericCondenserScreen(AtmosphericCondenserMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    private final int vertFluidTankLocX = 140;
    private final int vertFluidTankLocY = 9;

    private final int vertEnergyBarLocX = 159;
    private final int vertEnergyBarLocY = 9;

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 8;

        assignFluidRenderer();
        assignEnergyInfoArea();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + vertEnergyBarLocX,
                ((height - imageHeight) / 2) + vertEnergyBarLocY, menu.blockEntity.getEnergyStorage(null));
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, vertEnergyBarLocX, vertEnergyBarLocY, 8, 64)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(),
                    Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltip(guiGraphics, mouseX, mouseY, x, y);
        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.blockEntity.getFluid(),
                vertFluidTankLocX, vertFluidTankLocY, fluidRenderer);
    }

    private void assignFluidRenderer() {
        fluidRenderer = new FluidTankRenderer(16000, true, 8, 64);
    }

    private void renderFluidTooltipArea(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y,
                                        FluidStack fluidStack,int offsetX, int offsetY, FluidTankRenderer renderer) {
        if (isMouseAboveFluidArea(mouseX, mouseY, x, y, offsetX, offsetY, renderer)) {
            guiGraphics.renderTooltip(this.font, renderer.getTooltip(fluidStack, TooltipFlag.Default.NORMAL),
                    Optional.empty(), mouseX - x, mouseY - y );
        }
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
        fluidRenderer.render(guiGraphics, x + vertFluidTankLocX, y + vertFluidTankLocY, menu.blockEntity.getFluid());

        renderEffectivenessMeter(guiGraphics, x, y);
    }

    private void renderEffectivenessMeter(GuiGraphics guiGraphics, int x, int y) {
        int scaledHeight = menu.getScaledEffectiveness(40);
        if (scaledHeight > 0) {
            guiGraphics.blit(EFFECTIVENESS_METER, x + 9, y + 24 + 40 - scaledHeight,
                    0, 40 - scaledHeight,
                    2, scaledHeight, 2, 40 );
        }
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
