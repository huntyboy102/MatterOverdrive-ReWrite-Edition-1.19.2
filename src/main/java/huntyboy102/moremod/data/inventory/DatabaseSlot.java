
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MatterHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DatabaseSlot extends Slot {
	public DatabaseSlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	@Override
	public boolean isValidForSlot(ItemStack itemStack) {
		return MatterHelper.isMatterScanner(itemStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
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
