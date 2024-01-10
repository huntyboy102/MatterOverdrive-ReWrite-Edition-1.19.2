
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.api.IUpgradeable;
import huntyboy102.moremod.api.inventory.IUpgrade;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

public class UpgradeSlot extends Slot {
	private IUpgradeable upgradeable;

	public UpgradeSlot(boolean isMainSlot, IUpgradeable upgradeable) {
		super(isMainSlot);
		this.upgradeable = upgradeable;
	}

	@Override
	public boolean isValidForSlot(ItemStack item) {
		if (item.getItem() instanceof IUpgrade) {
			IUpgrade upgrade = (IUpgrade) item.getItem();
			UpgradeTypes mainUpgradeType = upgrade.getMainUpgrade(item);
			if (mainUpgradeType != null) {
				return upgradeable.isAffectedByUpgrade(mainUpgradeType);
			} else {
				Map<UpgradeTypes, Double> upgradeMap = upgrade.getUpgrades(item);
				for (final Map.Entry<UpgradeTypes, Double> entry : upgradeMap.entrySet()) {
					if (upgradeable.isAffectedByUpgrade(entry.getKey())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public HoloIcon getHoloIcon() {
		return ClientProxy.holoIcons.getIcon("upgrade");
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean keepOnDismantle() {
		return true;
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "gui.tooltip.slot.upgrade";
	}
}
