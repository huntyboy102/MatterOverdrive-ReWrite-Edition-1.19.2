
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.api.matter.IRecyclable;
import net.minecraft.world.item.ItemStack;

public class SlotRecycler extends Slot {
	public SlotRecycler(boolean isMainSlot) {
		super(isMainSlot);
	}

	public boolean isValidForSlot(ItemStack item) {
		return item.getItem() instanceof IRecyclable && ((IRecyclable) item.getItem()).canRecycle(item);
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "gui.tooltip.slot.recycle";
	}
}
