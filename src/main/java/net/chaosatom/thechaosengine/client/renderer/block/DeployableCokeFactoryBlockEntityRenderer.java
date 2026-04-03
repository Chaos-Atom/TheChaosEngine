package net.chaosatom.thechaosengine.client.renderer.block;

import net.chaosatom.thechaosengine.block.custom.DeployableCokeFactoryBlock;
import net.chaosatom.thechaosengine.block.entity.custom.DeployableCokeFactoryBlockEntity;
import net.chaosatom.thechaosengine.client.model.block.DeployableCokeFactoryModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DeployableCokeFactoryBlockEntityRenderer extends GeoBlockRenderer<DeployableCokeFactoryBlockEntity> {
    public DeployableCokeFactoryBlockEntityRenderer(BlockEntityRendererProvider.Context renderManager) {
        super(new DeployableCokeFactoryModel());

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(DeployableCokeFactoryBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(DeployableCokeFactoryBlock.DEPLOYED)) {
            return new AABB(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5,
                    pos.getX() + 1.5, pos.getY() + 1.125, pos.getZ() + 1.5);
        } else {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 0.6875, pos.getZ() + 1);
        }
    }
}
