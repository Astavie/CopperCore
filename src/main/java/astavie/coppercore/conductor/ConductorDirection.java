package astavie.coppercore.conductor;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.math.Direction;

public class ConductorDirection {

    public final @NotNull Direction direction;
    public final int mask;

    public ConductorDirection(@NotNull Direction direction, int mask) {
        this.direction = direction;
        this.mask = mask;
    }

}
