package net.chaosatom.thechaosengine.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RenderLabelUtil {
    /**
     * Renders a scaled component to fix a specific width for all languages
     * Can be to desired location in GUI
     * @param maxWidth The pixel width of the area in where the component is located
     * @param textColor The decimal equivalent to RGB
     */
    public static void renderScaledComponent(GuiGraphics guiGraphics, Font font, Component component,
                                             int x, int y, int maxWidth, int textColor) {
        int textWidth = font.width(component);
        float scale = 1.0f; // Default scale

        if (textWidth > (maxWidth)) {
            scale = Math.min(1.0f, (float) maxWidth / (float) textWidth);
            y++; // When scaled, titles are typically higher than desired, moves it down to accommodate
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component, 0, 0, textColor, false);
        guiGraphics.pose().popPose();
    }

    /* Used for Multi-line cases (i.e. Screen inside GUI) */
    public static void renderScaledComponentLinked(GuiGraphics guiGraphics, Font font, Component component1, Component component2,
                                                   int x1, int y1, int x2, int y2, int maxWidth, int textColor) {
        int maxTextWidth = Math.max(font.width(component1), font.width(component2));
        float scale = 1.0f; // Default scale

        if (maxTextWidth > (maxWidth)) {
            scale = Math.min(1.0f, (float) maxWidth / (float) maxTextWidth);
            y1++; // When scaled, titles are typically higher than desired, moves it down to accommodate
            y2++;
        }

        // Both lines share the scaling factor of the longer text
        // First Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x1, y1, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component1, 0, 0, textColor, false);
        guiGraphics.pose().popPose();

        // Second Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x2, y2, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component2, 0, 0, textColor, false);
        guiGraphics.pose().popPose();
    }

    public static void renderScaledComponentLinked(GuiGraphics guiGraphics, Font font, Component component1, Component component2,
                                                   Component component3, int x1, int y1, int x2, int y2, int x3, int y3,
                                                   int maxWidth, int textColor) {
        int maxTextWidth = Math.max(Math.max(font.width(component1), font.width(component2)), font.width(component3));
        float scale = 1.0f; // Default scale

        if (maxTextWidth > (maxWidth)) {
            scale = Math.min(1.0f, (float) maxWidth / (float) maxTextWidth);
            y1++; // When scaled, titles are typically higher than desired, moves it down to accommodate
            y2++;
            y3++;
        }

        // First Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x1, y1, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component1, 0, 0, textColor, false);
        guiGraphics.pose().popPose();

        // Second Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x2, y2, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component2, 0, 0, textColor, false);
        guiGraphics.pose().popPose();

        // Third Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x3, y3, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component3, 0, 0, textColor, false);
        guiGraphics.pose().popPose();
    }

    // Honestly have no clue if this is a good way of doing it. Hopefully it works fine...
    public static void renderScaledComponentLinked(GuiGraphics guiGraphics, Font font, Component component1, Component component2,
                                                   Component component3, Component component4, int x1, int y1, int x2, int y2,
                                                   int x3, int y3, int x4, int y4, int maxWidth, int textColor) {
        int maxTextWidth = Math.max(Math.max(Math.max(font.width(component1), font.width(component2)),
                font.width(component3)), font.width(component4));
        float scale = 1.0f; // Default scale

        if (maxTextWidth > (maxWidth)) {
            scale = Math.min(1.0f, (float) maxWidth / (float) maxTextWidth);
            y1++; // When scaled, titles are typically higher than desired, moves it down to accommodate
            y2++;
            y3++;
            y4++;
        }

        // First Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x1, y1, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component1, 0, 0, textColor, false);
        guiGraphics.pose().popPose();

        // Second Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x2, y2, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component2, 0, 0, textColor, false);
        guiGraphics.pose().popPose();

        // Third Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x3, y3, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component3, 0, 0, textColor, false);
        guiGraphics.pose().popPose();

        // Fourth Line
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x4, y4, 0);
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component4, 0, 0, textColor, false);
        guiGraphics.pose().popPose();

    }
}
