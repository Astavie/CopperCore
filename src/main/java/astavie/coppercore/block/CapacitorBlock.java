package astavie.coppercore.block;

import astavie.coppercore.CopperCore;
import astavie.coppercore.block.entity.CapacitorBlockEntity;
import astavie.coppercore.conductor.ICapacitor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.OxidizableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CapacitorBlock extends OxidizableBlock implements BlockEntityProvider {

    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final MapColor[] COLOR = new MapColor[] { MapColor.ORANGE, MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.DARK_AQUA, MapColor.TEAL };

    public CapacitorBlock(OxidationLevel level) {
        super(level, FabricBlockSettings.of(Material.METAL, COLOR[level.ordinal()])
                .requiresTool().strength(3.0F, 6.0F)
                .sounds(BlockSoundGroup.COPPER).luminance(state -> state.get(ON) ? 10 : 0));
        setDefaultState(stateManager.getDefaultState().with(ON, false));
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
        return state.get(ON);
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
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(ON);
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
