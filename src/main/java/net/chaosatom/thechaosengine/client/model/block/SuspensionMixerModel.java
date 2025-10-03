package net.chaosatom.thechaosengine.client.model.block;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.custom.SuspensionMixerBlock;
import net.chaosatom.thechaosengine.block.entity.custom.SuspensionMixerBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SuspensionMixerModel extends DefaultedBlockGeoModel<SuspensionMixerBlockEntity> {
    private final ResourceLocation SUSPENSION_MIXER_MODEL = buildFormattedModelPath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "suspension_mixer"));
    private final ResourceLocation SUSPENSION_MIXER_TEXTURE = buildFormattedTexturePath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "suspension_mixer"));
    private final ResourceLocation SUSPENSION_MIXER_ANIMATIONS = buildFormattedAnimationPath(
            ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "suspension_mixer"));

    public SuspensionMixerModel() {
        super(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "suspension_mixer"));
    }

    @Override
    public ResourceLocation getModelResource(SuspensionMixerBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(SuspensionMixerBlock.DEPLOYED)) {
            return super.getModelResource(animatable);
        } else {
            return SUSPENSION_MIXER_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(SuspensionMixerBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(SuspensionMixerBlock.DEPLOYED)) {
            return super.getTextureResource(animatable);
        } else {
            return SUSPENSION_MIXER_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(SuspensionMixerBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(SuspensionMixerBlock.DEPLOYED)) {
            return super.getAnimationResource(animatable);
        } else {
            return SUSPENSION_MIXER_ANIMATIONS;
        }
    }

    @Override
    public @Nullable RenderType getRenderType(SuspensionMixerBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
