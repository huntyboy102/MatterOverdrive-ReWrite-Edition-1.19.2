
package huntyboy102.moremod.util;

import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MOInventoryHelper {

	public static void setInventorySlotContents(@Nonnull ItemStack container, int slot, @Nonnull ItemStack stack) {
		if (!stack.isEmpty()) {
			container.getOrCreateTag().put("Slot" + slot, new CompoundTag());
		} else {
			CompoundTag itemTag = stack.save(new CompoundTag());
			container.getOrCreateTag().put("Slot" + slot, itemTag);
		}
	}

	@Nonnull
	public static ItemStack decrStackSize(ItemStack container, int slot, int amount) {
		CompoundTag slotTag = container.getTag().getCompound("Slot" +slot);

		if (slotTag.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = ItemStack.of(slotTag);
		int stackSize = Math.min(stack.getCount(), amount);

		ItemStack retStack = stack.copy();
		retStack.setCount(stackSize);

		stack.shrink(stackSize);

		if (stack.isEmpty()) {
			container.getTag().remove("Slot" + slot);
		} else {
			CompoundTag itemTag = stack.save(new CompoundTag());
			container.getTag().put("Slot" + slot, itemTag);
		}

		return retStack;
	}

	@Nonnull
	public static ItemStack getStackInSlot(ItemStack container, int slot) {
		if (!container.hasTag() || container.getTag().getCompound("Slot" + slot).isEmpty()) {
			return ItemStack.EMPTY;
		}
		CompoundTag itemTag = container.getTag().getCompound("Slot" + slot);
		ItemStack stack = ItemStack.of(itemTag);
		return stack;
	}

	public static List<ItemStack> getStacks(ItemStack container) {
		if (!container.hasTag()) {
			return Collections.emptyList();
		}

		List<ItemStack> itemStacks = new ArrayList<>();

		for (String s : container.getTag().getAllKeys()) {
			if (s.startsWith("Slot")) {
				Tag nbtTag = container.getTag().get(s);
				if (nbtTag instanceof CompoundTag compoundTag) {
                    ItemStack stack = ItemStack.of(compoundTag);
					if (!stack.isEmpty()) {
						itemStacks.add(stack);
					}
				}
			}
		}
		return itemStacks;
	}

	public static ItemStack addItemInContainer(InventoryMenu container, ItemStack itemStack) {
		for (Slot slot : container.slots) {
			if (slot.mayPlace(itemStack)) {
				if (slot.hasItem()) {
					if (ItemStack.isSame(slot.getItem(), itemStack) && ItemStack.tagMatches(slot.getItem(), itemStack)) {
						int newStackSize = Math.min(slot.getItem().getCount() + itemStack.getCount(), slot.getMaxStackSize());
						int leftStackSize = slot.getItem().getCount() + itemStack.getCount() - newStackSize;
						slot.getItem().setCount(newStackSize);

						if (leftStackSize <= 0) {
							return ItemStack.EMPTY;
						}

						itemStack.setCount(itemStack.getMaxStackSize());
					}
				} else {
					int maxStackSize = Math.min(itemStack.getMaxStackSize(), slot.getMaxStackSize());
					ItemStack stackToAdd = itemStack.copy();
					stackToAdd.setCount(Math.min(itemStack.getCount(), maxStackSize));
					slot.set(stackToAdd);

					if (itemStack.getCount() > maxStackSize){
						itemStack.shrink(maxStackSize);
					} else {
						return ItemStack.EMPTY;
					}
				}
			}
		}
		return itemStack;
	}

	public static ItemStack insertItemStackIntoInventory(MenuProvider inventoryProvider, ItemStack itemstack, Direction side) {
		if (itemstack != null && inventoryProvider != null) {
			int originalCount = itemstack.getCount();
			Inventory inv = (Inventory) inventoryProvider;

            for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                if (inv.canPlaceItem(slot, itemstack)) {
                    ItemStack stackInSlot = inv.getItem(slot);

                    if (ItemStack.isSame(itemstack, stackInSlot)) {
                        itemstack = addToOccupiedInventorySlot(inventoryProvider, slot, itemstack, stackInSlot);
                    }
                }
            }

            for (int slot = 0; slot < inv.getContainerSize(); slot++) {
                if (inv.getItem(slot).isEmpty() && inv.canPlaceItem(slot, itemstack)) {
                    itemstack = addToEmptyInventorySlot(inventoryProvider, slot, itemstack);
                }
            }

            if (itemstack == null || itemstack.getCount() != originalCount) {
				inv.setChanged();
			}

			return itemstack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	public static ItemStack addToOccupiedInventorySlot(MenuProvider inventoryProvider, int slotIndex, ItemStack one, ItemStack two) {
		if (!(inventoryProvider instanceof InventoryMenu)) {
			return one; // Return early if the provider is not an InventoryMenu
		}

		InventoryMenu inventory = ((InventoryMenu) inventoryProvider);

		if (slotIndex < 0 || slotIndex >= inventory.slots.size()) {
			return one;
		}

		Slot targetSlot = inventory.slots.get(slotIndex);

		int maxSize = Math.min(targetSlot.getMaxStackSize(), one.getMaxStackSize());
		if (one.getCount() + two.getCount() > maxSize) {
			int remanningSize = maxSize - two.getCount();
			two.setCount(maxSize);
			one.shrink(remanningSize);
			targetSlot.set(two);
			return one;
		} else {
			two.grow(Math.min(one.getCount(), maxSize));
			targetSlot.set(two);
			return maxSize >= one.getCount() ? ItemStack.EMPTY : one.split(one.getCount() - maxSize);
		}
	}

	public static ItemStack addToEmptyInventorySlot(MenuProvider inventoryProvider, int slotIndex, ItemStack itemStack) {
		if (!(inventoryProvider instanceof InventoryMenu)) {
			return itemStack; // Return early if the provider is not an InventoryMenu
		}

		InventoryMenu inventory = ((InventoryMenu) inventoryProvider);

		if (slotIndex < 0 || slotIndex >= inventory.slots.size()) {
			return itemStack;
		}

		Slot targetSlot = inventory.getSlot(slotIndex);

		if (!targetSlot.mayPlace(itemStack)) {
			return itemStack;
		} else {
			int inventoryStackLimit = targetSlot.getMaxStackSize();
			ItemStack newItemStack = itemStack.copy();
			newItemStack.setCount(Math.min(itemStack.getCount(), inventoryStackLimit));
			targetSlot.set(newItemStack);

			return inventoryStackLimit >= itemStack.getCount() ? null
					: itemStack.split(itemStack.getCount() - inventoryStackLimit);
		}
	}

	public static boolean mergeItemStack(List<Slot> var0, ItemStack var1, int var2, int var3, boolean var4) {
		return mergeItemStack(var0, var1, var2, var3, var4, true);
	}

	public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int startIndex, int endIndex, boolean reverse, boolean simulate) {
		boolean merged = false;
		int index = reverse ? endIndex -1 : startIndex;
		int increment = reverse ? -1 : 1;

		if (stack.isStackable()) {
			while (stack.getCount() > 0 && (reverse ? index >= startIndex : index < endIndex)) {
				Slot slot = slots.get(index);
				ItemStack slotStack = slot.getItem();

				if (slot.mayPlace(stack) && ItemStack.isSame(stack, slotStack) &&
						(!stack.hasTag() || ItemStack.tagMatches(stack, slotStack))) {
					int roomInSlot = slot.getMaxStackSize() - slotStack.getCount();
					int transferAmount = Math.min(stack.getCount(), roomInSlot);

					if (transferAmount > 0) {
						if (!simulate) {
							slotStack.grow(transferAmount);
							stack.shrink(transferAmount);
							slot.setChanged();
						}
						merged = true;
					}
				}
				index += increment;
			}
		}

		if (stack.getCount() > 0) {
			index = reverse ? endIndex -1 : startIndex;

			while (stack.getCount() > 0 && (reverse ? index >= startIndex : index < endIndex)) {
				Slot slot = slots.get(index);

				if (slot.mayPlace(stack) && slot.getItem().isEmpty()) {
					int transferAmount = simulate ? Math.min(stack.getCount(), slot.getMaxStackSize()) : stack.getCount();

					if (!simulate) {
						ItemStack newStack = stack.split(transferAmount);
						slot.set(newStack);
						slot.setChanged();
					}
					merged = true;
				}
				index += increment;
			}
		}

		return merged;
	}
}
