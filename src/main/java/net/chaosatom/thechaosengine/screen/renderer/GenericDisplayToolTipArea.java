package net.chaosatom.thechaosengine.screen.renderer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.inventory.ContainerData;

import java.util.List;

public class GenericDisplayToolTipArea {
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final ContainerData data;

    public GenericDisplayToolTipArea(int xPos, int yPos, int width, int height, ContainerData data) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public List<Component> getTooltipsHeatBonus() {
        int hex = getDynamicHeatBonusColor(this.data.get(2));
        return List.of(Component.translatable("tooltip.thechaosengine.heat_bonus_level")
                .append(Component.literal(this.data.get(2) + " / " + this.data.get(3)))
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(hex))));
    }

    private int getDynamicHeatBonusColor (int level) {
        // Based off of RGB values from Heat Bonus Meter texture
        return switch (level) {
            case 2 -> 0x841505;
            case 3 -> 0xD2120C;
            case 4 -> 0xEA4E1C;
            case 5 -> 0xEC822B;
            case 6 -> 0xF5BB6B;
            case 7 -> 0XF4F4EF;
            default -> 0x5B0B02; // Made brighter for readability
        };
    }

    public Component getDescriptiveTooltip(String key) {
        return Component.translatable(key);
    }
}
