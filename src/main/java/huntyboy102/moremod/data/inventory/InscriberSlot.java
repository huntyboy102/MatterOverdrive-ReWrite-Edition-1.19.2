
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.init.MatterOverdriveRecipes;
import net.minecraft.world.item.ItemStack;

public class InscriberSlot extends Slot {
	boolean isSecSlot;

	public InscriberSlot(boolean isMainSlot, boolean isSecSlot) {
		super(isMainSlot);
		this.isSecSlot = isSecSlot;
	}

	@Override
	public boolean isValidForSlot(ItemStack stack) {
		return isSecSlot ? MatterOverdriveRecipes.INSCRIBER.isSecondaryInput(stack)
				: MatterOverdriveRecipes.INSCRIBER.isPrimaryInput(stack);
	}

	public int getMaxStackSize() {
		if (isSecSlot) {
			return 64;
		} else {
			return 1;
		}
	}
}
