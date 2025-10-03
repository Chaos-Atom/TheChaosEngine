package net.chaosatom.thechaosengine.client.renderer;

import net.chaosatom.thechaosengine.block.custom.SuspensionMixerBlock;
import net.chaosatom.thechaosengine.block.entity.custom.SuspensionMixerBlockEntity;
import net.chaosatom.thechaosengine.client.model.block.SuspensionMixerModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SuspensionMixerBlockEntityRenderer extends GeoBlockRenderer<SuspensionMixerBlockEntity> {
    public SuspensionMixerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new SuspensionMixerModel());

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public AABB getRenderBoundingBox(SuspensionMixerBlockEntity blockEntity) {
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
