package net.chaosatom.thechaosengine.block.custom;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.block.entity.ModBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.CompactCoalGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompactCoalGeneratorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<CompactCoalGeneratorBlock> CODEC = simpleCodec(CompactCoalGeneratorBlock::new);
    
    public CompactCoalGeneratorBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /* FACING */

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    /* BLOCK ENTITY */

    @Override
    protected RenderShape getRenderShape(BlockState state) {return RenderShape.MODEL;}

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CompactCoalGeneratorBlockEntity compactCoalGeneratorBlockEntity) {
                compactCoalGeneratorBlockEntity.drops();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CompactCoalGeneratorBlockEntity coalGeneratorBlockEntity) {
                player.openMenu(new SimpleMenuProvider(coalGeneratorBlockEntity, Component.literal("Compact Coal Generator")), pos);
            } else {
                throw new IllegalStateException("Our Container Provider is missing!");
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CompactCoalGeneratorBlockEntity(blockPos,blockState);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlockEntities.COMPACT_COAL_GENERATOR_BE.get(),
                ((level, blockPos, blockState, coalGeneratorBlockEntity) -> coalGeneratorBlockEntity.tick(level, blockPos, blockState)));
    }

    /* SOUNDS & PARTICLES */
    // TODO: Add custom sounds
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double d0 = (double)pos.getX() + (double)0.75F;
        double d1 = (double)pos.getY() + (double)1.15F;
        double d2 = (double)pos.getZ() + (double)0.875F;
        if (random.nextDouble() < 0.1) {
            // Plays sounds at a specific spot
            level.playLocalSound(d0, d1, d2, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,
                    0.85F,
                    0.75F,
                    false);
        }

        // TODO: Fix particle directionality
        Direction direction = state.getValue(FACING);
        Direction.Axis directionAxis = direction.getAxis();
        double d3 = directionAxis == Direction.Axis.X ? (double)direction.getStepX() * 0.5: 0;
        double d4 = directionAxis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.5: 0;

        if (level.getBlockEntity(pos) instanceof CompactCoalGeneratorBlockEntity compactCoalGeneratorBlockEntity
                && !compactCoalGeneratorBlockEntity.itemHandler.getStackInSlot(0).isEmpty()) {
            // Creates smoke particles at a specific spot on the block.
            level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, (double)0.0F, (double)0.0F, (double)0.0F);
            level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, (double)0.0F, (double)0.01F, (double)0.0F);
        }
    }
}
