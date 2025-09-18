package net.chaosatom.thechaosengine.block.custom;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.block.entity.ModBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.CompactPulverizerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import org.jetbrains.annotations.Nullable;

public class CompactPulverizerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<CompactPulverizerBlock> CODEC = simpleCodec(CompactPulverizerBlock::new);

    public CompactPulverizerBlock(Properties properties) {
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
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CompactPulverizerBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CompactPulverizerBlockEntity compactPulverizerBlockEntity) {
                compactPulverizerBlockEntity.drops();
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CompactPulverizerBlockEntity compactPulverizerBlockEntity) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(compactPulverizerBlockEntity, Component.translatable("block.thechaosengine.compact_pulverizer")), pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.COMPACT_PULVERIZER_BE.get(),
                (level1, blockPos, blockState, compactPulverizerBlockEntity)
                        -> compactPulverizerBlockEntity.tick(level1, blockPos, blockState));
    }

    /* SOUNDS & PARTICLES */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double xPos = (double)pos.getX() + (double)0.5F;
        double yPos = (double)pos.getY() + (double)1.05F;
        double zPos = (double)pos.getZ() + (double)0.5F;
        /* TODO: Add custom sounds
        if (random.nextDouble() < 0.1) {
            // Plays sounds at a specific spot
            level.playLocalSound(xPos, yPos, zPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS,
                    0.85F, 1F,
                    true);
        }
         */

        double spread = 0.92; // If > 1, higher spread, < 1 will give tighter spread
        double jump = 0.12; // Higher the value, the further the particles will spawn from origin
        double xOffset = random.nextDouble() * spread - 0.45;
        double yOffset =  Math.abs(random.nextDouble() * jump);
        double zOffset = random.nextDouble() * spread - 0.42;
        if (level.getBlockEntity(pos) instanceof CompactPulverizerBlockEntity compactPulverizerBlockEntity
                && !compactPulverizerBlockEntity.itemHandler.getStackInSlot(0).isEmpty()) {
            level.addParticle(ParticleTypes.CRIT,
                    xPos + xOffset, yPos + yOffset, zPos + zOffset,
                    (double)0.0F, (double)0.0F, (double)0.0F);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, compactPulverizerBlockEntity.itemHandler.getStackInSlot(0)),
                    xPos + xOffset, yPos + yOffset, zPos + zOffset,
                    (double)0.0F, (double)0.01F, (double)0.0F);
        }
    }
}
