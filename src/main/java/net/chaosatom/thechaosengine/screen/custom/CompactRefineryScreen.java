package net.chaosatom.thechaosengine.screen.custom;

import net.chaosatom.thechaosengine.screen.renderer.EnergyDisplayTooltipArea;
import net.chaosatom.thechaosengine.screen.renderer.FluidTankRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CompactRefineryScreen extends AbstractContainerScreen<CompactRefineryMenu> {
    private FluidTankRenderer fluidTankRenderer;
    private EnergyDisplayTooltipArea energyInfoArea;

    public CompactRefineryScreen(CompactRefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 8;
    }
}
