package me.aleksilassila.litematica.printer.v1_21.guides.placement;

import me.aleksilassila.litematica.printer.v1_21.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21.actions.*;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21.guides.Guide;
import me.aleksilassila.litematica.printer.v1_21.implementation.PrinterPlacementContext;
import me.aleksilassila.litematica.printer.v1_21.implementation.actions.AirPlaceAction;
import me.aleksilassila.litematica.printer.v1_21.implementation.actions.InteractActionImpl;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Guide that clicks its neighbors to create a placement in target position.
 */
abstract public class PlacementGuide extends Guide {
    MinecraftClient mc = MinecraftClient.getInstance();

    public PlacementGuide(SchematicBlockState state) {
        super(state);
    }

    protected ItemStack getBlockItem(BlockState state) {
        return state.getBlock().getPickStack(this.state.world, this.state.blockPos, state);
    }

    protected Optional<Block> getRequiredItemAsBlock(ClientPlayerEntity player) {
        ItemStack requiredItem = getRequiredItem(player).stream().findFirst().orElse(ItemStack.EMPTY);

        if (requiredItem.isEmpty()) {
            return Optional.empty();
        } else {
            if (requiredItem.getItem() instanceof BlockItem)
                return Optional.of(((BlockItem) requiredItem.getItem()).getBlock());
            else return Optional.empty();
        }
    }

    @Override
    protected @NotNull List<ItemStack> getRequiredItems() {
        return Collections.singletonList(getBlockItem(state.targetState));
    }

    abstract protected boolean getUseShift(SchematicBlockState state);

    @Nullable
    abstract public PrinterPlacementContext getPlacementContext(ClientPlayerEntity player);

    @Override
    public boolean canExecute(ClientPlayerEntity player) {
        if (!super.canExecute(player)) return false;

        List<ItemStack> requiredItems = getRequiredItems();
        if (requiredItems.isEmpty() || requiredItems.stream().allMatch(i -> i.isOf(Items.AIR)))
            return false;

        if (!currentState.isReplaceable()) return false;

        ItemPlacementContext ctx = getPlacementContext(player);
        if (ctx == null || !ctx.canPlace()) return false;
//        if (!state.currentState.getMaterial().isReplaceable()) return false;
        if (!LitematicaMixinMod.REPLACE_FLUIDS_SOURCE_BLOCKS.getBooleanValue()
                && getProperty(state.currentState, FluidBlock.LEVEL).orElse(1) == 0)
            return false;

        BlockState resultState = getRequiredItemAsBlock(player)
                .orElse(targetState.getBlock())
                .getPlacementState(ctx);

        if (resultState != null) {
            if (collidesWithPlayer(resultState)) {
                if (PrinterConfig.isDebug()) {
                    System.out.println("Block collides with player. Not placing.");
                }
                return false;
            }
            if (!resultState.canPlaceAt(state.world, state.blockPos)) return false;
            return !(currentState.getBlock() instanceof FluidBlock) || canPlaceInWater(resultState);
        } else {
            return false;
        }

    }

    public boolean isInAir(BlockPos pos) {
        if (mc.world == null) return false;
        for (Direction dir : Direction.values()) {
            if (!mc.world.getBlockState(pos.offset(dir)).isAir()) {
                return false;
            }
        }
        return true;
    }

    private boolean collidesWithPlayer(BlockState blockState) {
        if (mc.player == null || mc.world == null) return true;

        VoxelShape shape = blockState.getCollisionShape(state.schematic, state.blockPos);
        if (shape.isEmpty()) return false;
        shape = shape.offset(state.blockPos.getX(), state.blockPos.getY(), state.blockPos.getZ());
        Box playerShape = mc.player.getBoundingBox();
        return VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(playerShape), shape, BooleanBiFunction.AND);
    }

    @Override
    public @NotNull List<Action> execute(ClientPlayerEntity player) {
        List<Action> actions = new ArrayList<>();
        PrinterPlacementContext ctx = getPlacementContext(player);

        if (ctx == null) return actions;
        ActionChain actionChain = new ActionChain();

        if (ctx.isAirPlace) {
            actionChain.addImmediateAction(new PrepareAction(ctx));
            actionChain.addImmediateAction(new AirPlaceAction(ctx));
            actions.add(actionChain);
            return actions;
        } else {
            if (PrinterConfig.PRINTER_AIRPLACE.getBooleanValue() && PrinterConfig.PRINTER_AIRPLACE_ONLY.getBooleanValue()) {
                return actions;
            }
        }

        actionChain.addImmediateAction(new PrepareLook(ctx));
        actionChain.addImmediateAction(new PrepareAction(ctx));
        if (ctx.shouldSneak) actionChain.addImmediateAction(new PresShift());
        actionChain.addNextTickAction(new InteractActionImpl(ctx));
        if (ctx.shouldSneak) actionChain.addNextTickAction(new ReleaseShiftAction());
        actions.add(actionChain);

        return actions;
    }

    protected static boolean canBeClicked(World world, BlockPos pos) {
        return getOutlineShape(world, pos) != VoxelShapes.empty() && !(world.getBlockState(pos).getBlock() instanceof AbstractSignBlock); // FIXME signs
    }

    private static VoxelShape getOutlineShape(World world, BlockPos pos) {
        return world.getBlockState(pos).getOutlineShape(world, pos);
    }

    public boolean isInteractive(Block block) {
        for (Class<?> clazz : interactiveBlocks) {
            if (clazz.isInstance(block)) {
                return true;
            }
        }

        return false;
    }

    private boolean canPlaceInWater(BlockState blockState) {
        Block block = blockState.getBlock();
        if (block instanceof FluidFillable) {
            return true;
        } else if (!(block instanceof DoorBlock) && !(blockState.getBlock() instanceof AbstractSignBlock) && !blockState.isOf(Blocks.LADDER) && !blockState.isOf(Blocks.SUGAR_CANE) && !blockState.isOf(Blocks.BUBBLE_COLUMN)) {
//            Material material = blockState.getMaterial();
//            if (material != Material.PORTAL && material != Material.STRUCTURE_VOID && material != Material.UNDERWATER_PLANT && material != Material.REPLACEABLE_UNDERWATER_PLANT) {
//                return material.blocksMovement();
//            } else {
//                return true;
//            }
            return blockState.blocksMovement();
        }

        return true;
    }
}
