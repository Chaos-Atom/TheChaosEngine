package net.chaosatom.thechaosengine.screen.renderer;

import net.minecraft.network.chat.Component;

public class StickyNoteTooltipArea {
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;

    public StickyNoteTooltipArea(int xMin, int yMin, int width, int height) {
        this.xPos = xMin;
        this.yPos = yMin;
        this.width = width;
        this.height = height;
    }

    public Component getStickyNoteTooltips() {
        return Component.translatable("tooltip.thechaosengine.compact_coal_generator_stickynote");
    }
}
