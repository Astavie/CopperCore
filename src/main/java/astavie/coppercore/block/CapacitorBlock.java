package astavie.coppercore.block;

import java.util.Random;

import astavie.coppercore.CopperCore;
import astavie.coppercore.block.entity.CapacitorBlockEntity;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CapacitorBlock extends OxidizableBlock implements BlockEntityProvider {

    // TODO: Figure out how to make oxidizing and waxing work.
    // TODO: (Or hope Mojang fixes this mess)

    public static final BooleanProperty ON = BooleanProperty.of("on");

    private static final MapColor[] COLOR = new MapColor[] { MapColor.ORANGE, MapColor.TERRACOTTA_LIGHT_GRAY,
            MapColor.DARK_AQUA, MapColor.TEAL };

    private final boolean waxed;

    public CapacitorBlock(OxidizationLevel level, boolean waxed) {
        super(level, FabricBlockSettings.of(Material.METAL, COLOR[level.ordinal()]).requiresTool().strength(3.0F, 6.0F)
                .sounds(BlockSoundGroup.COPPER).luminance(state -> state.get(ON) ? 10 : 0));
        this.waxed = waxed;
        setDefaultState(stateManager.getDefaultState().with(ON, false));
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !waxed && super.hasRandomTicks(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!waxed) {
            super.randomTick(state, world, pos, random);
        }
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return world.isClient || type != CopperCore.CAPACITOR_BLOCK_ENTITY ? null
                : (a, b, c, d) -> CapacitorBlockEntity.serverTick(a, b, c, (CapacitorBlockEntity) d);
    }

}
