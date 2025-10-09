package net.chaosatom.thechaosengine.client.renderer.block;

import net.chaosatom.thechaosengine.block.custom.DeployableSolarBlock;
import net.chaosatom.thechaosengine.block.entity.custom.DeployableSolarBlockEntity;
import net.chaosatom.thechaosengine.client.model.block.DeployableSolarModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DeployableSolarBlockEntityRenderer extends GeoBlockRenderer<DeployableSolarBlockEntity> {
    public DeployableSolarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new DeployableSolarModel());

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public AABB getRenderBoundingBox(DeployableSolarBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(DeployableSolarBlock.DEPLOYED)) {
            return new AABB(pos.getX() -0.5, pos.getY(), pos.getZ() - 0.5,
                    pos.getX() + 1.5, pos.getY() + 2, pos.getZ() + 1.5);
        } else {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 0.6875, pos.getZ() + 1);
        }
    }
}
