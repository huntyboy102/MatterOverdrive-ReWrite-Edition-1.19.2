
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.tile.TileEntityAndroidSpawner;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockAndroidSpawner extends MOBlockMachine<TileEntityAndroidSpawner> {
	public BlockAndroidSpawner(Material material, String name) {
		super(material, name);
		blockHardness = -1;
		setHasGui(true);
	}

	@Override
	public Class<TileEntityAndroidSpawner> getTileEntityClass() {
		return TileEntityAndroidSpawner.class;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityAndroidSpawner();
	}
}
