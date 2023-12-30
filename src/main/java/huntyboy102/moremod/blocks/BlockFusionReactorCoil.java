
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.api.wrench.IDismantleable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockFusionReactorCoil extends MOBlock implements IDismantleable {

	public BlockFusionReactorCoil(MaterialTritanium material, String name) {
		super(material, name);
		setHardness(30.0F);
		this.setResistance(10.0f);
		this.setHarvestLevel("pickaxe", 2);
	}

	/*
	 * @SideOnly(Side.CLIENT) public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	 * { return MatterOverdriveIcons.YellowStripes; }
	 */

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {
		if (!returnDrops) {
			IBlockState state = world.getBlockState(pos);
			world.setBlockToAir(pos);
			dropBlockAsItem(world, pos, state, 0);
		}

		return null;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
		return true;
	}

}
