
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.items.Contract;
import net.minecraft.item.ItemStack;

public class SlotContract extends Slot {
	public SlotContract(boolean isMainSlot) {
		super(isMainSlot);
	}

	@Override
	public boolean isValidForSlot(ItemStack item) {
		return item.getItem() instanceof Contract;
	}
}
