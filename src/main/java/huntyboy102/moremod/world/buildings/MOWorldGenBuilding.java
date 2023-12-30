
package huntyboy102.moremod.world.buildings;

import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.data.world.GenPositionWorldData;
import huntyboy102.moremod.data.world.WorldPosition2D;
import huntyboy102.moremod.world.MOImageGen;
import huntyboy102.moremod.world.MOWorldGen;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.apache.logging.log4j.Level;

import java.util.Random;

public abstract class MOWorldGenBuilding<T extends MOWorldGenBuilding.WorldGenBuildingWorker> extends MOImageGen<T>
		implements IMOWorldGenBuilding<T> {

	protected Block[] validSpawnBlocks;
	int yOffset = -1;
	int maxDistanceToAir = 2;
	String name;

	public MOWorldGenBuilding(String name, ResourceLocation texture, int layerWidth, int layerHeight) {
		super(texture, layerWidth, layerHeight);
		this.name = name;
		validSpawnBlocks = new Block[] { Blocks.STONE, Blocks.GRASS, Blocks.DIRT };
	}

	@Override
	public void generate(Random random, BlockPos pos, Level world, ChunkGenerator chunkGenerator,
						 BiomeSource chunkProvider, int layer, int placeNotify, T worker) {
		generateFromImage(world, random, pos.add(0, getYOffset(), 0), layer, placeNotify, worker);
	}

	public boolean locationIsValidSpawn(Level world, BlockPos pos) {
		int distanceToAir = 0;
		BlockState blockState = world.getBlockState(pos);

		while (blockState.getBlock() != Blocks.AIR) {
			if (distanceToAir > getMaxDistanceToAir()) {
				return false;
			}

			distanceToAir++;
			blockState = world.getBlockState(pos.add(0, distanceToAir, 0));
		}

		pos = pos.add(0, distanceToAir - 1, 0);

		BlockState block = world.getBlockState(pos);
		BlockState blockAbove = world.getBlockState(pos.add(0, 1, 0));
		BlockState blockBelow = world.getBlockState(pos.add(0, -1, 0));

		for (Block x : getValidSpawnBlocks()) {
			if (!blockAbove.getBlock().isAir(blockAbove, world, pos.add(0, 1, 0))) {
				return false;
			}
			if (block.getBlock() == x) {
				return true;
			} else if (block.getBlock() == Blocks.SNOW && blockBelow.getBlock() == x) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	protected int getMaxDistanceToAir() {
		return maxDistanceToAir;
	}

	public void setMaxDistanceToAir(int maxDistanceToAir) {
		this.maxDistanceToAir = maxDistanceToAir;
	}

	protected Block[] getValidSpawnBlocks() {
		return validSpawnBlocks;
	}

	public int getYOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	protected abstract void onGeneration(Random random, Level world, BlockPos pos, T worker);

	protected abstract void onGeneration(Random random, net.minecraft.world.level.Level world, BlockPos pos, WorldGenBuildingWorker worker);

	public abstract boolean isLocationValid(net.minecraft.world.level.Level world, BlockPos pos);

	public abstract boolean shouldGenerate(Random random, Level world, BlockPos pos);

	public boolean isLocationValid(Level world, BlockPos pos) {
		return locationIsValidSpawn(world, pos) && locationIsValidSpawn(world, pos.add(layerWidth, 0, 0))
				&& locationIsValidSpawn(world, pos.add(layerWidth, 0, layerHeight))
				&& locationIsValidSpawn(world, pos.add(0, 0, layerHeight));
	}

	@Override
	public void onGenerationWorkerCreated(T worker) {
		worker.setWorldGenBuilding(this);
		GenPositionWorldData data = MOWorldGen.getWorldPositionData(worker.getWorld());
		data.addPosition(getName(),
				new WorldPosition2D(worker.getPos().getX() + layerWidth / 2, worker.getPos().getZ() + layerHeight / 2));
	}

	public boolean isFarEnoughFromOthers(Level world, int x, int z, int minDistance) {
		GenPositionWorldData worldData = MOWorldGen.getWorldPositionData(world);
		if (worldData != null) {
			return worldData.isFarEnough(getName(), x, z, minDistance);
		}
		return true;
	}

	public abstract boolean shouldGenerate(Random random, net.minecraft.world.level.Level world, BlockPos pos);

	public abstract void onBlockPlace(net.minecraft.world.level.Level world, BlockState state, BlockPos pos, Random random, int color,
									  ImageGenWorker worker);

    public static class WorldGenBuildingWorker extends ImageGenWorker {
		MOWorldGenBuilding worldGenBuilding;

		public void setWorldGenBuilding(MOWorldGenBuilding worldGenBuilding) {
			this.worldGenBuilding = worldGenBuilding;
			this.worldGenBuilding.manageTextureLoading();
		}

		public boolean generate() {
			try {
				if (currentLayer >= worldGenBuilding.getLayerCount()) {
					worldGenBuilding.onGeneration(random, getWorld(), getPos(), this);
					return true;
				} else {
					worldGenBuilding.generate(random, getPos(), getWorld(), getChunkGenerator(), getChunkProvider(),
							currentLayer, getPlaceNotify(), this);
					currentLayer++;
					return false;
				}

			} catch (Exception e) {
				MOLog.log(Level.ERROR, e, "There was a problem while generating layer %s of %s", currentLayer,
						worldGenBuilding.getName());
			}
			return false;
		}
	}
}
