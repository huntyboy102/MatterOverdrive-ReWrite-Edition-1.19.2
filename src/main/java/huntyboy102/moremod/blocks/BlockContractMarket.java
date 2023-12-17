
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.tile.TileEntityMachineContractMarket;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

public class BlockContractMarket extends BlockMonitor<TileEntityMachineContractMarket> {
	public BlockContractMarket(Material material, String name) {
		super(material, name);
		setHardness(20.0F);
		this.setResistance(9.0f);
		this.setHarvestLevel("pickaxe", 2);
		setBoundingBox(new AABB(0, 1, 0, 1, 11 / 16d, 1));
		setHasGui(true);
	}

	@Override
	public Class<TileEntityMachineContractMarket> getTileEntityClass() {
		return TileEntityMachineContractMarket.class;
	}

	@Nonnull
	@Override
	public BlockEntity createTileEntity(@Nonnull LevelAccessor world, @Nonnull BlockState state) {
		return new TileEntityMachineContractMarket();
	}
}
