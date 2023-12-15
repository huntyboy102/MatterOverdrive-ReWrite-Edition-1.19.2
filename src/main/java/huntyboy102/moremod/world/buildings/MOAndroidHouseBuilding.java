
package huntyboy102.moremod.world.buildings;

import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.blocks.BlockChargingStation;
import huntyboy102.moremod.blocks.BlockReplicator;
import huntyboy102.moremod.blocks.BlockTritaniumCrate;
import huntyboy102.moremod.blocks.BlockWeaponStation;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.entity.monster.EntityMeleeRougeAndroidMob;
import huntyboy102.moremod.entity.monster.EntityRangedRogueAndroidMob;
import huntyboy102.moremod.tile.TileEntityHoloSign;
import huntyboy102.moremod.tile.TileEntityTritaniumCrate;
import huntyboy102.moremod.tile.TileEntityWeaponStation;
import huntyboy102.moremod.util.MOInventoryHelper;
import huntyboy102.moremod.util.WeaponFactory;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.world.MOImageGen;
import huntyboy102.moremod.world.MOLootTableManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Random;

public class MOAndroidHouseBuilding extends MOWorldGenBuilding {
	private static final int MIN_DISTANCE_APART = 1000;
	private final String[] holoTexts;
	private int airLeeway;

	public MOAndroidHouseBuilding(String name) {
		super(name, new ResourceLocation(Reference.PATH_WORLD_TEXTURES + "android_house.png"), 21, 21);
		holoTexts = new String[] { "Critical\nError", "Contacting\nSection 9", "System\nFailure",
				"Emergency\nPower\nOffline", "System\nReboot\nFailure", "Help Me", "I Need\nWater" };
		setMaxDistanceToAir(airLeeway);
		setyOffset(-4);
		addMapping(0x00fffc, MatterOverdrive.BLOCKS.decorative_beams,
				MatterOverdrive.BLOCKS.decorative_carbon_fiber_plate, MatterOverdrive.BLOCKS.decorative_white_plate);
		addMapping(0x623200, Blocks.DIRT);
		addMapping(0xffa200, MatterOverdrive.BLOCKS.decorative_floor_tiles);
		addMapping(0xfff600, MatterOverdrive.BLOCKS.decorative_holo_matrix);
		addMapping(0x80b956, Blocks.GRASS);
		addMapping(0x539ac3, MatterOverdrive.BLOCKS.decorative_tritanium_plate);
		addMapping(0xb1c8d5, MatterOverdrive.BLOCKS.decorative_floor_noise,
				MatterOverdrive.BLOCKS.decorative_floor_tiles, MatterOverdrive.BLOCKS.decorative_floor_tile);
		addMapping(0x5f6569, MatterOverdrive.BLOCKS.decorative_vent_dark);
		addMapping(0xf1f1f1, Blocks.AIR);
		addMapping(0xe400ff, MatterOverdrive.BLOCKS.starMap);
		addMapping(0x1850ad, MatterOverdrive.BLOCKS.decorative_clean);
		addMapping(0x9553c3, MatterOverdrive.BLOCKS.industrialGlass);
		addMapping(0x35d6e0, MatterOverdrive.BLOCKS.replicator);
		addMapping(0x35e091, MatterOverdrive.BLOCKS.network_switch);
		addMapping(0xc8d43d, MatterOverdrive.BLOCKS.tritaniumCrateColored[EnumDyeColor.ORANGE.getMetadata()]); // orange
																												// crate
		addMapping(0x2a4071, MatterOverdrive.BLOCKS.androidStation, MatterOverdrive.BLOCKS.weapon_station);
		addMapping(0xa13e5f, MatterOverdrive.BLOCKS.network_pipe);
		addMapping(0xa16a3e, MatterOverdrive.BLOCKS.chargingStation);
		addMapping(0x416173, MatterOverdrive.BLOCKS.decorative_tritanium_plate_stripe);
		addMapping(0x187716, MatterOverdrive.BLOCKS.pattern_monitor);
		addMapping(0xac7c1e, MatterOverdrive.BLOCKS.decorative_vent_bright);
		addMapping(0x007eff, MatterOverdrive.BLOCKS.decorative_stripes);
	}

