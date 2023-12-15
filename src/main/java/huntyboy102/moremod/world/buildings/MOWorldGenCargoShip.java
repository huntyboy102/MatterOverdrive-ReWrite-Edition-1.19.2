package huntyboy102.moremod.world.buildings;

import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.blocks.BlockDecorative;
import huntyboy102.moremod.blocks.BlockTritaniumCrate;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.tile.TileEntityHoloSign;
import huntyboy102.moremod.tile.TileEntityTritaniumCrate;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.quest.logic.QuestLogicBlockInteract;
import huntyboy102.moremod.world.MOLootTableManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Random;

public class MOWorldGenCargoShip extends MOWorldGenBuilding<MOWorldGenCargoShip.Worker> {
	private static final int MIN_DISTANCE_APART = 4096;
	private final String[] holoTexts;
	final NoiseGeneratorSimplex noise;

	public MOWorldGenCargoShip(String name) {
		super(name, new ResourceLocation(Reference.PATH_WORLD_TEXTURES + "cargo_ship.png"), 58, 23);
		holoTexts = new String[] { "Critical\nError", "Contacting\nSection 9", "System\nFailure",
				"Emergency\nPower\nOffline", "System\nReboot\nFailure", "Help Me", "I Need\nWater" };
		noise = new NoiseGeneratorSimplex(new Random());
		for (BlockDecorative blockDecorative : BlockDecorative.decorativeBlocks) {
			addMapping(blockDecorative.getBlockColor(0), blockDecorative);
		}
		setyOffset(86);
		addMapping(0xdb9c3a, MatterOverdrive.BLOCKS.holoSign);
		addMapping(0x5fffbe, MatterOverdrive.BLOCKS.transporter);
		addMapping(0xd2fb50, MatterOverdrive.BLOCKS.industrialGlass);
		addMapping(0xdc01d8, Blocks.WOODEN_PRESSURE_PLATE);
		addMapping(0xfc6b34, new BlockMapping(true, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE,
				MatterOverdrive.BLOCKS.tritaniumOre));
		addMapping(0xeff73d, MatterOverdrive.BLOCKS.fusionReactorIO);
		addMapping(0x1b2ff7, MatterOverdrive.BLOCKS.network_pipe);
		addMapping(0x1f2312, MatterOverdrive.BLOCKS.tritaniumCrateColored[EnumDyeColor.LIME.getMetadata()]);
		addMapping(0xab4824, Blocks.OAK_FENCE);
		addMapping(0x68d738, Blocks.CARPET);
		addMapping(0xbdea8f, Blocks.LADDER);
		addMapping(0xd1626, MatterOverdrive.BLOCKS.network_switch);
		addMapping(0xa8ed1c, MatterOverdrive.BLOCKS.network_pipe);
		addMapping(0x4b285d, Blocks.OAK_STAIRS);
		addMapping(0xcfd752, MatterOverdrive.BLOCKS.network_router);
		addMapping(0x4d8dd3, MatterOverdrive.BLOCKS.pattern_monitor);
		addMapping(0x6b3534, Blocks.BED);
		addMapping(0xff00ff, Blocks.AIR);
		addMapping(0x69960c, MatterOverdrive.BLOCKS.tritaniumCrateColored[EnumDyeColor.RED.getMetadata()]);
	}

	@Override
	protected void onGeneration(Random random, World world, BlockPos pos, Worker worker) {
		if (worker.contractDestination != null) {
			TileEntity tileEntity = world.getTileEntity(worker.contractDestination);
			if (tileEntity instanceof TileEntityTritaniumCrate) {
				ItemStack contract = worker.contractQuest.getContract();
				((TileEntityTritaniumCrate) tileEntity).getInventory().addItem(contract);
			}
		}
	}

	@Override
	public int getMetaFromColor(int color, Random random) {
		return 255 - getAlphaFromColor(color);
	}

