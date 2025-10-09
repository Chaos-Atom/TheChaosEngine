package net.chaosatom.thechaosengine.client.model.block;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.custom.DeployableSolarBlock;
import net.chaosatom.thechaosengine.block.entity.custom.DeployableSolarBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class DeployableSolarModel extends DefaultedBlockGeoModel<DeployableSolarBlockEntity> {
    private final ResourceLocation DEPLOYABLE_SOLAR_MODEL = buildFormattedModelPath(ResourceLocation
            .fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_solar"));
    private final ResourceLocation DEPLOYABLE_SOLAR_TEXTURE = buildFormattedTexturePath(ResourceLocation
            .fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_solar"));
    private final ResourceLocation DEPLOYABLE_SOLAR_ANIMATION = buildFormattedAnimationPath(ResourceLocation
            .fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_solar"));

    public DeployableSolarModel() {
        super(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "deployable_solar"));
    }

    @Override
    public ResourceLocation getModelResource(DeployableSolarBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(DeployableSolarBlock.DEPLOYED)) {
            return super.getModelResource(animatable);
        } else {
            return DEPLOYABLE_SOLAR_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(DeployableSolarBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(DeployableSolarBlock.DEPLOYED)) {
            return super.getTextureResource(animatable);
        } else {
            return DEPLOYABLE_SOLAR_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(DeployableSolarBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(DeployableSolarBlock.DEPLOYED)) {
            return super.getAnimationResource(animatable);
        } else {
            return DEPLOYABLE_SOLAR_ANIMATION;
        }
    }

    @Override
    public @Nullable RenderType getRenderType(DeployableSolarBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
