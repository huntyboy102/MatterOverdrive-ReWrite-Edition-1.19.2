
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.tile.pipes.TileEntityHeavyMatterPipe;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockHeavyMatterPipe extends BlockMatterPipe {

	public BlockHeavyMatterPipe(MaterialTritanium material, String name) {
		super(material, name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getTileEntityClass() {
		return TileEntityHeavyMatterPipe.class;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityHeavyMatterPipe();
	}

}
