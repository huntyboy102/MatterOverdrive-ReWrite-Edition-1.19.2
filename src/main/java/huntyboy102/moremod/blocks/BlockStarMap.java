
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockStarMap extends MOBlockMachine<TileEntityMachineStarMap> {
	public BlockStarMap(Material material, String name) {
		super(material, name);
		setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, 9 / 16d, 1));
		setHardness(20.0F);
		this.setResistance(9.0f);
		this.setHarvestLevel("pickaxe", 2);
		setHasGui(true);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			TileEntityMachineStarMap starMap = (TileEntityMachineStarMap) worldIn.getTileEntity(pos);
			starMap.zoom();
			return true;
		} else {
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}
	}

	@Override
	public Class<TileEntityMachineStarMap> getTileEntityClass() {
		return TileEntityMachineStarMap.class;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityMachineStarMap();
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}
