package net.chaosatom.thechaosengine.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CETransparentBlock extends HalfTransparentBlock {
    public static final MapCodec<CETransparentBlock> CODEC = simpleCodec(CETransparentBlock::new);

    public CETransparentBlock(Properties properties) {
        super(properties);
    }

    protected MapCodec<CETransparentBlock> codec() {
        return CODEC;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.85f;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
