package net.chaosatom.thechaosengine.client.model.block;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.custom.AtmosphericCondenserBlock;
import net.chaosatom.thechaosengine.block.entity.custom.AtmosphericCondenserBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class AtmosphericCondenserModel extends DefaultedBlockGeoModel<AtmosphericCondenserBlockEntity> {
    private final ResourceLocation ATMOSPHERIC_CONDENSER_MODEL = buildFormattedModelPath(ResourceLocation
            .fromNamespaceAndPath(TheChaosEngine.MOD_ID, "atmospheric_condenser"));
    private final ResourceLocation ATMOSPHERIC_CONDENSER_TEXTURE = buildFormattedTexturePath(ResourceLocation
            .fromNamespaceAndPath(TheChaosEngine.MOD_ID, "atmospheric_condenser"));
    private final ResourceLocation ATMOSPHERIC_CONDENSER_ANIMATIONS = buildFormattedAnimationPath(ResourceLocation
            .fromNamespaceAndPath(TheChaosEngine.MOD_ID, "atmospheric_condenser"));

    public AtmosphericCondenserModel() {
        super(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, "atmospheric_condenser"));
    }

    @Override
    public ResourceLocation getAnimationResource(AtmosphericCondenserBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(AtmosphericCondenserBlock.DEPLOYED)) {
            return super.getAnimationResource(animatable);
        } else {
            return ATMOSPHERIC_CONDENSER_ANIMATIONS;
        }
    }

    @Override
    public ResourceLocation getModelResource(AtmosphericCondenserBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(AtmosphericCondenserBlock.DEPLOYED)) {
            return super.getModelResource(animatable);
        } else {
            return ATMOSPHERIC_CONDENSER_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(AtmosphericCondenserBlockEntity animatable) {
        BlockState blockState = animatable.getBlockState();
        if (blockState.getValue(AtmosphericCondenserBlock.DEPLOYED)) {
            return super.getTextureResource(animatable);
        } else {
            return ATMOSPHERIC_CONDENSER_TEXTURE;
        }
    }

    @Override
    public @Nullable RenderType getRenderType(AtmosphericCondenserBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
