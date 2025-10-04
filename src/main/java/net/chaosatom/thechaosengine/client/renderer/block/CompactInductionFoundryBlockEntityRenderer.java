package net.chaosatom.thechaosengine.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.chaosatom.thechaosengine.block.entity.custom.CompactInductionFoundryBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

// I really wrote myself into a corner with these machine prefixes...
public class CompactInductionFoundryBlockEntityRenderer implements BlockEntityRenderer<CompactInductionFoundryBlockEntity> {
    public CompactInductionFoundryBlockEntityRenderer (BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CompactInductionFoundryBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = blockEntity.getItem(0);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);
        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
