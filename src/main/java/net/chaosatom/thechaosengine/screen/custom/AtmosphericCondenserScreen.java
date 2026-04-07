package net.chaosatom.thechaosengine.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.screen.renderer.EnergyDisplayTooltipArea;
import net.chaosatom.thechaosengine.screen.renderer.FluidTankRenderer;
import net.chaosatom.thechaosengine.screen.renderer.GenericDisplayToolTipArea;
import net.chaosatom.thechaosengine.util.MouseUtil;
import net.chaosatom.thechaosengine.util.RenderLabelUtil;
import net.minecraft.ChatFormatting;
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
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,"textures/gui/atmospheric_condenser/atmospheric_condenser_gui.png");

    private FluidTankRenderer fluidRenderer;
    private EnergyDisplayTooltipArea energyInfoArea;
    private GenericDisplayToolTipArea metalStampInfoArea;

    public AtmosphericCondenserScreen(AtmosphericCondenserMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 189;
    }
    // Components to Render

    // Coordinates
    private final int fluidTankLocX = 138;
    private final int fluidTankLocY = 28 ;
    private final int energyBarLocX = 155;
    private final int energyBarLocY = 28;

    @Override
    protected void init() {
        super.init();

        assignFluidRenderer();
        assignEnergyInfoArea();
        assignMetalStampInfoArea();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + energyBarLocX,
                ((height - imageHeight) / 2) + energyBarLocY, menu.blockEntity.getEnergyStorage(null));
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, energyBarLocX, energyBarLocY, 8, 64)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(),
                    Optional.empty(), mouseX - x, mouseY - y);
        }
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

    private void assignMetalStampInfoArea() {
        metalStampInfoArea = new GenericDisplayToolTipArea((width - imageWidth) / 2,
                (height - imageHeight) / 2, width, height, menu.getData());
    }

    private void renderMetalStampTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, 107, 33, 14, 14)) {
            guiGraphics.renderTooltip(this.font,
                    metalStampInfoArea.getDescriptiveTooltip("tooltip.thechaosengine.metal_stamp"),
                    mouseX - x,
                    mouseY - y);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        Component title = this.getTitle();
        int titleWidth = this.font.width(title);
        int centerX = (this.imageWidth - titleWidth) / 2;
        int textColor = 3289650;

        Component monitorTitle = Component.translatable("label.thechaosengine.generic.monitor_title");
        Component effectivePercent = Component.translatable("label.thechaosengine.atmospheric_condenser.effective_percent")
                .append(this.menu.getEffectivenessPercentage() + "%");
        Component waterGeneration = Component.translatable("label.thechaosengine.atmospheric_condenser.production_shorthand")
                .append(this.menu.getCurrentWaterGeneration() + " mB/t");
        Component fluidTransferRate = Component.translatable("label.thechaosengine.generic.output")
                .append(this.menu.getCurrentFluidTransferAmount() + " mB");

        RenderLabelUtil.renderScaledComponent(guiGraphics, this.font, title, centerX, this.titleLabelY + 2, 164, textColor);
        RenderLabelUtil.renderScaledComponent(guiGraphics, this.font, this.playerInventoryTitle, 8, this.imageHeight - 94,
                50, textColor);

        RenderLabelUtil.renderScaledComponentLinked(guiGraphics, this.font,  monitorTitle, effectivePercent,
                waterGeneration, fluidTransferRate, 22, 38, 22,47,22,56,22,65,
                59,0x29B46C );

        renderEnergyAreaTooltip(guiGraphics, mouseX, mouseY, x, y);
        renderFluidTooltipArea(guiGraphics, mouseX, mouseY, x, y, menu.blockEntity.getFluid(),
                fluidTankLocX, fluidTankLocY, fluidRenderer);
        renderMetalStampTooltip(guiGraphics, mouseX, mouseY, x, y);
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
        fluidRenderer.render(guiGraphics, x + fluidTankLocX, y + fluidTankLocY, menu.blockEntity.getFluid());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private static boolean isMouseAboveFluidArea(double pMouseX, double pMouseY, int x, int y, int offsetX, int offsetY, FluidTankRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }

    private static boolean isMouseAboveArea(double pMouseX, double pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseAboveArea(mouseX - this.leftPos, mouseY - this.topPos, 107,33,0,0,14,14)) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.displayClientMessage(Component.translatable(
                    "lore.thechaosengine.atmospheric_condenser.metal_stamp").withStyle(ChatFormatting.GRAY), false);
            this.minecraft.player.closeContainer();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
