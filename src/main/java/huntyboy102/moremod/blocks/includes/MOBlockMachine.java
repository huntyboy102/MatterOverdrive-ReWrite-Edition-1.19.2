
package huntyboy102.moremod.blocks.includes;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.api.IMOTileEntity;
import huntyboy102.moremod.api.wrench.IDismantleable;
import huntyboy102.moremod.data.Inventory;
import huntyboy102.moremod.data.inventory.Slot;
import huntyboy102.moremod.handler.ConfigurationHandler;
import matteroverdrive.util.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public abstract class MOBlockMachine<TE extends TileEntity> extends MOBlockContainer<TE>
		implements IDismantleable, IConfigSubscriber {
	public float volume = 1;
	public boolean hasGui;

	public MOBlockMachine(Material material, String name) {
		super(material, name);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return super.createBlockState();
	}

	public boolean doNormalDrops(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		IMOTileEntity entity = (IMOTileEntity) worldIn.getTileEntity(pos);
		if (entity != null) {
			try {
				entity.readFromPlaceItem(stack);
			} catch (Exception e) {
				e.printStackTrace();
				MOLog.log(Level.ERROR, "Could not load settings from placing item", e);
			}

			entity.onPlaced(worldIn, placer);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		// drops inventory
		Inventory inventory = getInventory(worldIn, pos);
		if (inventory != null) {
			MatterHelper.DropInventory(worldIn, inventory, pos);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return MachineHelper.canOpenMachine(worldIn, pos, playerIn, hasGui, getUnlocalizedMessage(0));
	}

	protected String getUnlocalizedMessage(int type) {
		switch (type) {
		case 0:
			return "alert.no_rights";
		default:
			return "alert.no_access_default";
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player,
			boolean willHarvest) {
		if (MachineHelper.canRemoveMachine(world, player, pos, willHarvest)) {
			return world.setBlockToAir(pos);
		}
		return false;
	}

	public ItemStack getNBTDrop(World world, BlockPos blockPos, IMOTileEntity te) {
		IBlockState state = world.getBlockState(blockPos);
		ItemStack itemStack = new ItemStack(this, 1, damageDropped(state));
		if (te != null) {
			te.writeToDropItem(itemStack);
		}
		return itemStack;
	}

	public boolean hasGui() {
		return hasGui;
	}

	public void setHasGui(boolean hasGui) {
		this.hasGui = hasGui;
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {
		ArrayList<ItemStack> items = new ArrayList<>();
		ItemStack blockItem = getNBTDrop(world, pos, (IMOTileEntity) world.getTileEntity(pos));
		Inventory inventory = getInventory(world, pos);
		items.add(blockItem);

		// remove any items from the machine inventory so that breakBlock doesn't
		// duplicate the items
		if (inventory != null) {
			for (int i1 = 0; i1 < inventory.getSizeInventory(); ++i1) {
				Slot slot = inventory.getSlot(i1);
				ItemStack itemstack = slot.getItem();

				if (!itemstack.isEmpty()) {
					if (slot.keepOnDismantle()) {
						slot.setItem(ItemStack.EMPTY);
					}
				}
			}
		}

		IBlockState blockState = world.getBlockState(pos);
		boolean flag = blockState.getBlock().removedByPlayer(blockState, world, pos, player, true);
		super.breakBlock(world, pos, blockState);

		if (flag) {
			blockState.getBlock().onPlayerDestroy(world, pos, blockState);
		}

		if (!returnDrops) {
			dropBlockAsItem(world, pos, blockState, 0);
		} else {
			MOInventoryHelper.insertItemStackIntoInventory(player.inventory, blockItem, EnumFacing.DOWN);
		}

		return items;
	}

	protected Inventory getInventory(World world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof MOTileEntityMachine) {
			MOTileEntityMachine machine = (MOTileEntityMachine) world.getTileEntity(pos);
			return machine.getInventoryContainer();
		}
		return null;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof MOTileEntityMachine) {
			if (player.capabilities.isCreativeMode || !((MOTileEntityMachine) tileEntity).hasOwner()) {
				return true;
			} else {
				if (((MOTileEntityMachine) tileEntity).getOwner().equals(player.getGameProfile().getId())) {
					return true;
				} else {
					if (world.isRemote) {
						TextComponentString message = new TextComponentString(
								TextFormatting.GOLD + "[Matter Overdrive] " + TextFormatting.RED
										+ MOStringHelper.translateToLocal("alert.no_rights.dismantle").replace("$0",
												getLocalizedName()));
						message.setStyle(new Style().setColor(TextFormatting.RED));
						player.sendMessage(message);
					}
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		config.initMachineCategory(getTranslationKey());
		volume = (float) config.getMachineDouble(getTranslationKey(), "volume", 1, "The volume of the Machine");
	}
}