	public boolean isFlat(World world, BlockPos pos) {
		BlockPos y10 = world.getHeight(pos.add(layerWidth, 0, 0));
		BlockPos y11 = world.getHeight(pos.add(layerWidth, 0, layerHeight));
		BlockPos y01 = world.getHeight(pos.add(0, 0, layerHeight));
		if (Math.abs(pos.getY() - y10.getY()) <= airLeeway && Math.abs(pos.getY() - y11.getY()) <= airLeeway
				&& Math.abs(pos.getY() - y01.getY()) <= airLeeway) {
			return blockBelowMatches(airLeeway, world, Blocks.GRASS, pos)
					&& blockBelowMatches(airLeeway, world, Blocks.GRASS, pos.add(layerWidth, 0, 0))
					&& blockBelowMatches(airLeeway, world, Blocks.GRASS, pos.add(0, 0, layerHeight))
					&& blockBelowMatches(airLeeway, world, Blocks.GRASS, pos.add(layerWidth, 0, layerHeight));
		}
		return false;
	}

	private boolean blockBelowMatches(int airLeeway, World world, Block block, BlockPos pos) {
		for (int i = 0; i < airLeeway; i++) {
			if (world.getBlockState(pos.add(0, -i, 0)).getBlock() == block) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isLocationValid(World world, BlockPos pos) {
		pos = new BlockPos(pos.getX(), Math.min(pos.getY(), world.getHeight()), pos.getZ());
		return world.getBlockState(pos).getBlock() == Blocks.GRASS
				&& world.getBlockState(pos.add(layerWidth, 0, 0)) == Blocks.GRASS
				&& world.getBlockState(pos.add(0, 0, layerHeight)) == Blocks.GRASS
				&& world.getBlockState(pos.add(layerWidth, 0, layerHeight)) == Blocks.GRASS
				&& world.getBlockState(pos.add(0, 16, 0)) == Blocks.GRASS
				&& world.getBlockState(pos.add(layerWidth, 16, 0)) == Blocks.GRASS
				&& world.getBlockState(pos.add(0, 16, layerHeight)) == Blocks.GRASS
				&& world.getBlockState(pos.add(layerWidth, 16, layerHeight)) == Blocks.GRASS;
	}
	
	@Override
	protected void onGeneration(Random random, World world, BlockPos pos, WorldGenBuildingWorker worker) {
		spawnLegendary(world, random, pos.add(12, 4, 10));
		for (int i = 0; i < random.nextInt(3) + 3; i++) {
			spawnAndroid(world, random, pos.add(7, i, 8));
		}
	}

	@Override
	public boolean shouldGenerate(Random random, World world, BlockPos pos) {
		return world.provider.getDimension() == 0
				&& world.getBiome(pos) != Biome.REGISTRY.getObject(new ResourceLocation("minecraft", "ocean"))
				&& world.getBiome(pos) != Biome.REGISTRY.getObject(new ResourceLocation("minecraft", "frozen_ocean"))
				&& world.getBiome(pos) != Biome.REGISTRY.getObject(new ResourceLocation("minecraft", "deep_ocean"))
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
			world.setBlockState(pos, state.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.WEST), 3);
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
		} else if (state.getBlock() instanceof BlockChargingStation) {
			if (colorsMatch(color, 0xa16a3e)) {
				world.setBlockState(pos, state.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.SOUTH), 3);
			}
		} else if (state.getBlock() instanceof BlockReplicator) {
			if (colorsMatch(color, 0x35d6e0)) {
				world.setBlockState(pos, state.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.EAST), 3);
			}
		}
	}

	public void spawnAndroid(World world, Random random, BlockPos pos) {
		if (random.nextInt(100) < 60) {
			EntityRangedRogueAndroidMob androidMob = new EntityRangedRogueAndroidMob(world);
			androidMob.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			world.spawnEntity(androidMob);
			androidMob.onInitialSpawn(world.getDifficultyForLocation(pos), null);
			androidMob.enablePersistence();
		} else {
			EntityMeleeRougeAndroidMob androidMob = new EntityMeleeRougeAndroidMob(world);
			androidMob.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			world.spawnEntity(androidMob);
			androidMob.onInitialSpawn(world.getDifficultyForLocation(pos), null);
			androidMob.enablePersistence();
		}
	}

	public void spawnLegendary(World world, Random random, BlockPos pos) {
		EntityRangedRogueAndroidMob legendaryMob = new EntityRangedRogueAndroidMob(world, 3, true);
		legendaryMob.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		world.spawnEntity(legendaryMob);
		legendaryMob.onInitialSpawn(world.getDifficultyForLocation(pos), null);
		legendaryMob.enablePersistence();
	}

	@Override
	public int getMetaFromColor(int color, Random random) {
		int alpha = 255 - getAlphaFromColor(color);
		return (int) ((alpha / 255d) * 10d);
	}

	@Override
	public WorldGenBuildingWorker getNewWorkerInstance() {
		return new WorldGenBuildingWorker();
	}
}
