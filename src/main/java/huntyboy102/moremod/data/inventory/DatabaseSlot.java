
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MatterHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DatabaseSlot extends Slot {
	public DatabaseSlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	@Override
	public boolean isValidForSlot(ItemStack itemStack) {
		return MatterHelper.isMatterScanner(itemStack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public HoloIcon getHoloIcon() {
		return ClientProxy.holoIcons.getIcon("scanner");
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "gui.tooltip.slot.database";
	}
}
