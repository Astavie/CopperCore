package astavie.coppercore.block;

import astavie.coppercore.CopperCore;
import astavie.coppercore.block.entity.CapacitorBlockEntity;
import astavie.coppercore.conductor.ICapacitor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WaxedCapacitorBlock extends Block implements BlockEntityProvider {

    public WaxedCapacitorBlock(OxidationLevel level) {
        super(FabricBlockSettings.of(Material.METAL, CapacitorBlock.COLOR[level.ordinal()])
                .requiresTool().strength(3.0F, 6.0F)
                .sounds(BlockSoundGroup.COPPER).luminance(state -> state.get(CapacitorBlock.ON) ? 10 : 0));
        setDefaultState(stateManager.getDefaultState().with(CapacitorBlock.ON, false));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        // Do not replace the tile entity if this is the same block
        if (!(newState.getBlock() instanceof CapacitorBlock || newState.getBlock() instanceof WaxedCapacitorBlock)) {
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return state.get(CapacitorBlock.ON);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        ICapacitor capacitor = getCapacitor(world, pos, state);
        return MathHelper.floor((float) capacitor.getEnergy() / capacitor.getMaxEnergy() * 14.0F) + (capacitor.getEnergy() > 0 ? 1 : 0);
    }

    private ICapacitor getCapacitor(World world, BlockPos pos, BlockState state) {
        return (ICapacitor) world.getBlockEntity(pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CapacitorBlock.ON);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CapacitorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient || type != CopperCore.CAPACITOR_BLOCK_ENTITY ? null : (a, b, c, d) -> CapacitorBlockEntity.serverTick(a, b, c, (CapacitorBlockEntity) d);
    }

}
