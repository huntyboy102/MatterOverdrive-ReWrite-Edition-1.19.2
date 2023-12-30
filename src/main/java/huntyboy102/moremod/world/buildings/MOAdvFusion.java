package huntyboy102.moremod.world.buildings;

import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.world.MOImageGen;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

public class MOAdvFusion extends MOWorldGenBuilding {
	private static final int MIN_DISTANCE_APART = 256;

	public MOAdvFusion(String name) {
		super(name, new ResourceLocation(Reference.PATH_WORLD_TEXTURES + "advfusion.png"), 24, 24);
		setyOffset(5);
		addMapping(0x00fffc, MatterOverdriveRewriteEdition.BLOCKS.decomposer);
		addMapping(0xffa200, MatterOverdriveRewriteEdition.BLOCKS.machine_hull);
		addMapping(0xfff600, MatterOverdriveRewriteEdition.BLOCKS.fusion_reactor_controller);
		addMapping(0xaccb00, MatterOverdriveRewriteEdition.BLOCKS.fusion_reactor_controller);
		addMapping(0x80b956, MatterOverdriveRewriteEdition.BLOCKS.fusion_reactor_coil);
		// addMapping(0xec1c24, Blocks.AIR);
		addMapping(0xe400ff, MatterOverdriveRewriteEdition.BLOCKS.gravitational_anomaly);
	}

	@Override
	protected void onGeneration(Random random, Level world, BlockPos pos, WorldGenBuildingWorker worker) {
	}

	@Override
	public int getMetaFromColor(int color, Random random) {
		return 255 - getAlphaFromColor(color);
	}

	@Override
	public boolean isLocationValid(Level world, BlockPos pos) {
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
	public boolean shouldGenerate(Random random, Level world, BlockPos pos) {
		return false;
	}

	@Override
	public WorldGenBuildingWorker getNewWorkerInstance() {
		return new WorldGenBuildingWorker();
	}

	@Override
	public void onBlockPlace(Level world, BlockState state, BlockPos pos, Random random, int color,
			MOImageGen.ImageGenWorker worker) {
		if (state.getBlock() == MatterOverdriveRewriteEdition.BLOCKS.fusion_reactor_controller) {
			if (colorsMatch(color, 0xfff600)) {
				world.setBlock(pos, state.setValue(MOBlock.PROPERTY_DIRECTION, Direction.NORTH), 3);
			} else if (colorsMatch(color, 0xaccb00)) {
				world.setBlock(pos, state.setValue(MOBlock.PROPERTY_DIRECTION, Direction.UP), 3);
			}
		}
	}
}