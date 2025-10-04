package net.chaosatom.thechaosengine.client.renderer.block.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.chaosatom.thechaosengine.block.entity.custom.SuspensionMixerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class SuspensionMixerItemRenderLayer extends BlockAndItemGeoLayer<SuspensionMixerBlockEntity> {
    public SuspensionMixerItemRenderLayer(GeoRenderer<SuspensionMixerBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    protected @Nullable ItemStack getStackForBone(GeoBone bone, SuspensionMixerBlockEntity animatable) {
        if (bone.getName().equals("Item Bone")) {
            return animatable.itemHandler.getStackInSlot(0);
        }
        return null;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, SuspensionMixerBlockEntity animatable,
                                      MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        if (bone.getName().equals("Item Bone")) {
            poseStack.pushPose();
            poseStack.translate(0f,0.035f,-0.55f);
            poseStack.scale(0.5f, 0.5f, 0.5f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                    poseStack, bufferSource, null, 0
            );
            poseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
