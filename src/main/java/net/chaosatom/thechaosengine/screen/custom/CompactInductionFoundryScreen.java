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

public class CompactInductionFoundryScreen extends AbstractContainerScreen<CompactInductionFoundryMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
                    "textures/gui/compact_induction_foundry/compact_induction_foundry_gui.png");
    private static final ResourceLocation ARROW_PROGRESS_TEX =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
                    "textures/gui/compact_induction_foundry/foundry_arrow_progress.png");
    private static final ResourceLocation INDUCTION_COIL_TEX =
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
                    "textures/gui/compact_induction_foundry/foundry_coil_progress.png");
    private EnergyDisplayTooltipArea energyInfoArea;
    private GenericDisplayToolTipArea metalPlaqueInfoArea;

    private final int vertEnergyBarLocX = 155;
    private final int vertEnergyBarLocY = 28;

    public CompactInductionFoundryScreen(CompactInductionFoundryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageHeight = 189;
    }

    @Override
    protected void init() {
        super.init();

        assignEnergyInfoArea();
        assignMetalPlaqueInfoArea();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + vertEnergyBarLocX,
                ((height - imageHeight) / 2) + vertEnergyBarLocY, menu.blockEntity.getEnergyStorage(null));
    }

    private void assignMetalPlaqueInfoArea() {
        metalPlaqueInfoArea = new GenericDisplayToolTipArea((width - imageWidth) / 2,
                (height - imageHeight) / 2, width, height, menu.getData());
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, vertEnergyBarLocX, vertEnergyBarLocY, 8, 64)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    private void renderMetalPlaqueTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, 77, 89, 64, 6)) {
            guiGraphics.renderTooltip(this.font,
                    metalPlaqueInfoArea.getDescriptiveTooltip("tooltip.thechaosengine.metal_plaque"),
                    mouseX - x,
                    mouseY - y);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        Component title = this.getTitle();
        int titleWidth = this.font.width(title);
        int centerX = (this.imageWidth - titleWidth) / 2;
        int textColor = 3289650;

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

        energyInfoArea.render(guiGraphics);

        renderFoundryProgressArrow(guiGraphics, x, y);
        renderFoundryCoilProgress(guiGraphics, x, y);
    }

    private void renderFoundryProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(ARROW_PROGRESS_TEX, x + 69, y + 49, 0, 0, menu.getScaledArrowProgress(34),
                    12, 33, 12);
        }
    }

    private void renderFoundryCoilProgress(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(INDUCTION_COIL_TEX, x + 45, y + 39, 0, 0, menu.getScaledCoilProgress(23),
                    32, 23, 32);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private static boolean isMouseAboveArea(double pMouseX, double pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseAboveArea(mouseX - this.leftPos, mouseY - this.topPos, 77,89,0,0,64,6)) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.displayClientMessage(Component.translatable(
                    "lore.thechaosengine.compact_induction_foundry.metal_plaque").withStyle(ChatFormatting.GRAY), false);
            this.minecraft.player.closeContainer();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isWithinPlaqueBoundary(double mouseX, double mouseY) {
        int plaqueX = 77;
        int plaqueY = 89;
        int relX = (int)mouseX - this.leftPos;
        int relY = (int)mouseY - this.topPos;

        return relX >= plaqueX && relX <= plaqueX + 64 && relY >= plaqueY && relY <= plaqueY + 6;
    }
}
