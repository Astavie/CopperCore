package astavie.coppercore.conductor;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.math.Direction;

public class ConductorDirection {

    public final @NotNull Direction direction;
    public final byte mask;

    public ConductorDirection(@NotNull Direction direction, byte mask) {
        this.direction = direction;
        this.mask = mask;
    }

}
