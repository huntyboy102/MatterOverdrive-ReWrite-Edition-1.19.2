
package huntyboy102.moremod.data;

import huntyboy102.moremod.data.inventory.Slot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomInventory extends Inventory {
	final NonNullList<Slot> slots;
	String name;
	IUsableCondition usableCondition;

	public CustomInventory(String name) {
		this(name, new ArrayList<>());
	}

	public CustomInventory(String name, Collection<Slot> slots) {
		this(name, slots, null);
	}

	public CustomInventory(String name, Collection<Slot> slots, IUsableCondition usableCondition) {
		this.slots = NonNullList.create();
		this.slots.addAll(slots);
		this.name = name;
		this.usableCondition = usableCondition;
	}

	@Override
	public boolean isEmpty() {
		return slots.isEmpty();
	}

	public int AddSlot(Slot slot) {
		if (slots.add(slot)) {
			slot.setId(slots.size() - 1);
			return slots.size() - 1;
		}
		return 0;
	}

	public void setUsableCondition(IUsableCondition condition) {
		this.usableCondition = condition;
	}

	public void readFromNBT(CompoundTag compound) {
		ListTag nbttaglist = compound.getList("Items", 10);
		for (int i = 0; i < nbttaglist.size(); ++i) {
			CompoundTag nbttagcompound1 = nbttaglist.getCompound(i);
			byte b0 = nbttagcompound1.getByte("Slot");
			if (nbttagcompound1.hasUUID("id")) {
				setInventorySlotContents(b0, new ItemStack(nbttagcompound1));
			} else {
				setInventorySlotContents(b0, ItemStack.EMPTY);
			}
		}
	}

	public void writeToNBT(CompoundTag compound, boolean toDisk) {
		ListTag nbttaglist = new ListTag();

		for (int i = 0; i < getSizeInventory(); ++i) {
			writeSlotToNBT(nbttaglist, i, toDisk);
		}

		if (nbttaglist.size() > 0) {
			compound.put("Items", nbttaglist);
		}
	}

	protected void writeSlotToNBT(ListTag nbttaglist, int slotId, boolean toDisk) {
		Slot slot = getSlot(slotId);
		if (slot != null) {
			if (toDisk && !slot.getItem().isEmpty()) {
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte) slotId);
				if (!slot.getItem().isEmpty()) {
					slot.getItem().save(nbttagcompound1);
				}
				nbttaglist.add(nbttagcompound1);
			} else if (!toDisk && slot.sendsToClient()) {
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte) slotId);
				if (!slot.getItem().isEmpty()) {
					slot.getItem().save(nbttagcompound1);
				}
				nbttaglist.add(nbttagcompound1);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return slots.size();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return slots.get(slot).getItem();
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slotId, int size) {
		Slot slot = getSlot(slotId);
		if (slot != null && !slot.getItem().isEmpty()) {
			ItemStack itemstack;

			if (slot.getItem().getCount() <= size) {
				itemstack = slot.getItem();
				slot.setItem(ItemStack.EMPTY);

				return itemstack;
			} else {
				itemstack = slot.getItem().split(size);

				if (slot.getItem().getCount() == 0) {
					slot.setItem(ItemStack.EMPTY);
				}

				return itemstack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = getSlot(index).getItem();
		getSlot(index).setItem(ItemStack.EMPTY);
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		getSlot(slot).setItem(item);

		if (!item.isEmpty() && item.getCount() > this.getInventoryStackLimit()) {
			item.setCount(this.getInventoryStackLimit());
		}
	}

	public void addItem(ItemStack itemStack) {
		for (int i = 0; i < slots.size(); i++) {
			Slot slot = getSlot(i);
			if (slot.isValidForSlot(itemStack)) {
				if (slot.getItem().isEmpty()) {
					slot.setItem(itemStack);
					return;
				} else if (ItemStack.isSame(slot.getItem(), itemStack)
						&& slot.getItem().getCount() < slot.getItem().getMaxStackSize()) {
					int newStackSize = Math.min(slot.getItem().getCount() + itemStack.getCount(),
							slot.getItem().getMaxStackSize());
					int leftStackSize = slot.getItem().getCount() + itemStack.getCount() - newStackSize;
					slot.getItem().setCount(newStackSize);
					if (leftStackSize <= 0) {
						return;
					}

					itemStack.setCount(newStackSize);
				}
			}
		}
	}

	public void clearItems() {
		for (Slot slot : slots) {
			slot.setItem(ItemStack.EMPTY);
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean hasCustomName() {
		return name != null && !name.isEmpty();
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.name);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(Player player) {
		return true;

	}

	@Override
	public void openInventory(Player entityPlayer) {

	}

	@Override
	public void closeInventory(Player entityPlayer) {

	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack item) {
		if (slotID >= 0 && slotID < getSizeInventory() && getSlot(slotID) != null) {
			Slot slot = getSlot(slotID);
			if (!slot.getItem().isEmpty()) {
				return slot.getItem().getCount() <= slot.getMaxStackSize() && slot.isValidForSlot(item);
			}
			return slot.isValidForSlot(item);
		}
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
		for (Slot slot : slots) {
			slot.setItem(ItemStack.EMPTY);
		}
	}

	public Slot getSlot(int slotID) {
		return slots.get(slotID);
	}

	public int getLastSlotId() {
		return slots.size() - 1;
	}

	public List<Slot> getSlots() {
		return slots;
	}
}
