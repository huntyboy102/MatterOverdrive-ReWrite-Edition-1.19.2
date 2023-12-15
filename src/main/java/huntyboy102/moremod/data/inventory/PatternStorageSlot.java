
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.api.matter.IMatterDatabase;
import huntyboy102.moremod.api.matter.IMatterPatternStorage;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PatternStorageSlot extends Slot {
	private IMatterDatabase database;
	private int storageId;

	public PatternStorageSlot(boolean isMainSlot, IMatterDatabase database, int storageId) {
		super(isMainSlot);
		this.database = database;
		this.storageId = storageId;
	}

	@Override
	public boolean isValidForSlot(ItemStack item) {
		return item.getItem() instanceof IMatterPatternStorage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public HoloIcon getHoloIcon() {
		return ClientProxy.holoIcons.getIcon("pattern_storage");
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "gui.tooltip.slot.pattern_storage";
	}

	public void onSlotChanged() {
		database.onPatternStorageChange(storageId);
	}
}
