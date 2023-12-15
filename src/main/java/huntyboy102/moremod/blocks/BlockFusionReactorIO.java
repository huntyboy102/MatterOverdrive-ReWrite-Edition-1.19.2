
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.tile.TileEntityFusionReactorPart;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockFusionReactorIO extends MOBlockMachine<TileEntityFusionReactorPart> {
	public BlockFusionReactorIO(Material material, String name) {
		super(material, name);
		setHardness(30.0F);
		this.setResistance(10.0f);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	public Class<TileEntityFusionReactorPart> getTileEntityClass() {
		return TileEntityFusionReactorPart.class;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityFusionReactorPart();
	}

	/*
	 * @SideOnly(Side.CLIENT) public IIcon getIcon(int side, int meta) { return
	 * MatterOverdriveIcons.Network_port_square; }
	 */
}
