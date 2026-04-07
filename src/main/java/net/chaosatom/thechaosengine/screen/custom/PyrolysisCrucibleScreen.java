package net.chaosatom.thechaosengine.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.screen.renderer.EnergyDisplayTooltipArea;
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

import java.util.Optional;

public class PyrolysisCrucibleScreen extends AbstractContainerScreen<PyrolysisCrucibleMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/pyrolysis_crucible/pyrolysis_crucible_gui.png");
    private static final ResourceLocation MAIN_PROGRESS_ARROW =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/pyrolysis_crucible/pyrolysis_crucible_progress.png");
    private static final ResourceLocation HEAT_BONUS_METER =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/pyrolysis_crucible/heat_bonus_meter.png");

    // GUI Element Coordinates
    private static final int energyBarX = 155;
    private static final int energyBarY = 28;
    private static final int heatBonusX = 13;
    private static final int heatBonusY = 28;
    private static final int plaqueX = 68;
    private static final int plaqueY = 28;

    // Tooltip Display
    private EnergyDisplayTooltipArea energyInfoArea;
    private GenericDisplayToolTipArea heatBonusInfoArea;
    private GenericDisplayToolTipArea metalPlaqueInfoArea;

    public PyrolysisCrucibleScreen(PyrolysisCrucibleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 204;
    }

    @Override
    protected void init() {
        super.init();

        assignEnergyInfoArea();
        assignHeatBonusInfoArea();
        assignMetalPlaqueInfoArea();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + energyBarX,
                ((height - imageHeight) / 2) + energyBarY, menu.blockEntity.getEnergyStorage(null), 8, 63);
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, energyBarX, energyBarY, 8, 63)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(),
                    Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        int scaledWidth = menu.getScaledArrowProgress(46);
        if (menu.isCoking()) {
            guiGraphics.blit(MAIN_PROGRESS_ARROW, x + 63, y + 58, 0, 0, scaledWidth,
                    10, 45, 10);
        }
    }

    private void renderHeatBonusMeter(GuiGraphics guiGraphics, int x, int y) {
        int scaledHeight = menu.getScaledHeatBonus(70);
        if (scaledHeight > 0) {
            guiGraphics.blit(HEAT_BONUS_METER, x + heatBonusX, y + heatBonusY + 70 - scaledHeight,
                    0, 70 - scaledHeight, 3, scaledHeight, 3, 70);
        }
    }

    private void assignHeatBonusInfoArea() {
        heatBonusInfoArea = new GenericDisplayToolTipArea(((width - imageWidth) / 2) + heatBonusX,
                ((height - imageHeight) / 2) + heatBonusY, 3, 70, menu.getData());
    }

    private void renderHeatBonusAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, heatBonusX, heatBonusY, 3, 70)) {
            guiGraphics.renderTooltip(this.font, heatBonusInfoArea.getTooltipsHeatBonus(),
                    Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    private void renderMetalPlaqueTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, plaqueX, plaqueY, 34, 16)) {
            guiGraphics.renderTooltip(this.font,
                    metalPlaqueInfoArea.getDescriptiveTooltip("tooltip.thechaosengine.metal_plaque"),
                    mouseX - x,
                    mouseY - y);
        }
    }

    private void assignMetalPlaqueInfoArea() {
        metalPlaqueInfoArea = new GenericDisplayToolTipArea((width - imageWidth) / 2,
                (height - imageHeight) / 2, width, height, menu.getData());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        Component title = this.getTitle();
        int textWidth = this.font.width(title);
        int centerX = (this.imageWidth - textWidth) / 2;
        int textColor = 3289650; // 50-50-50 RGB

        RenderLabelUtil.renderScaledComponent(guiGraphics, this.font, title, centerX, this.titleLabelY + 2, 164, textColor);
        RenderLabelUtil.renderScaledComponent(guiGraphics, this.font, this.playerInventoryTitle, 8, this.imageHeight - 94,
                50, textColor);

        renderEnergyAreaTooltip(guiGraphics, mouseX, mouseY, x, y);
        renderHeatBonusAreaTooltip(guiGraphics, mouseX, mouseY, x ,y);
        renderMetalPlaqueTooltip(guiGraphics, mouseX, mouseY, x, y);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        energyInfoArea.render(guiGraphics);

        renderProgressArrow(guiGraphics, x, y);
        renderHeatBonusMeter(guiGraphics, x, y);
    }

    private static boolean isMouseAboveArea(double mouseX, double mouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseAboveArea(mouseX - this.leftPos, mouseY - this.topPos, plaqueX, plaqueY,
                0,0, 34,16)) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.displayClientMessage(Component.translatable(
                    "lore.thechaosengine.pyrolysis_crucible.metal_plaque").withStyle(ChatFormatting.GRAY), false);
            this.minecraft.player.closeContainer();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}