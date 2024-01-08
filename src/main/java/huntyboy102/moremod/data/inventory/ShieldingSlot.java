
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShieldingSlot extends Slot {
	public ShieldingSlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	@Override
	public boolean isValidForSlot(ItemStack itemStack) {
		if (this.getItem() == null || this.getItem().getCount() < 4) {
			if (itemStack != null && !itemStack.isEmpty()) {
				return itemStack.getItem() == MatterOverdriveRewriteEdition.ITEMS.tritanium_plate;
			}
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public HoloIcon getHoloIcon() {
		return ClientProxy.holoIcons.getIcon("shielding");
	}

	@Override
	public int getMaxStackSize() {
		return 5;
	}

	@Override
	public boolean keepOnDismantle() {
		return true;
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "gui.tooltip.slot.shielding";
	}
}
