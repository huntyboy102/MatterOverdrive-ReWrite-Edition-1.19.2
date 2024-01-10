
package huntyboy102.moremod.data.inventory;

import net.minecraft.world.item.ItemStack;

public class RemoveOnlySlot extends Slot {
	public RemoveOnlySlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	@Override
	public boolean isValidForSlot(ItemStack itemStack) {
		return false;
	}
}
