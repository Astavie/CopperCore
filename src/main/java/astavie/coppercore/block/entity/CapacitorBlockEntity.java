package astavie.coppercore.block.entity;

import java.util.Set;

import astavie.coppercore.CopperCore;
import astavie.coppercore.block.CapacitorBlock;
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
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("energy", energy);
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

        // TODO: Test
        System.out.println(pos + " is in a network with " + blockEntity.network.size() + " capacitors.");
        blockEntity.giveEnergy(10);
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
        int leftover = 0;
        int oldEnergy = this.energy;

        // Set energy
        this.energy += energy;
        if (this.energy > getMaxEnergy()) {
            leftover = this.energy - getMaxEnergy();
            this.energy = getMaxEnergy();
        }

        // Set on
        if (oldEnergy == 0 && energy > 0) {
            world.setBlockState(pos, getCachedState().with(CapacitorBlock.ON, true));
        }
        world.updateComparators(pos, getCachedState().getBlock());

        return leftover;
    }

    @Override
    public int takeEnergy(int energy) {
        int oldEnergy = this.energy;

        // Set energy
        this.energy -= energy;
        if (this.energy < 0) {
            energy += this.energy;
            this.energy = 0;
        }

        // Set off
        if (oldEnergy > 0 && this.energy == 0) {
            world.setBlockState(pos, getCachedState().with(CapacitorBlock.ON, false));
        }
        world.updateComparators(pos, getCachedState().getBlock());

        return energy;
    }

    @Override
    public void cacheNetworkForTick(long tick, Set<ICapacitor> capacitors) {
        lastCache = tick;
        network = capacitors;
    }

}
