package net.chaosatom.thechaosengine.client.model.block;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.custom.DeployableCokeFactoryBlock;
import net.chaosatom.thechaosengine.block.entity.custom.DeployableCokeFactoryBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class DeployableCokeFactoryModel extends DefaultedBlockGeoModel<DeployableCokeFactoryBlockEntity> {
    private final ResourceLocation DEPLOYABLE_COKE_FACTORY_MODEL = buildFormattedModelPath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_coke_factory"));
    private final ResourceLocation DEPLOYABLE_COKE_FACTORY_TEXTURE = buildFormattedTexturePath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_coke_factory"));
    private final ResourceLocation DEPLOYABLE_COKE_FACTORY_ANIM = buildFormattedAnimationPath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_coke_factory"));

    public DeployableCokeFactoryModel() {
        super(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_coke_factory"));
    }

    @Override
    public ResourceLocation getModelResource(DeployableCokeFactoryBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(DeployableCokeFactoryBlock.DEPLOYED)) {
            return super.getModelResource(animatable);
        } else {
            return DEPLOYABLE_COKE_FACTORY_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(DeployableCokeFactoryBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(DeployableCokeFactoryBlock.DEPLOYED)) {
            return super.getTextureResource(animatable);
        } else {
            return DEPLOYABLE_COKE_FACTORY_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(DeployableCokeFactoryBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(DeployableCokeFactoryBlock.DEPLOYED)) {
            return super.getAnimationResource(animatable);
        } else {
            return DEPLOYABLE_COKE_FACTORY_ANIM;
        }
    }

    @Override
    public @Nullable RenderType getRenderType(DeployableCokeFactoryBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
