
package huntyboy102.moremod.init;

import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.blocks.includes.MOBlockOre;
import huntyboy102.moremod.blocks.world.DilithiumOre;
import huntyboy102.moremod.items.includes.MOMachineBlockItem;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.internal.IItemBlockFactory;
import huntyboy102.moremod.blocks.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemColored;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import static net.minecraftforge.versions.forge.ForgeVersion.MOD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MatterOverdriveBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

	public static final List<Recipe> recipes = new ArrayList<>();
	public static List<Block> blocks = new ArrayList<>();
	public static List<Item> items = new ArrayList<>();

	public final MaterialTritanium TRITANIUM = new MaterialTritanium(MaterialColor.CLAY);
	// Materials
	public DilithiumOre dilithium_ore;
	public MOBlockOre tritaniumOre;
	public MOBlockOre tritanium_block;
	// Crafting
	public MOBlock machine_hull;
	// Matter Network
	public BlockDecomposer decomposer;
	public BlockMatterRecycler recycler;
	public BlockReplicator replicator;
	public BlockMatterPipe matter_pipe;
	public BlockMatterPipe heavy_matter_pipe;
	public BlockNetworkPipe network_pipe;
	public BlockNetworkRouter network_router;
	public BlockMatterAnalyzer matter_analyzer;
	public BlockPatternMonitor pattern_monitor;
	public BlockPatternStorage pattern_storage;
	public BlockNetworkSwitch network_switch;
	// Energy Generation
	public BlockSolarPanel solar_panel;
	// Gravitational Anomaly / Fusion Reactor
	public BlockGravitationalAnomaly gravitational_anomaly;
	public BlockGravitationalStabilizer gravitational_stabilizer;
	public BlockFusionReactorController fusion_reactor_controller;
	public BlockFusionReactorCoil fusion_reactor_coil;
	public BlockFusionReactorIO fusionReactorIO;
	// Fluids
	public BlockFluidMatterPlasma blockMatterPlasma;
	public BlockFluidClassic blockMoltenTritanium;
	// Storage
	public BlockTritaniumCrate tritaniumCrate;
	public BlockTritaniumCrate[] tritaniumCrateColored;
	// Machines
	public BlockInscriber inscriber;
	public BlockContractMarket contractMarket;
	public BlockAndroidSpawner androidSpawner;
	public BlockSpacetimeAccelerator spacetimeAccelerator;
	public BlockPylon pylon;
	// Misc
	public BlockTransporter transporter;
	public BlockStarMap starMap;
	public BlockHoloSign holoSign;
	public BlockWeaponStation weapon_station;
	public BlockAndroidStation androidStation;
	public BlockChargingStation chargingStation;
	// Decorative
	public BlockDecorative decorative_stripes;
	public BlockDecorative decorative_coils;
	public BlockDecorative decorative_clean;
	public BlockDecorative decorative_vent_dark;
	public BlockDecorative decorative_vent_bright;
	public BlockDecorative decorative_holo_matrix;
	public BlockDecorative decorative_tritanium_plate;
	public BlockDecorative decorative_tritanium_plate_stripe;
	public BlockDecorative decorative_carbon_fiber_plate;
	public BlockDecorative decorative_matter_tube;
	public BlockDecorative decorative_beams;
	public BlockDecorative decorative_floor_tiles;
	public BlockDecorative decorative_floor_tile;
	public BlockDecorative decorative_floor_noise;
	public BlockDecorative decorative_white_plate;
	public BlockDecorative decorative_separator;
	public BlockDecorative decorative_tritanium_lamp;
	public BlockDecorative decorative_tritanium_plate_colored;
	public BlockDecorative decorative_engine_exhaust_plasma;
	public BlockMicrowave microwave;
	public BlockIndustrialGlass industrialGlass;
	private int registeredCount = 0;

	// Test new crates.
	public BlockNewTritaniumCrate new_tritanium_crate_base;
	public BlockNewTritaniumCrate new_tritanium_crate_white;
	public BlockNewTritaniumCrate new_tritanium_crate_purple;

	public void init() {
		MOLog.info("Registering blocks");

//		Materials
		dilithium_ore = register(new DilithiumOre(Material.STONE, "dilithium_ore", "oreDilithium"));
		tritaniumOre = register(new MOBlockOre(Material.STONE, "tritanium_ore", "oreTritanium"));
		tritaniumOre.setHardness(8f).setResistance(5.0F).setHarvestLevel("pickaxe", 2);
		tritanium_block = register(new MOBlockOre(TRITANIUM, "tritanium_block", "blockTritanium"));
		tritanium_block.setHardness(15.0F).setResistance(10.0F).setHarvestLevel("pickaxe", 2);

//		Crafting
		machine_hull = register(new MOBlock(TRITANIUM, "machine_hull"));
		machine_hull.setHardness(15.0F).setResistance(8.0F).setHarvestLevel("pickaxe", 2);

//		Matter Network
		decomposer = register(new BlockDecomposer(TRITANIUM, "decomposer"));
		recycler = register(new BlockMatterRecycler(TRITANIUM, "matter_recycler"));
		replicator = register(new BlockReplicator(TRITANIUM, "replicator"));
		matter_pipe = register(new BlockMatterPipe(TRITANIUM, "matter_pipe"));
		heavy_matter_pipe = register(new BlockHeavyMatterPipe(TRITANIUM, "heavy_matter_pipe"));
		network_pipe = register(new BlockNetworkPipe(TRITANIUM, "network_pipe"));
		network_router = register(new BlockNetworkRouter(TRITANIUM, "network_router"));
		matter_analyzer = register(new BlockMatterAnalyzer(TRITANIUM, "matter_analyzer"));
		pattern_monitor = register(new BlockPatternMonitor(TRITANIUM, "pattern_monitor"));
		pattern_storage = register(new BlockPatternStorage(TRITANIUM, "pattern_storage"));
		network_switch = register(new BlockNetworkSwitch(TRITANIUM, "network_switch"));

//		Energy Generation
		solar_panel = register(new BlockSolarPanel(TRITANIUM, "solar_panel"));

//		Gravitational Anomaly / Fusion Reactor
		gravitational_anomaly = register(new BlockGravitationalAnomaly(Material.PORTAL, "gravitational_anomaly"));
		gravitational_stabilizer = register(new BlockGravitationalStabilizer(TRITANIUM, "gravitational_stabilizer"));
		fusion_reactor_controller = register(new BlockFusionReactorController(TRITANIUM, "fusion_reactor_controller"));
		fusion_reactor_coil = register(new BlockFusionReactorCoil(TRITANIUM, "fusion_reactor_coil"));
		fusionReactorIO = register(new BlockFusionReactorIO(TRITANIUM, "fusion_reactor_io"));

//		Fluids
		blockMatterPlasma = register(new BlockFluidMatterPlasma(OverdriveFluids.matterPlasma, Material.WATER));
		blockMoltenTritanium = register(new BlockFluidMoltenTritanium(OverdriveFluids.moltenTritanium, Material.LAVA));

//		Storage
		tritaniumCrate = register(new BlockTritaniumCrate(TRITANIUM, "tritanium_crate"));
		DyeColor[] colors = DyeColor.values();
		tritaniumCrateColored = new BlockTritaniumCrate[colors.length];
		for (DyeColor color : colors)
			tritaniumCrateColored[color.getMetadata()] = register(
					new BlockTritaniumCrate(TRITANIUM, "tritanium_crate_" + color.getName()));

//		Machines
		inscriber = register(new BlockInscriber(TRITANIUM, "inscriber"));
		contractMarket = register(new BlockContractMarket(TRITANIUM, "contract_market"));
		androidSpawner = register(new BlockAndroidSpawner(TRITANIUM, "android_spawner"));
		spacetimeAccelerator = register(new BlockSpacetimeAccelerator(TRITANIUM, "spacetime_accelerator"));
		pylon = register(new BlockPylon(TRITANIUM, "pylon"));

//		Misc
		transporter = register(new BlockTransporter(TRITANIUM, "transporter"));
		starMap = register(new BlockStarMap(TRITANIUM, "star_map"));
		holoSign = register(new BlockHoloSign(TRITANIUM, "holo_sign"));
		weapon_station = register(new BlockWeaponStation(TRITANIUM, "weapon_station"));
		androidStation = register(new BlockAndroidStation(TRITANIUM, "android_station"));
		chargingStation = register(new BlockChargingStation(TRITANIUM, "charging_station"));

//		Decorative
		// forceGlass = register(new ForceGlass(Material.GLASS, "force_glass"));
		decorative_stripes = register(new BlockDecorative(TRITANIUM, "decorative.stripes", 5, 1, 8, 0xd4b108));
		decorative_coils = register(new BlockDecorative(TRITANIUM, "decorative.coils", 5, 1, 8, 0xb6621e));
		decorative_clean = register(new BlockDecorative(TRITANIUM, "decorative.clean", 5, 1, 8, 0x3b484b));
		decorative_vent_dark = register(new BlockDecorative(TRITANIUM, "decorative.vent.dark", 5, 1, 8, 0x32393c));
		decorative_vent_bright = register(new BlockDecorative(TRITANIUM, "decorative.vent.bright", 5, 1, 8, 0x3f4b4e));
		decorative_holo_matrix = register(new BlockDecorative(TRITANIUM, "decorative.holo_matrix", 3, 1, 4, 0x323b3a));
		decorative_tritanium_plate = register(
				new BlockDecorative(TRITANIUM, "decorative.tritanium_plate", 10, 1, 10, 0x475459));
		decorative_tritanium_plate_stripe = register(
				new BlockDecorative(TRITANIUM, "decorative.tritanium_plate_stripe", 10, 1, 10, 0x576468));
		decorative_carbon_fiber_plate = register(
				new BlockDecorative(TRITANIUM, "decorative.carbon_fiber_plate", 10, 1, 12, 0x1c1f20));
		decorative_matter_tube = register(
				new BlockDecorativeRotated(Material.GLASS, "decorative.matter_tube", 3, 1, 4, 0x5088a5));
		decorative_beams = register(new BlockDecorativeRotated(TRITANIUM, "decorative.beams", 8, 1, 8, 0x1e2220));
		decorative_floor_tiles = register(
				new BlockDecorativeColored(Material.CLAY, "decorative.floor_tiles", 4, 0, 4, 0x958d7c));
		decorative_floor_tile = register(
				new BlockDecorativeColored(Material.CLAY, "decorative.floor_tile", 4, 0, 4, 0xa3a49c));
		decorative_floor_noise = register(
				new BlockDecorative(Material.CLAY, "decorative.floor_noise", 4, 0, 4, 0x7f7e7b));
		decorative_white_plate = register(new BlockDecorative(TRITANIUM, "decorative.white_plate", 8, 1, 8, 0xe3e3e3));
		decorative_separator = register(
				new BlockDecorativeRotated(TRITANIUM, "decorative.separator", 8, 1, 8, 0x303837));
		decorative_tritanium_lamp = register(
				new BlockDecorativeRotated(TRITANIUM, "decorative.tritanium_lamp", 2, 1, 4, 0xd4f8f5));
		decorative_tritanium_lamp.setLightLevel(1);
		decorative_tritanium_plate_colored = register(
				new BlockDecorativeColored(TRITANIUM, "decorative.tritanium_plate_colored", 10, 1, 10, 0x505050));
		decorative_engine_exhaust_plasma = register(
				new BlockDecorative(Material.CACTUS, "decorative.engine_exhaust_plasma", 1, 1, 1, 0x387c9e));
		decorative_engine_exhaust_plasma.setLightLevel(1);
		microwave = register(new BlockMicrowave(Material.IRON, "microwave"));
		industrialGlass = register(new BlockIndustrialGlass(Material.GLASS, "industrial_glass"));

		// Register test crates.
		new_tritanium_crate_base = register(new BlockNewTritaniumCrate(TRITANIUM, "new_tritanium_crate", 0));

		MOLog.info("Finished registering %d blocks", registeredCount);

	}

	protected <T extends Block> T register(T block) {
		BlockItem itemBlock;
		if (block instanceof IItemBlockFactory) {
			itemBlock = ((IItemBlockFactory) block).createItemBlock();
		} else if (block instanceof MOBlockMachine) {
			itemBlock = new MOMachineBlockItem(block);
		} else if (block instanceof BlockDecorativeColored) {
			itemBlock = new ItemColored(block, false);
			itemBlock.setRegistryName(block.getRegistryName());
		} else {
			itemBlock = new ItemBlock(block);
			itemBlock.setRegistryName(block.getRegistryName());
		}
		return register(block, itemBlock, true);
	}

	protected <T extends Block> RegistryObject<T> register(T block, boolean addToItems) {
		BlockItem itemBlock;
		if (block instanceof IItemBlockFactory) {
			itemBlock = ((IItemBlockFactory) block).createItemBlock();
		} else if (block instanceof MOBlockMachine) {
			itemBlock = new MOMachineBlockItem(block);
		} else if (block instanceof BlockDecorativeColored) {
			itemBlock = new ItemColored(block, false);
			itemBlock.setRegistryName(block.getName());
		} else {
			itemBlock = new ItemBlock(block);
			itemBlock.setRegistryName(block.getName());
		}
		return register(block, itemBlock, addToItems);
	}

	protected <T extends Block> RegistryObject<T> register(T block, BlockItem itemBlock, boolean addToItems) {
		if (block instanceof IConfigSubscriber) {
			MatterOverdriveRewriteEdition.CONFIG_HANDLER.subscribe((IConfigSubscriber) block);
		}
		registeredCount++;
		blocks.add(block);

		if (addToItems) {
			items.add(itemBlock);
		}

		return block;
	}

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier) {
		return BLOCKS.register(name, blockSupplier);
	}

	private static <T extends Block> RegistryObject<Item> registerItemBlock(String name, RegistryObject<T> block) {
		return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
	}
}