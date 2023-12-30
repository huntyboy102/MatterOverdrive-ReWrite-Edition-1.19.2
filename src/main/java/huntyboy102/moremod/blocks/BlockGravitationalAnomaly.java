
package huntyboy102.moremod.blocks;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import huntyboy102.moremod.blocks.includes.MOBlockContainer;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.api.IScannable;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.tile.TileEntityGravitationalAnomaly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGravitationalAnomaly extends MOBlockContainer<TileEntityGravitationalAnomaly>
		implements IScannable, IConfigSubscriber {
	public BlockGravitationalAnomaly(Material material, String name) {
		super(material, name);
		setBoundingBox(new AxisAlignedBB(0.3f, 0.3f, 0.3f, 0.6f, 0.6f, 0.6f));
		setBlockUnbreakable();
		setResistance(6000000.0F);
		disableStats();
	}

	@Override
	@Deprecated
	public boolean isNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	@Deprecated
	public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos,
			@Nonnull Vec3d start, @Nonnull Vec3d end) {
		return super.collisionRayTrace(state, world, pos, start, end);
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntityGravitationalAnomaly tileEntity = getTileEntity(source, pos);
		if (tileEntity != null) {
			double range = tileEntity.getEventHorizon();
			range = Math.max(range, 0.4);
			float rangeMin = (float) (0.5 - (range / 2));
			float rangeMax = (float) (0.5 + (range / 2));
			return new AxisAlignedBB(rangeMin, rangeMin, rangeMin, rangeMax, rangeMax, rangeMax);
		}
		return super.getBoundingBox(state, source, pos);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return Block.NULL_AABB;
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public Class<TileEntityGravitationalAnomaly> getTileEntityClass() {
		return TileEntityGravitationalAnomaly.class;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityGravitationalAnomaly();
	}

	@Override
	public void addInfo(World world, double x, double y, double z, List<String> infos) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		if (tileEntity != null && tileEntity instanceof TileEntityGravitationalAnomaly) {
			((TileEntityGravitationalAnomaly) tileEntity).addInfo(world, x, y, z, infos);
		}
	}

	@Override
	public void onScan(World world, double x, double y, double z, EntityPlayer player, ItemStack scanner) {

	}

	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		return false;
	}

	@Nonnull
	@Override
	@Deprecated
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		TileEntityGravitationalAnomaly.BLOCK_ENTETIES = config.getBool(
				ConfigurationHandler.KEY_GRAVITATIONAL_ANOMALY_BLOCK_ENTITIES,
				ConfigurationHandler.CATEGORY_SERVER + "." + getTranslationKey().substring(5), true,
				"Should the blocks drop entities or be directly consumed when destroyed by the gravitational anomaly");
		TileEntityGravitationalAnomaly.FALLING_BLOCKS = config.getBool(
				ConfigurationHandler.KEY_GRAVITATIONAL_ANOMALY_FALLING_BLOCKS,
				ConfigurationHandler.CATEGORY_SERVER + "." + getTranslationKey().substring(5), true,
				"Should blocks be turned into falling blocks when broken");
		TileEntityGravitationalAnomaly.VANILLA_FLUIDS = config.getBool(
				ConfigurationHandler.KEY_GRAVITATIONAL_ANOMALY_VANILLA_FLUIDS,
				ConfigurationHandler.CATEGORY_SERVER + "." + getTranslationKey().substring(5), true,
				"Should vanilla fluid block such as water and lava be consumed by the anomaly");
		TileEntityGravitationalAnomaly.FORGE_FLUIDS = config.getBool(
				ConfigurationHandler.KEY_GRAVITATIONAL_ANOMALY_FORGE_FLUIDS,
				ConfigurationHandler.CATEGORY_SERVER + "." + getTranslationKey().substring(5), true,
				"Should other mod fluid blocks be consumed by the anomaly");
		TileEntityGravitationalAnomaly.BLOCK_DESTRUCTION = config.getBool("block destruction",
				ConfigurationHandler.CATEGORY_SERVER + "." + getTranslationKey().substring(5), true,
				"Should the gravitational anomaly destroy blocks");
		TileEntityGravitationalAnomaly.GRAVITATION = config.getBool("gravitational pull",
				ConfigurationHandler.CATEGORY_SERVER + "." + getTranslationKey().substring(5), true,
				"Should the gravitational entity pull entities towards it");
	}
}
