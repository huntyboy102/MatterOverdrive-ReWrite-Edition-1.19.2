
package huntyboy102.moremod.container.slot;

import huntyboy102.moremod.util.MOEnergyHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotEnergy extends MOSlot {
	public SlotEnergy(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean isValid(ItemStack itemStack) {
		return MOEnergyHelper.isEnergyContainerItem(itemStack);
	}
}
