
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TeleportFlashDriveSlot extends Slot {
	public TeleportFlashDriveSlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	public boolean isValidForSlot(ItemStack item) {
		return item != null && item.getItem() == MatterOverdriveRewriteEdition.ITEMS.transportFlashDrive;
	}

	@OnlyIn(Dist.CLIENT)
	public HoloIcon getHoloIcon() {
		return ClientProxy.holoIcons.getIcon("flash_drive");
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "item.transport_flash_drive.name";
	}
}
