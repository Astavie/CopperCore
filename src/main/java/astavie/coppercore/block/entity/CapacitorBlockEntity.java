package astavie.coppercore.block.entity;

import java.util.Set;

import astavie.coppercore.CopperCore;
import astavie.coppercore.conductor.ConductorProvider;
import astavie.coppercore.conductor.ICapacitor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CapacitorBlockEntity extends BlockEntity implements ICapacitor {

    private int energy = 0;

    private long lastCache = 0;
    private Set<ICapacitor> network;

    public CapacitorBlockEntity(BlockPos pos, BlockState state) {
        super(CopperCore.CAPACITOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("energy", energy);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        energy = nbt.getInt("energy");
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, CapacitorBlockEntity blockEntity) {
        // Only do this once a second
        if (world.getTime() % 20 != 0)
            return;

        // Get network
        if (world.getTime() != blockEntity.lastCache) {
            ICapacitor.cacheNetwork(world, pos, dir -> ConductorProvider.FULL, blockEntity);
        }

        // Print
        System.out.println(pos + " is connected to " + (blockEntity.network.size() - 1) + " other capacitors.");
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public int getMaxEnergy() {
        return 1000;
    }

    @Override
    public int giveEnergy(int energy) {
        this.energy += energy;
        if (energy > getMaxEnergy()) {
            int leftover = this.energy - getMaxEnergy();
            this.energy = getMaxEnergy();
            return leftover;
        }
        return 0;
    }

    @Override
    public int takeEnergy(int energy) {
        this.energy -= energy;
        if (this.energy < 0) {
            energy += this.energy;
            this.energy = 0;
        }
        return energy;
    }

    @Override
    public void cacheNetworkForTick(long tick, Set<ICapacitor> capacitors) {
        lastCache = tick;
        network = capacitors;
    }

}
