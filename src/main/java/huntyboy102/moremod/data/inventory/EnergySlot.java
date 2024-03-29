
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOEnergyHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnergySlot extends Slot {
	public EnergySlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	@Override
	public boolean isValidForSlot(ItemStack itemStack) {
		return MOEnergyHelper.isEnergyContainerItem(itemStack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public HoloIcon getHoloIcon() {
		return ClientProxy.holoIcons.getIcon("energy");
	}

	@Override
	public boolean keepOnDismantle() {
		return true;
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "gui.tooltip.slot.energy";
	}
}