	@Override
	public boolean isLocationValid(World world, BlockPos pos) {
		pos = new BlockPos(pos.getX(), Math.min(pos.getY() + 86, world.getHeight() - 18), pos.getZ());
		return world.getBlockState(pos).getBlock() == Blocks.AIR
				&& world.getBlockState(pos.add(layerWidth, 0, 0)) == Blocks.AIR
				&& world.getBlockState(pos.add(0, 0, layerHeight)) == Blocks.AIR
				&& world.getBlockState(pos.add(layerWidth, 0, layerHeight)) == Blocks.AIR
				&& world.getBlockState(pos.add(0, 16, 0)) == Blocks.AIR
				&& world.getBlockState(pos.add(layerWidth, 16, 0)) == Blocks.AIR
				&& world.getBlockState(pos.add(0, 16, layerHeight)) == Blocks.AIR
				&& world.getBlockState(pos.add(layerWidth, 16, layerHeight)) == Blocks.AIR;
	}

	@Override
	public boolean shouldGenerate(Random random, World world, BlockPos pos) {
		return world.getBiome(pos) != Biome.REGISTRY.getObject(new ResourceLocation("minecraft", "hell"))
				&& isFarEnoughFromOthers(world, pos.getX(), pos.getZ(), MIN_DISTANCE_APART);
	}

	@Override
	public Worker getNewWorkerInstance() {
		return new Worker();
	}

	@Override
	public void onBlockPlace(World world, IBlockState blockState, BlockPos pos, Random random, int color,
			Worker worker) {
		if (blockState.getBlock() == MatterOverdrive.BLOCKS.holoSign) {
			if (colorsMatch(color, 0xd8ff00)) {
				world.setBlockState(pos, blockState.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.EAST), 3);
			} else if (colorsMatch(color, 0xaccb00)) {
				world.setBlockState(pos, blockState.withProperty(MOBlock.PROPERTY_DIRECTION, EnumFacing.WEST), 3);
			}
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof TileEntityHoloSign) {
				if (random.nextInt(100) < 30) {
					((TileEntityHoloSign) tileEntity).setText(holoTexts[random.nextInt(holoTexts.length)]);
				}
			}
		} else if (blockState.getBlock() instanceof BlockTritaniumCrate) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof IInventory) {
				TileEntityTritaniumCrate chest = (TileEntityTritaniumCrate) tileEntity;
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world);
				LootTable loottable = world.getLootTableManager()
						.getLootTableFromLocation(MOLootTableManager.MO_CRASHED_SHIP);
				loottable.fillInventory(chest, world.rand, lootcontext$builder.build());
				if (colorsMatch(color, 0x69960c)) {
					TileEntity tritaniumCrate = world.getTileEntity(pos);
					if (tritaniumCrate instanceof TileEntityTritaniumCrate) {
						worker.setQuestPos(pos);
						ItemStack itemStack = new ItemStack(MatterOverdrive.ITEMS.isolinear_circuit)
								.setStackDisplayName("Trade Route Agreement");
						((TileEntityTritaniumCrate) tritaniumCrate).getInventory().addItem(itemStack);
					}
				} else if (colorsMatch(color, 0x1f2312)) {
					TileEntity tritaniumCrate = world.getTileEntity(pos);
					if (tritaniumCrate instanceof TileEntityTritaniumCrate) {
						if (!worker.questAdded) {
							worker.markQuestAdded();
							worker.contractDestination = pos;
						}
					}
				}
			}
		}
	}

	static class Worker extends MOWorldGenBuilding.WorldGenBuildingWorker {
		private QuestStack contractQuest;
		private BlockPos contractDestination;
		private boolean questAdded;

		public Worker() {
			contractQuest = new QuestStack(MatterOverdrive.QUESTS.getQuestByName("trade_route"));
		}

		private void setQuestPos(BlockPos pos) {
			QuestLogicBlockInteract.setBlockPosition(contractQuest, pos);
		}

		private void markQuestAdded() {
			this.questAdded = true;
		}
	}
}