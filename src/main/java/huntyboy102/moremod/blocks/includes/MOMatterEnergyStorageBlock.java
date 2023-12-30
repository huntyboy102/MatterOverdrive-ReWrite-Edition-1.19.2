
package huntyboy102.moremod.blocks.includes;

import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public abstract class MOMatterEnergyStorageBlock<TE extends BlockEntity> extends MOBlockMachine<TE> {
	protected boolean dropsItself;
	private boolean keepsMatter;
	private boolean keepsEnergy;

	public MOMatterEnergyStorageBlock(Material material, String name, boolean keepsEnergy, boolean keepsMatter) {
		super(material, name);
		this.keepsEnergy = keepsEnergy;
		this.keepsMatter = keepsMatter;
	}

	@Override
	public void onBlockPlacedBy(LevelAccessor worldIn, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (stack.hasTag()) {
			BlockEntity entity = worldIn.getBlockEntity(pos);

			if (entity != null && entity.hasCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null)) {
				if (this.keepsMatter) {
					int matterStored = stack.getTag("Matter");
					entity.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null)
							.setMatterStored(matterStored);
				}
			}
		}
	}

	/*
	 * @Override public boolean removedByPlayer(World world, EntityPlayer player,
	 * int x, int y, int z) { if(dropsItself) { MOTileEntityMachineMatter tile =
	 * (MOTileEntityMachineMatter)world.getTileEntity(x,y,z);
	 * 
	 * if (tile != null && !world.isRemote && !world.restoringBlockSnapshots) // do
	 * not drop items while restoring blockstates, prevents item dupe { ItemStack
	 * item = new ItemStack(this);
	 * 
	 * if(tile.getMatterStored() > 0 && this.keepsMatter) {
	 * if(!item.hasTagCompound()) item.setTagCompound(new NBTTagCompound());
	 * 
	 * item.getTagCompound().setInteger("Matter", tile.getMatterStored()); }
	 * if(tile.getEnergyStored(EnumFacing.DOWN) > 0 && this.keepsEnergy) {
	 * if(!item.hasTagCompound()) item.setTagCompound(new NBTTagCompound());
	 * 
	 * item.getTagCompound().setInteger("Energy",
	 * tile.getEnergyStored(EnumFacing.DOWN));
	 * item.getTagCompound().setInteger("MaxEnergy",
	 * tile.getMaxEnergyStored(EnumFacing.DOWN)); }
	 * 
	 * this.dropBlockAsItem(world, x, y, z, item); } } return
	 * super.removedByPlayer(world,player,x,y,z); }
	 */

}
