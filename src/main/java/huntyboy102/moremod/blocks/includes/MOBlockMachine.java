
package huntyboy102.moremod.blocks.includes;

import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.api.IMOTileEntity;
import huntyboy102.moremod.api.wrench.IDismantleable;
import huntyboy102.moremod.data.inventory.Slot;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.util.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.LevelAccessor;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public abstract class MOBlockMachine<TE extends BlockEntity> extends MOBlockContainer<TE>
		implements IDismantleable, IConfigSubscriber {
	public float volume = 1;
	public boolean hasGui;

	public MOBlockMachine(Material material, String name) {
		super(material, name);
	}

	@Nonnull
	@Override
	protected BlockBehaviour createBlockState() {
		return super.createBlockState();
	}

	public boolean doNormalDrops(LevelAccessor world, int x, int y, int z) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(LevelAccessor worldIn, BlockPos pos, BlockState state, Player placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		IMOTileEntity entity = (IMOTileEntity) worldIn.getBlockEntity(pos);
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
	public void breakBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		// drops inventory
		CustomInventory customInventory = getInventory(worldIn, pos);
		if (customInventory != null) {
			MatterHelper.DropInventory(worldIn, customInventory, pos);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(LevelAccessor worldIn, BlockPos pos, BlockState state, Player playerIn,
									InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ) {
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
	public boolean removedByPlayer(BlockState state, LevelAccessor world, BlockPos pos, Player player,
			boolean willHarvest) {
		if (MachineHelper.canRemoveMachine(world, player, pos, willHarvest)) {
			return world.setBlockToAir(pos);
		}
		return false;
	}

	public ItemStack getNBTDrop(LevelAccessor world, BlockPos blockPos, IMOTileEntity te) {
		BlockState state = world.getBlockState(blockPos);
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
	public ArrayList<ItemStack> dismantleBlock(Player player, LevelAccessor world, BlockPos pos, boolean returnDrops) {
		ArrayList<ItemStack> items = new ArrayList<>();
		ItemStack blockItem = getNBTDrop(world, pos, (IMOTileEntity) world.getBlockEntity(pos));
		CustomInventory customInventory = getInventory(world, pos);
		items.add(blockItem);

		// remove any items from the machine inventory so that breakBlock doesn't
		// duplicate the items
		if (customInventory != null) {
			for (int i1 = 0; i1 < customInventory.getSizeInventory(); ++i1) {
				Slot slot = customInventory.getSlot(i1);
				ItemStack itemstack = slot.getItem();

				if (!itemstack.isEmpty()) {
					if (slot.keepOnDismantle()) {
						slot.setItem(ItemStack.EMPTY);
					}
				}
			}
		}

		BlockState blockState = world.getBlockState(pos);
		boolean flag = blockState.getBlock().removedByPlayer(blockState, world, pos, player, true);
		super.breakBlock(world, pos, blockState);

		if (flag) {
			blockState.getBlock().onPlayerDestroy(world, pos, blockState);
		}

		if (!returnDrops) {
			dropBlockAsItem(world, pos, blockState, 0);
		} else {
			MOInventoryHelper.insertItemStackIntoInventory(player.inventory, blockItem, Direction.DOWN);
		}

		return items;
	}

	protected CustomInventory getInventory(LevelAccessor world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof MOTileEntityMachine) {
			MOTileEntityMachine machine = (MOTileEntityMachine) world.getTileEntity(pos);
			return machine.getInventoryContainer();
		}
		return null;
	}

	@Override
	public boolean canDismantle(Player player, LevelAccessor world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof MOTileEntityMachine) {
			if (player.capabilities.isCreativeMode || !((MOTileEntityMachine) tileEntity).hasOwner()) {
				return true;
			} else {
				if (((MOTileEntityMachine) tileEntity).getOwner().equals(player.getGameProfile().getId())) {
					return true;
				} else {
					if (world.isRemote) {
						Component message = new Component(
								ChatFormatting.GOLD + "[Matter Overdrive] " + ChatFormatting.RED
										+ MOStringHelper.translateToLocal("alert.no_rights.dismantle").replace("$0",
												getLocalizedName()));
						message.setStyle(new Style().setColor(ChatFormatting.RED));
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
