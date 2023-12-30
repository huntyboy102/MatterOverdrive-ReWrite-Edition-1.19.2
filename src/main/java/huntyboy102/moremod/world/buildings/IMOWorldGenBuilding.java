
package huntyboy102.moremod.world.buildings;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.Random;

public interface IMOWorldGenBuilding<T extends MOWorldGenBuilding.ImageGenWorker> {
	String getName();

	void generate(Random random, BlockPos pos, Level world, ChunkGenerator chunkGenerator,
				  BiomeSource chunkProvider, int layer, int placeNotify, T worker);
}
