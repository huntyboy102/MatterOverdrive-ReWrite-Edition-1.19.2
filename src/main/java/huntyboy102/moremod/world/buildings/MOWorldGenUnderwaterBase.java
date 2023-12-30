package huntyboy102.moremod.world.buildings;

import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.blocks.BlockDecorative;
import huntyboy102.moremod.blocks.BlockPatternStorage;
import huntyboy102.moremod.blocks.BlockTritaniumCrate;
import huntyboy102.moremod.blocks.BlockWeaponStation;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.tile.TileEntityHoloSign;
import huntyboy102.moremod.tile.TileEntityTritaniumCrate;
import huntyboy102.moremod.tile.TileEntityWeaponStation;
import huntyboy102.moremod.util.MOInventoryHelper;
import huntyboy102.moremod.util.WeaponFactory;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.world.MOImageGen;
import huntyboy102.moremod.world.MOLootTableManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

import java.util.Random;

public class MOWorldGenUnderwaterBase extends MOWorldGenBuilding {
	private static final int MIN_DISTANCE_APART = 2048;
	private final String[] holoTexts;
	final NoiseGeneratorSimplex noise;

	public MOWorldGenUnderwaterBase(String name) {
		super(name, new ResourceLocation(Reference.PATH_WORLD_TEXTURES + "underwater_base.png"), 43, 43);
		holoTexts = new String[] { "Critical\nError", "Contacting\nSection 9", "System\nFailure",
				"Emergency\nPower\nOffline", "System\nReboot\nFailure", "Help Me", "I Need\nWater" };
		validSpawnBlocks = new Block[] { Blocks.WATER };
		setyOffset(-24);
		noise = new NoiseGeneratorSimplex(new Random());
		for (BlockDecorative blockDecorative : BlockDecorative.decorativeBlocks) {
			addMapping(blockDecorative.getBlockColor(0), blockDecorative);
		}
		addMapping(0xdc979c, Blocks.TALLGRASS);
		addMapping(0x77d1b6, Blocks.RED_FLOWER);
		addMapping(0xd2fb50, MatterOverdrive.BLOCKS.industrialGlass);
		addMapping(0xc1e4e, Blocks.FARMLAND);
		addMapping(0xa7ac65, MatterOverdrive.BLOCKS.tritaniumCrateColored[EnumDyeColor.ORANGE.getMetadata()]); // orange
																												// crate
		addMapping(0xd6a714, Blocks.STAINED_GLASS);
		addMapping(0x2c5ae9, MatterOverdrive.BLOCKS.weapon_station);
		addMapping(0xacd8c, MatterOverdrive.BLOCKS.androidStation);
		addMapping(0x7018f9, MatterOverdrive.BLOCKS.tritaniumCrateColored[EnumDyeColor.LIGHT_BLUE.getMetadata()]); // light
																													// blue
		addMapping(0x4657cc, MatterOverdrive.BLOCKS.tritaniumCrateColored[EnumDyeColor.LIME.getMetadata()]); // lime
		addMapping(0x1f2312, MatterOverdrive.BLOCKS.tritaniumCrateColored[EnumDyeColor.WHITE.getMetadata()]); // white
		addMapping(0xd3371d, MatterOverdrive.BLOCKS.machine_hull);
		addMapping(0x3640f9, Blocks.STONE_BUTTON);
		addMapping(0xeff73d, MatterOverdrive.BLOCKS.network_switch);
		addMapping(0x5a6388, Blocks.AIR);
		addMapping(0xbf19a9, Blocks.GRASS);
		addMapping(0xc05e5e, Blocks.FLOWER_POT);
		addMapping(0x4d8dd3, MatterOverdrive.BLOCKS.pattern_monitor);
		addMapping(0xdb9c3a, MatterOverdrive.BLOCKS.holoSign);
		addMapping(0x68b68c, MatterOverdrive.BLOCKS.matter_analyzer);
		addMapping(0x2cb0c7, MatterOverdrive.BLOCKS.starMap);
		addMapping(0x1b2ff7, MatterOverdrive.BLOCKS.network_pipe);
		addMapping(0x5eaab, MatterOverdrive.BLOCKS.tritaniumCrate);
		addMapping(0x11003e, MatterOverdrive.BLOCKS.chargingStation);
		addMapping(0xb31e83, Blocks.CARROTS);
		addMapping(0xc78e77, MatterOverdrive.BLOCKS.replicator);
		addMapping(0x338a42, Blocks.POTATOES);
		addMapping(0xbdea8f, Blocks.LADDER);
		addMapping(0x4d12f4, MatterOverdrive.BLOCKS.pattern_storage);
		addMapping(0xf7d20b, Blocks.SAPLING);
		addMapping(0x854b38, Blocks.IRON_DOOR);
		addMapping(0xff00ff, Blocks.AIR);
	}

