
package huntyboy102.moremod.data;

import javax.annotation.Nonnull;

import huntyboy102.moremod.util.MOInventoryHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ItemInventoryWrapper implements Container {
	ItemStack inventory;
	int size;
	boolean dirty;

	public ItemInventoryWrapper(ItemStack itemStack, int size) {
		this.inventory = itemStack;
		this.size = size;
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return MOInventoryHelper.getStackInSlot(inventory, slot);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amount) {
		return MOInventoryHelper.decrStackSize(inventory, slot, amount);
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = MOInventoryHelper.getStackInSlot(inventory, index);
		MOInventoryHelper.setInventorySlotContents(inventory, index, ItemStack.EMPTY);
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (inventory.getTag() == null) {
			inventory.setTag(new CompoundTag());
		}

		MOInventoryHelper.setInventorySlotContents(inventory, slot, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public boolean isUsableByPlayer(Player player) {
		return true;
	}

	@Override
	public void openInventory(Player player) {

	}

	@Override
	public void closeInventory(Player player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < getSizeInventory(); i++) {
			removeStackFromSlot(i);
		}
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public String getName() {
		return inventory.getDescriptionId();
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public Component getDisplayName() {
		return inventory.getHoverName();
	}
}
