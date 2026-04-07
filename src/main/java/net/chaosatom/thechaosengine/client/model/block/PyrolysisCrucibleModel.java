package net.chaosatom.thechaosengine.client.model.block;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.custom.PyrolysisCrucibleBlock;
import net.chaosatom.thechaosengine.block.entity.custom.PyrolysisCrucibleBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PyrolysisCrucibleModel extends DefaultedBlockGeoModel<PyrolysisCrucibleBlockEntity> {
    private final ResourceLocation PYROLYSIS_CRUCIBLE_MODEL = buildFormattedModelPath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "pyrolysis_crucible"));
    private final ResourceLocation PYROLYSIS_CRUCIBLE_TEXTURE = buildFormattedTexturePath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "pyrolysis_crucible"));
    private final ResourceLocation PYROLYSIS_CRUCIBLE_ANIM = buildFormattedAnimationPath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "pyrolysis_crucible"));

    public PyrolysisCrucibleModel() {
        super(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "pyrolysis_crucible"));
    }

    @Override
    public ResourceLocation getModelResource(PyrolysisCrucibleBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(PyrolysisCrucibleBlock.DEPLOYED)) {
            return super.getModelResource(animatable);
        } else {
            return PYROLYSIS_CRUCIBLE_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(PyrolysisCrucibleBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(PyrolysisCrucibleBlock.DEPLOYED)) {
            return super.getTextureResource(animatable);
        } else {
            return PYROLYSIS_CRUCIBLE_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(PyrolysisCrucibleBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(PyrolysisCrucibleBlock.DEPLOYED)) {
            return super.getAnimationResource(animatable);
        } else {
            return PYROLYSIS_CRUCIBLE_ANIM;
        }
    }

    @Override
    public @Nullable RenderType getRenderType(PyrolysisCrucibleBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
