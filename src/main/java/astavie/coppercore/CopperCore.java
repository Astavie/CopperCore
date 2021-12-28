package astavie.coppercore;

import org.jetbrains.annotations.NotNull;

import astavie.coppercore.block.CapacitorBlock;
import astavie.coppercore.block.WaxedCapacitorBlock;
import astavie.coppercore.block.entity.CapacitorBlockEntity;
import astavie.coppercore.conductor.ConductorDirection;
import astavie.coppercore.conductor.ConductorProvider;
import astavie.coppercore.conductor.ICapacitor;
import astavie.coppercore.conductor.IConductor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CopperCore implements ModInitializer {

	public static BlockEntityType<CapacitorBlockEntity> CAPACITOR_BLOCK_ENTITY;

	public static Block COPPER_CAPACITOR;
	public static Block EXPOSED_COPPER_CAPACITOR;
	public static Block WEATHERED_COPPER_CAPACITOR;
	public static Block OXIDIZED_COPPER_CAPACITOR;
	public static Block WAXED_COPPER_CAPACITOR;
	public static Block WAXED_EXPOSED_COPPER_CAPACITOR;
	public static Block WAXED_WEATHERED_COPPER_CAPACITOR;
	public static Block WAXED_OXIDIZED_COPPER_CAPACITOR;

	public static final String MOD_ID = "coppercore";

	public static final BlockApiLookup<IConductor, @NotNull ConductorDirection> CONDUCTOR = BlockApiLookup
			.get(new Identifier(MOD_ID, "conductor"), IConductor.class, ConductorDirection.class);
	public static final BlockApiLookup<ICapacitor, @NotNull ConductorDirection> CAPACITOR = BlockApiLookup
			.get(new Identifier(MOD_ID, "capacitor"), ICapacitor.class, ConductorDirection.class);

	public static final BlockApiProvider<IConductor, @NotNull ConductorDirection> CONDUCTOR_FULL = new ConductorProvider.Full();
	public static final BlockApiProvider<IConductor, @NotNull ConductorDirection> CONDUCTOR_SLAB = new ConductorProvider.Slab();
	public static final BlockApiProvider<IConductor, @NotNull ConductorDirection> CONDUCTOR_STAIRS = new ConductorProvider.Stairs();
	public static final BlockApiProvider<IConductor, @NotNull ConductorDirection> CONDUCTOR_ROD = new ConductorProvider.Rod();

	@Override
	public void onInitialize() {
		// BLOCKS
		COPPER_CAPACITOR = registerBlock(new CapacitorBlock(OxidationLevel.UNAFFECTED),
				"copper_capacitor");
		EXPOSED_COPPER_CAPACITOR = registerBlock(new CapacitorBlock(OxidationLevel.EXPOSED),
				"exposed_copper_capacitor");
		WEATHERED_COPPER_CAPACITOR = registerBlock(new CapacitorBlock(OxidationLevel.WEATHERED),
				"weathered_copper_capacitor");
		OXIDIZED_COPPER_CAPACITOR = registerBlock(new CapacitorBlock(OxidationLevel.OXIDIZED),
				"oxidized_copper_capacitor");

		WAXED_COPPER_CAPACITOR = registerBlock(new WaxedCapacitorBlock(OxidationLevel.UNAFFECTED),
				"waxed_copper_capacitor");
		WAXED_EXPOSED_COPPER_CAPACITOR = registerBlock(new WaxedCapacitorBlock(OxidationLevel.EXPOSED),
				"waxed_exposed_copper_capacitor");
		WAXED_WEATHERED_COPPER_CAPACITOR = registerBlock(new WaxedCapacitorBlock(OxidationLevel.WEATHERED),
				"waxed_weathered_copper_capacitor");
		WAXED_OXIDIZED_COPPER_CAPACITOR = registerBlock(new WaxedCapacitorBlock(OxidationLevel.OXIDIZED),
				"waxed_oxidized_copper_capacitor");
		
		// Oxidizing and waxing
		OxidizableBlocksRegistry.registerOxidizableBlockPair(COPPER_CAPACITOR, EXPOSED_COPPER_CAPACITOR);
		OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_COPPER_CAPACITOR, WEATHERED_COPPER_CAPACITOR);
		OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_COPPER_CAPACITOR, OXIDIZED_COPPER_CAPACITOR);

		OxidizableBlocksRegistry.registerWaxableBlockPair(COPPER_CAPACITOR, WAXED_COPPER_CAPACITOR);
		OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_COPPER_CAPACITOR, WAXED_EXPOSED_COPPER_CAPACITOR);
		OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_COPPER_CAPACITOR, WAXED_WEATHERED_COPPER_CAPACITOR);
		OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_COPPER_CAPACITOR, WAXED_OXIDIZED_COPPER_CAPACITOR);

		// BLOCK ENTITIES
		CAPACITOR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
				new Identifier(MOD_ID, "copper_capacitor"),
				FabricBlockEntityTypeBuilder.create(CapacitorBlockEntity::new, COPPER_CAPACITOR,
						EXPOSED_COPPER_CAPACITOR, WEATHERED_COPPER_CAPACITOR, OXIDIZED_COPPER_CAPACITOR,
						WAXED_COPPER_CAPACITOR, WAXED_EXPOSED_COPPER_CAPACITOR, WAXED_WEATHERED_COPPER_CAPACITOR,
						WAXED_OXIDIZED_COPPER_CAPACITOR).build());

		// LOOKUP APIS
		CONDUCTOR.registerForBlocks(CONDUCTOR_FULL, Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER,
				Blocks.OXIDIZED_COPPER, Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER,
				Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER,
				Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_CUT_COPPER,
				Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER,
				COPPER_CAPACITOR, EXPOSED_COPPER_CAPACITOR, WEATHERED_COPPER_CAPACITOR, OXIDIZED_COPPER_CAPACITOR,
				WAXED_COPPER_CAPACITOR, WAXED_EXPOSED_COPPER_CAPACITOR, WAXED_WEATHERED_COPPER_CAPACITOR,
				WAXED_OXIDIZED_COPPER_CAPACITOR);
		CONDUCTOR.registerForBlocks(CONDUCTOR_SLAB, Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB,
				Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB,
				Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
				Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB);
		CONDUCTOR.registerForBlocks(CONDUCTOR_STAIRS, Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS,
				Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS,
				Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
				Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
		CONDUCTOR.registerForBlocks(CONDUCTOR_ROD, Blocks.LIGHTNING_ROD);

		CAPACITOR.registerSelf(CAPACITOR_BLOCK_ENTITY);
	}

	private Block registerBlock(Block block, String name) {
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, name),
				new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
		return block;
	}

}
