package astavie.coppercore.conductor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class ConductorProvider implements BlockApiProvider<IConductor, @NotNull ConductorDirection> {

    // Basic shapes
    public static final byte EMPTY = 0;
    public static final byte FULL = 1 | 2 | 4 | 8;

    public static final byte HALF_BOTTOM = 1 | 2;
    public static final byte HALF_TOP = 4 | 8;

    public static final byte STAIRS_BOTTOM = 1 | 2 | 4;
    public static final byte STAIRS_TOP = 2 | 4 | 8;

    // X axis
    public static final byte NORTH_BOTTOM = 1;
    public static final byte SOUTH_BOTTOM = 2;
    public static final byte SOUTH_TOP = 4;
    public static final byte NORTH_TOP = 8;

    // Y axis
    public static final byte SOUTH_EAST = 1;
    public static final byte SOUTH_WEST = 2;
    public static final byte NORTH_WEST = 4;
    public static final byte NORTH_EAST = 8;

    // Z axis
    public static final byte EAST_BOTTOM = 1;
    public static final byte WEST_BOTTOM = 2;
    public static final byte WEST_TOP = 4;
    public static final byte EAST_TOP = 8;

    // I hate stairs
    public static final byte[] STAIRS_SHAPE_VERT = new byte[] { SOUTH_EAST | SOUTH_WEST,
            SOUTH_EAST | SOUTH_WEST | NORTH_EAST, SOUTH_EAST | SOUTH_WEST | NORTH_WEST, SOUTH_EAST, SOUTH_WEST };

    public static final byte[] STAIRS_SHAPE_LEFT_SIDE_BOTTOM = new byte[] { STAIRS_BOTTOM, FULL, STAIRS_BOTTOM,
            STAIRS_BOTTOM, HALF_BOTTOM };
    public static final byte[] STAIRS_SHAPE_RIGHT_SIDE_BOTTOM = new byte[] { STAIRS_BOTTOM, FULL, FULL, HALF_BOTTOM,
            STAIRS_BOTTOM };

    public static final byte[] STAIRS_SHAPE_LEFT_SIDE_TOP = new byte[] { STAIRS_TOP, FULL, STAIRS_TOP, STAIRS_TOP,
            HALF_TOP };
    public static final byte[] STAIRS_SHAPE_RIGHT_SIDE_TOP = new byte[] { STAIRS_TOP, FULL, FULL, HALF_TOP,
            STAIRS_TOP };

    public static final byte[] STAIRS_SHAPE_BACK_BOTTOM = new byte[] { HALF_BOTTOM, flipMask(STAIRS_BOTTOM, true),
            STAIRS_BOTTOM, HALF_BOTTOM, HALF_BOTTOM };
    public static final byte[] STAIRS_SHAPE_BACK_TOP = new byte[] { HALF_TOP, flipMask(STAIRS_TOP, true), STAIRS_TOP,
            HALF_TOP, HALF_TOP };

    public static byte rotateMask(byte mask, int amount) {
        if (amount == 0)
            return mask;
        if (amount < 0 || amount > 3)
            throw new IllegalArgumentException();
        return (byte) ((mask << amount) | (mask >> (4 - amount)) & 0b1111);
    }

    public static byte flipMask(byte mask, boolean flip) {
        if (!flip)
            return mask;
        return (byte) (((mask << 1) & 0b1010) | ((mask >> 1) & 0b0101));
    }

    public static class Full extends ConductorProvider {

        @Override
        public @Nullable IConductor find(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity,
                @NotNull ConductorDirection context) {
            return context.mask != EMPTY ? dir -> FULL : null;
        }

    }

    public static class Slab extends ConductorProvider {

        public static boolean isOpposite(SlabType type, Direction direction) {
            return (type == SlabType.BOTTOM && direction == Direction.UP)
                    || (type == SlabType.TOP && direction == Direction.DOWN);
        }

        @Override
        public @Nullable IConductor find(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity,
                @NotNull ConductorDirection context) {
            if (context.mask == EMPTY) {
                return null;
            }

            SlabType type = state.get(Properties.SLAB_TYPE);
            if (isOpposite(type, context.direction)) {
                return null;
            }

            if (type == SlabType.DOUBLE) {
                return dir -> FULL;
            }

            byte mask = type == SlabType.TOP ? HALF_TOP : HALF_BOTTOM;
            if (context.direction.getAxis() == Direction.Axis.Y || (context.mask & mask) > 0) {
                return dir -> dir.getAxis() == Direction.Axis.Y ? isOpposite(type, dir) ? EMPTY : FULL : mask;
            }

            return null;
        }

    }

    public static class Stairs extends ConductorProvider {

        @Override
        public @Nullable IConductor find(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity,
                @NotNull ConductorDirection context) {
            if (context.mask == EMPTY) {
                return null;
            }

            byte[] masks = new byte[6];

            Direction facing = state.get(Properties.HORIZONTAL_FACING);
            int rotation = facing.getHorizontal();
            boolean upsideDown = state.get(Properties.BLOCK_HALF) == BlockHalf.TOP;
            int shape = state.get(Properties.STAIR_SHAPE).ordinal();

            // Top and bottom
            byte vertical = rotateMask(STAIRS_SHAPE_VERT[shape], rotation);
            masks[Direction.UP.ordinal()] = upsideDown ? FULL : vertical;
            masks[Direction.DOWN.ordinal()] = upsideDown ? vertical : FULL;

            // Sides
            boolean flip = rotation >= 2;
            masks[facing.ordinal()] = FULL;
            masks[facing.rotateYCounterclockwise().ordinal()] = flipMask(
                    (upsideDown ? STAIRS_SHAPE_LEFT_SIDE_TOP : STAIRS_SHAPE_LEFT_SIDE_BOTTOM)[shape], flip);
            masks[facing.rotateYClockwise().ordinal()] = flipMask(
                    (upsideDown ? STAIRS_SHAPE_RIGHT_SIDE_TOP : STAIRS_SHAPE_RIGHT_SIDE_BOTTOM)[shape], flip);
            masks[facing.getOpposite().ordinal()] = flipMask(
                    (upsideDown ? STAIRS_SHAPE_BACK_TOP : STAIRS_SHAPE_BACK_BOTTOM)[shape], flip);

            if ((context.mask & masks[context.direction.ordinal()]) == 0) {
                return null;
            }

            return dir -> masks[dir.ordinal()];
        }

    }

    public static class Rod extends ConductorProvider {

        @Override
        public @Nullable IConductor find(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity,
                @NotNull ConductorDirection context) {
            Direction.Axis axis = state.get(Properties.FACING).getAxis();
            return context.mask != EMPTY && axis == context.direction.getAxis()
                    ? dir -> axis == dir.getAxis() ? FULL : EMPTY
                    : null;
        }

    }

}
