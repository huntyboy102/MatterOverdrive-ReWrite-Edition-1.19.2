
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.tile.TileEntityAndroidSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nonnull;

public class BlockAndroidSpawner extends MOBlockMachine<TileEntityAndroidSpawner> {
	public BlockAndroidSpawner(MaterialTritanium material, String name) {
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
	public BlockEntity createTileEntity(@Nonnull LevelAccessor world, @Nonnull BlockState state) {
		return new TileEntityAndroidSpawner();
	}
}
