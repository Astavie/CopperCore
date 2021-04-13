package astavie.coppercore.block;

import java.util.HashSet;
import java.util.Set;

import astavie.coppercore.CopperCore;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface ICapacitor {

    int getEnergy();

    int getMaxEnergy();

    int giveEnergy(int energy);

    int takeEnergy(int energy);

    void cacheNetworkForTick(long tick, Set<ICapacitor> capacitors);

    static void cacheNetwork(World world, BlockPos pos, IConductor conductor, ICapacitor capacitor) {
        // Get other capacitors
        LongSet seen = new LongOpenHashSet();
        Set<ICapacitor> network = new HashSet<>();
        network.add(capacitor);
        explore(seen, world, pos, conductor, network);

        // Cache network for tick
        for (ICapacitor c : network) {
            c.cacheNetworkForTick(world.getTime(), network);
        }
    }

    static void explore(LongSet seen, World world, BlockPos pos, IConductor current, Set<ICapacitor> set) {
        // We saw this conductor
        seen.add(pos.asLong());

        // Check all directions
        for (Direction dir : Direction.values()) {
            BlockPos next = pos.offset(dir);

            if (!current.conductsTo(dir) || seen.contains(next.asLong()))
                continue;

            IConductor conductor = CopperCore.CONDUCTOR.find(world, next, dir.getOpposite());
            ICapacitor capacitor = CopperCore.CAPACITOR.find(world, next, dir.getOpposite());

            if (capacitor != null) {
                set.add(capacitor);
            }

            if (conductor != null) {
                explore(seen, world, next, conductor, set);
            }
        }
    }

}