	@Override
	public int getMetaFromColor(int color, Random random) {
		return 255 - getAlphaFromColor(color);
	}

	@Override
	public MOImageGen.ImageGenWorker getNewWorkerInstance() {
		return new WorldGenBuildingWorker();
	}

	@Override
	protected void onGeneration(Random random, World world, BlockPos pos, WorldGenBuildingWorker worker) {

	}

	@Override
	public boolean isLocationValid(World world, BlockPos pos) {
		return isPointDeepEnough(world, pos) && isPointDeepEnough(world, pos.add(layerWidth, 0, 0))
				&& isPointDeepEnough(world, pos.add(layerWidth, 0, layerHeight))
				&& isPointDeepEnough(world, pos.add(0, 0, layerHeight));
	}

	public boolean isPointDeepEnough(World world, BlockPos pos) {
		int blocksInWater = 0;
		while (pos.getY() > 0) {
			if (world.getBlockState(pos).getBlock() == Blocks.WATER
					|| world.getBlockState(pos).getBlock() == Blocks.FLOWING_WATER) {
				blocksInWater++;
			} else {
				return blocksInWater > 26;
			}
			pos = pos.add(0, -1, 0);
		}
		return false;
	}

	@Override
	public boolean shouldGenerate(Random random, World world, BlockPos pos) {
		// deep_ocean biome
		return world.getBiome(pos).equals(Biome.REGISTRY.getObject(new ResourceLocation("minecraft", "deep_ocean")))
				&& isFarEnoughFromOthers(world, pos.getX(), pos.getZ(), MIN_DISTANCE_APART);
	}

	@Override
	public void onBlockPlace(World world, IBlockState state, BlockPos pos, Random random, int color,
			MOImageGen.ImageGenWorker worker) {
		if (state.getBlock() == MatterOverdrive.BLOCKS.holoSign) {
			if (colorsMatch(color, 0xd8ff00)) {
				world.setBlockState(pos, state.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.EAST), 3);
			} else if (colorsMatch(color, 0xaccb00)) {
				world.setBlockState(pos, state.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.WEST), 3);
			}
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof TileEntityHoloSign) {
				if (random.nextInt(100) < 30) {
					((TileEntityHoloSign) tileEntity).setText(holoTexts[random.nextInt(holoTexts.length)]);
				}
			}
		} else if (state.getBlock() instanceof BlockTritaniumCrate) {
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof IInventory) {
				TileEntityTritaniumCrate chest = (TileEntityTritaniumCrate) tileEntity;
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world);
				LootTable loottable = world.getLootTableManager()
						.getLootTableFromLocation(MOLootTableManager.MO_CRASHED_SHIP);
				loottable.fillInventory(chest, world.rand, lootcontext$builder.build());
				QuestStack questStack = MatterOverdrive.QUEST_FACTORY.generateQuestStack(random,
						MatterOverdrive.QUESTS.getQuestByName("crash_landing"));
				questStack.getTagCompound().setLong("pos", pos.toLong());
				MOInventoryHelper.insertItemStackIntoInventory((IInventory) tileEntity, questStack.getContract(),
						EnumFacing.DOWN);
			}

		} else if (state.getBlock() instanceof BlockWeaponStation) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof TileEntityWeaponStation) {
				if (random.nextInt(200) < 10) {
					((TileEntityWeaponStation) tileEntity).setInventorySlotContents(
							((TileEntityWeaponStation) tileEntity).INPUT_SLOT,
							MatterOverdrive.WEAPON_FACTORY.getRandomDecoratedEnergyWeapon(
									new WeaponFactory.WeaponGenerationContext(3, null, true)));
				}
			}
		} else if (state.getBlock() instanceof BlockPatternStorage) {
			if (colorsMatch(color, 0x4d12f4)) {
				world.setBlockState(pos, state.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.WEST), 3);
			}
		}
	}
}