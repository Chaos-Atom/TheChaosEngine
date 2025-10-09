package net.chaosatom.thechaosengine.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.screen.renderer.EnergyDisplayTooltipArea;
import net.chaosatom.thechaosengine.util.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class DeployableSolarScreen extends AbstractContainerScreen<DeployableSolarMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/deployable_solar/deployable_solar_gui.png");
    private static final ResourceLocation POWER_GENERATION_METER =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/deployable_solar/power_gen_meter.png");

    // Inspired by the way icons appear within Mekanism's solar generators!
    private static final ResourceLocation FULL_SUN_ICON =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/deployable_solar/full_sun.png");
    private static final ResourceLocation IS_NIGHT_ICON =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/deployable_solar/is_night.png");
    private static final ResourceLocation IS_RAINING_ICON =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/deployable_solar/is_raining.png");
    private static final ResourceLocation OBSTRUCTED_SUN_ICON =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "textures/gui/deployable_solar/obstructed_sun.png");

    private EnergyDisplayTooltipArea energyInfoArea;

    public DeployableSolarScreen(DeployableSolarMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    private final int vertEnergyBarLocX = 159;
    private final int vertEnergyBarLocY = 9;

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 8;

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

        renderPowerGenerationMeter(guiGraphics, x, y);
        renderOperationStatusIcon(guiGraphics, x, y);
    }

    private void renderPowerGenerationMeter(GuiGraphics guiGraphics, int x, int y) {
        int scaledHeight = menu.getScaledPowerGeneration(41);
        if (scaledHeight > 0) {
            guiGraphics.blit(POWER_GENERATION_METER, x + 9, y + 23 + 41 - scaledHeight,
                    0, 41 - scaledHeight,
                    19, scaledHeight,
                    19, 41);
        }
    }

    private void renderOperationStatusIcon(GuiGraphics guiGraphics, int x, int y) {
        int operationStatus = menu.sendOperationStatus();
        int iconX = x + 80;
        int iconY = y + 32;
        switch (operationStatus) {
            case 0:
                guiGraphics.blit(FULL_SUN_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16);
                break;
            case 1:
                guiGraphics.blit(IS_NIGHT_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16);
                break;
            case 2:
                guiGraphics.blit(IS_RAINING_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16);
                break;
            case 3:
                guiGraphics.blit(OBSTRUCTED_SUN_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16);
                break;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public static boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
