
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.util.MOBlockHelper;
import huntyboy102.moremod.tile.TileEntityMachineNetworkRouter;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockNetworkRouter extends MOBlockMachine<TileEntityMachineNetworkRouter> {

	public BlockNetworkRouter(MaterialTritanium material, String name) {
		super(material, name);
		setHardness(20.0F);
		this.setResistance(9.0f);
		this.setHarvestLevel("pickaxe", 2);
		setHasGui(true);
		setRotationType(MOBlockHelper.RotationType.PREVENT);
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public IIcon getIcon(IBlockAccess world, int x, int y,
	 * int z, int meta) { TileEntity entity = world.getTileEntity(x,y,z); if (entity
	 * instanceof TileEntityMachineNetworkRouter) { if
	 * (((TileEntityMachineNetworkRouter) entity).isActive()) { return activeIcon; }
	 * } return blockIcon; }
	 */

	@Override
	public Class<TileEntityMachineNetworkRouter> getTileEntityClass() {
		return TileEntityMachineNetworkRouter.class;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityMachineNetworkRouter();
	}
}
