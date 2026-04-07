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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CompactCoalGeneratorScreen extends AbstractContainerScreen<CompactCoalGeneratorMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,"textures/gui/compact_coal_generator/compact_coal_generator_gui.png");
    private static final ResourceLocation COMBUSTION_PROGRESS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,"textures/gui/compact_coal_generator/combustion_progress.png");
    private EnergyDisplayTooltipArea energyInfoArea;
    private GenericDisplayToolTipArea metalPlaqueInfoArea;

    public CompactCoalGeneratorScreen(CompactCoalGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.imageHeight = 186;
    }

    // To clarify what the numbers in renderEnergyAreaTooltip() and assignEnergyInfoArea()
    private final int energyBarX = 155;
    private final int energyBarY = 25;

    @Override
    protected void init() {
        super.init();

        assignEnergyInfoArea();
        assignMetalPlaqueInfoArea();
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, energyBarX, energyBarY, 8, 64)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    private void renderMetalPlaqueTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, 11, 66, 39, 16)) {
            guiGraphics.renderTooltip(this.font,
                    metalPlaqueInfoArea.getDescriptiveTooltip("tooltip.thechaosengine.metal_plaque"),
                    mouseX - x,
                    mouseY - y);
        }
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + energyBarX,
                ((height - imageHeight) / 2) + energyBarY, menu.blockEntity.getEnergyStorage(null));
    }

    private void assignMetalPlaqueInfoArea() {
        metalPlaqueInfoArea = new GenericDisplayToolTipArea((width - imageWidth) / 2,
                (height - imageHeight) / 2, width, height, menu.getData());
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        Component title = this.getTitle();
        int titleWidth = this.font.width(title);
        int centerX = (this.imageWidth - titleWidth) / 2;
        int textColor = 3289650; // 50-50-50 RGB

        RenderLabelUtil.renderScaledComponent(guiGraphics, this.font, title, centerX, this.titleLabelY + 2, 164, textColor);
        RenderLabelUtil.renderScaledComponent(guiGraphics, this.font, this.playerInventoryTitle, 8, this.imageHeight - 94,
                50, textColor);

        renderEnergyAreaTooltip(guiGraphics, pMouseX, pMouseY, x, y);
        renderMetalPlaqueTooltip(guiGraphics, pMouseX, pMouseY, x, y);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderFuelBurning(guiGraphics, x, y);
        energyInfoArea.render(guiGraphics);
    }

    private void renderFuelBurning(GuiGraphics guiGraphics, int x, int y) {
        if(this.menu.isBurning()) {
            int l = Mth.ceil(this.menu.getFuelProgress() * 14);
            if (l > 0) {
                // Mostly to understand better what these numbers do.
                int progressTexLocationX = 81;
                int progressTexLocationY = 47;
                guiGraphics.blit(COMBUSTION_PROGRESS_TEXTURE,
                        x + progressTexLocationX,
                        y + progressTexLocationY + 14 - l,
                        0,
                        14 - l,
                        14,
                        l,
                        14,
                        14);
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private static boolean isMouseAboveArea(double pMouseX, double pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseAboveArea(mouseX - this.leftPos, mouseY - this.topPos, 11,66,0,0,39,16)) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.displayClientMessage(Component.translatable(
                    "lore.thechaosengine.compact_coal_generator.metal_plaque").withStyle(ChatFormatting.GRAY), false);
            this.minecraft.player.closeContainer();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}