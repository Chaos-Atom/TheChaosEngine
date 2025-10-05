package net.chaosatom.thechaosengine.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.chaosatom.thechaosengine.block.custom.SuspensionMixerBlock;
import net.chaosatom.thechaosengine.block.entity.custom.SuspensionMixerBlockEntity;
import net.chaosatom.thechaosengine.client.model.block.SuspensionMixerModel;
import net.chaosatom.thechaosengine.client.renderer.block.layer.SuspensionMixerItemRenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SuspensionMixerBlockEntityRenderer extends GeoBlockRenderer<SuspensionMixerBlockEntity> {
    public SuspensionMixerBlockEntityRenderer(BlockEntityRendererProvider.Context renderManager) {
        super(new SuspensionMixerModel());

        addRenderLayer(new SuspensionMixerItemRenderLayer(this));
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    // Credits to TurtyWurty for render(), drawVertex, and drawQuad methods
    // Under MIT-License: https://github.com/DaRealTurtyWurty/1.20-Tutorial-Mod?tab=MIT-1-ov-file#readme
    // Slightly Modified by ChaosAtom for use in GeckoLib Animated Model
    @Override
    public void render(SuspensionMixerBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        SuspensionMixerBlockEntity.AnimationState currentState = animatable.getAnimState();
        if (currentState == SuspensionMixerBlockEntity.AnimationState.DEPLOYED || currentState == SuspensionMixerBlockEntity.AnimationState.WORKING) {
            FluidStack inputFluidStack = animatable.getInputFluid();
            if (inputFluidStack.isEmpty())
                return;

            Level level = animatable.getLevel();
            if (level == null)
                return;

            BlockPos pos = animatable.getBlockPos();

            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(inputFluidStack.getFluid());
            ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(inputFluidStack);

            FluidState state = inputFluidStack.getFluid().defaultFluidState();

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
            int tintColor = fluidTypeExtensions.getTintColor(state, level, pos);

            float height = (((float) animatable.getInputTank(null).getFluidInTank(0).getAmount() / animatable.getInputTank(null).getTankCapacity(0)) * 0.625f) + 0.25f;

            VertexConsumer builder = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(state));

            // Top Texture
            poseStack.pushPose();
            drawQuad(builder, poseStack, 0.1f, height, 0.1f, 0.9f, height, 0.9f, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLight, tintColor);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.translate(0f, -0.9f, 0f);
            poseStack.popPose();
        }
    }

    private static void drawVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, float u, float v, int packedLight, int color) {
        builder.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setLight(packedLight)
                .setNormal(1, 0, 0);
    }

    private static void drawQuad(VertexConsumer builder, PoseStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int packedLight, int color) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, packedLight, color);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, packedLight, color);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, packedLight, color);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(SuspensionMixerBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(SuspensionMixerBlock.DEPLOYED)) {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 1.5625, pos.getZ() + 1);
        } else {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 0.9375, pos.getZ() + 1);
        }
    }
}
