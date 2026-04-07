package net.chaosatom.thechaosengine.client.renderer.block;

import net.chaosatom.thechaosengine.block.custom.PyrolysisCrucibleBlock;
import net.chaosatom.thechaosengine.block.entity.custom.PyrolysisCrucibleBlockEntity;
import net.chaosatom.thechaosengine.client.model.block.PyrolysisCrucibleModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class PyrolysisCrucibleBlockEntityRenderer extends GeoBlockRenderer<PyrolysisCrucibleBlockEntity> {
    public PyrolysisCrucibleBlockEntityRenderer(BlockEntityRendererProvider.Context renderManager) {
        super(new PyrolysisCrucibleModel());

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(PyrolysisCrucibleBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(PyrolysisCrucibleBlock.DEPLOYED)) {
            return new AABB(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5,
                    pos.getX() + 1.5, pos.getY() + 1.125, pos.getZ() + 1.5);
        } else {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 0.6875, pos.getZ() + 1);
        }
    }
}
