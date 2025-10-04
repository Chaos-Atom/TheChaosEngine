package net.chaosatom.thechaosengine.client.renderer.block;

import net.chaosatom.thechaosengine.block.custom.AtmosphericCondenserBlock;
import net.chaosatom.thechaosengine.block.entity.custom.AtmosphericCondenserBlockEntity;
import net.chaosatom.thechaosengine.client.model.block.AtmosphericCondenserModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AtmosphericCondenserBlockEntityRenderer extends GeoBlockRenderer<AtmosphericCondenserBlockEntity> {
    public AtmosphericCondenserBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new AtmosphericCondenserModel());

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public AABB getRenderBoundingBox(AtmosphericCondenserBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(AtmosphericCondenserBlock.DEPLOYED)) {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
        } else {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 0.6875, pos.getZ() + 1);
        }
    }
}
