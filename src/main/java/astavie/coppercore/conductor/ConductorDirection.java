package astavie.coppercore.conductor;

import net.minecraft.util.math.Direction;

public class ConductorDirection {

    public final Direction direction;
    public final int mask;

    public ConductorDirection(Direction direction, int mask) {
        this.direction = direction;
        this.mask = mask;
    }

}
