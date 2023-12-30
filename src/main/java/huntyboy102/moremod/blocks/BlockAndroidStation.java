
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.tile.TileEntityAndroidStation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

public class BlockAndroidStation extends MOBlockMachine<TileEntityAndroidStation> {
	public BlockAndroidStation(MaterialTritanium material, String name) {
		super(material, name);
		setBoundingBox(new AABB(0, 0, 0, 1, 9 / 16d, 1));
		setHardness(20.0F);
		this.setResistance(9.0f);
		this.setHarvestLevel("pickaxe", 2);
		lightValue = 10;
		setHasGui(true);
	}

	/*
	 * @SideOnly(Side.CLIENT) public void registerBlockIcons(IIconRegister
	 * iconRegister) { super.registerBlockIcons(iconRegister); topIcon =
	 * iconRegister.registerIcon(Reference.MOD_ID + ":weapon_station_top");
	 * bottomIcon = iconRegister.registerIcon(Reference.MOD_ID +
	 * ":weapon_station_bottom"); blockIcon =
	 * iconRegister.registerIcon(Reference.MOD_ID + ":android_station_side"); }
	 * 
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public IIcon getIcon(int side, int meta) { if (side ==
	 * 1) { return topIcon; } else if (side == 0) { return bottomIcon; } else {
	 * return blockIcon; } }
	 */

	@Override
	protected String getUnlocalizedMessage(int type) {
		switch (type) {
		case 0:
			return "alert.not_android";
		default:
			return getUnlocalizedMessage(type);
		}
	}

	@Override
	public Class<TileEntityAndroidStation> getTileEntityClass() {
		return TileEntityAndroidStation.class;
	}

	@Nonnull
	@Override
	public BlockEntity createTileEntity(@Nonnull LevelAccessor world, @Nonnull BlockState state) {
		return new TileEntityAndroidStation();
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}
}
