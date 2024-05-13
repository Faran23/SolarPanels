package io.github.faran23.solarpanels.solar;

import io.github.faran23.solarpanels.Config;
import io.github.faran23.solarpanels.GroupColor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SolarPanelBlock extends Block implements EntityBlock {

    private static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public static final EnumProperty<GroupColor> COLOR = EnumProperty.create("color", GroupColor.class);

    public SolarPanelBlock() {
        super(Properties.of().instabreak().pushReaction(PushReaction.DESTROY).sound(SoundType.METAL).explosionResistance(3600000.0F).lightLevel(
                (state) -> state.getValue(BlockStateProperties.POWERED) ? 15 : 0
        ).noOcclusion());
    }

    @NotNull
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult blockHitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        // colour
        if (stack.getItem() instanceof DyeItem dyeItem) {
            BlockState newState = state.setValue(COLOR, GroupColor.fromString(dyeItem.getDyeColor().getName()));
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            level.setBlockAndUpdate(pos, newState);
            return InteractionResult.SUCCESS;
        }

        // upgrade
        Config.Tier upgradeTier = Config.getTier(stack.getItem());
        if (upgradeTier != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SolarPanelBlockEntity solar) {
                solar.upgrade(upgradeTier, (ServerLevel) level);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(state, level, pos, player, hand, blockHitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolarPanelBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof SolarPanelBlockEntity solar) {
                solar.tickServer();
            }
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false)
                .setValue(COLOR, GroupColor.BLUE)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED, COLOR, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return AABB;
    }

}
