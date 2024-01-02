package huntyboy102.moremod.tile;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import huntyboy102.moremod.data.TileEntityCustomInventory;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.data.inventory.CrateSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.Nameable;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;

public class TileEntityNewTritaniumCrate extends MOTileEntity implements Container, Nameable, Clearable {
	final TileEntityCustomInventory inventory;

	private int color;

	public TileEntityNewTritaniumCrate() {
		inventory = new TileEntityCustomInventory(this, MOStringHelper.translateToLocal("container.new_tritanium_crate"));

		for (int i = 0; i < 54; i++) {
			CrateSlot slot = new CrateSlot(false);
			inventory.AddSlot(slot);
		}
	}

	public int getInventorySize() {
		return 54;
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		if (categories.contains(MachineNBTCategory.INVENTORY) && toDisk) {
			inventory.writeToNBT(nbt, true);
		}

		if (categories.contains(MachineNBTCategory.COLOR) && toDisk) {
			nbt.putInt("Color", color);
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.COLOR)) {
			color = nbt.putInt("Color");

			MOLog.info("Setting custom color to: " + color);
		}

		if (categories.contains(MachineNBTCategory.INVENTORY)) {
			inventory.readFromNBT(nbt);
		}
	}

	@Override
	protected void onAwake(Dist side) {

	}

	@Override
	public void onAdded(Level world, BlockPos pos, BlockState state) {

	}

	@Override
	public void onPlaced(Level world, LivingEntity entityLiving) {

	}

	@Override
	public void onDestroyed(Level worldIn, BlockPos pos, BlockState state) {

	}

	@Override
	public void onNeighborBlockChange(LevelAccessor world, BlockPos pos, BlockState state, Block neighborBlock) {

	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {
		if (!itemStack.hasTag()) {
			itemStack.setTag(new CompoundTag());
		}

		inventory.writeToNBT(itemStack.getTag(), true);
	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {
		if (itemStack.hasTag()) {
			inventory.readFromNBT(itemStack.getTag());
		}
	}

	public TileEntityCustomInventory getInventory() {
		return inventory;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public String getName() {
		return inventory.getName();
	}

	@Override
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}

	@Override
	public Component getDisplayName() {
		return this.getDisplayName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(Player p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory(Player entityPlayer) {
		inventory.openInventory(entityPlayer);
	}

	@Override
	public void closeInventory(Player entityPlayer) {
		inventory.closeInventory(entityPlayer);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public AbstractContainerMenu createContainer(int windowId, Inventory inv, Player player) {
		return ChestMenu.threeRows(windowId, inv, this);
	}

	@Override
	public String getGuiID() {
		return "minecraft:chest";
	}

	public void readInv(CompoundTag nbt) {
		ListTag invList = nbt.getList("inventory", 10);

		for (int i = 0; i < invList.size(); i++) {
			CompoundTag itemTag = invList.getCompound(i);

			int slot = itemTag.getByte("Slot");

			if (slot >= 0 && slot < inventory.getSlots().size()) {
				inventory.setInventorySlotContents(slot, new ItemStack(itemTag));
			}
		}
	}

	public void writeInv(CompoundTag nbt, Player player) {
		boolean write = false;

		ListTag invList = new ListTag();

		for (int i = 0; i < inventory.getSlots().size(); i++) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				continue;
			}

			CompoundTag itemTag = new CompoundTag();

			itemTag.putByte("Slot", (byte) i);

			player.sendSystemMessage(
					new Component("Writing out item: " + inventory.getStackInSlot(i)));

			inventory.getStackInSlot(i).writeToNBT(itemTag);

			invList.addTag(itemTag);
		}

		nbt.put("inventory", invList);
	}

	public int getColor() {
		return this.color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
