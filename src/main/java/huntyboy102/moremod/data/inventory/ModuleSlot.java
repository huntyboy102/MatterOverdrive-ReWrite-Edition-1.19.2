
package huntyboy102.moremod.data.inventory;

import huntyboy102.moremod.api.weapon.IWeapon;
import huntyboy102.moremod.api.weapon.IWeaponModule;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.util.WeaponHelper;
import huntyboy102.moremod.Reference;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ModuleSlot extends Slot {
	int type;
	WeaponSlot weaponSlot;

	public ModuleSlot(boolean isMainSlot, int type, WeaponSlot weaponSlot) {
		super(isMainSlot);
		this.type = type;
		this.weaponSlot = weaponSlot;
	}

	@Override
	public boolean isValidForSlot(ItemStack item) {
		switch (type) {
		case Reference.MODULE_BATTERY:
			return MOEnergyHelper.isEnergyContainerItem(item) && !WeaponHelper.isWeapon(item);
		default:
			if (WeaponHelper.isWeaponModule(item)) {
				if (item.getItem() instanceof IWeaponModule && ((IWeaponModule) item.getItem()).getSlot(item) == type) {
					if (weaponSlot != null && !weaponSlot.getItem().isEmpty()
							&& weaponSlot.getItem().getItem() instanceof IWeapon) {
						return ((IWeapon) weaponSlot.getItem().getItem()).supportsModule(weaponSlot.getItem(), item);
					}
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void setItem(@Nonnull ItemStack item) {
		super.setItem(item);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public HoloIcon getHoloIcon() {
		switch (type) {
		case Reference.MODULE_BATTERY:
			return ClientProxy.holoIcons.getIcon("battery");
		case Reference.MODULE_COLOR:
			return ClientProxy.holoIcons.getIcon("color");
		case Reference.MODULE_BARREL:
			return ClientProxy.holoIcons.getIcon("barrel");
		case Reference.MODULE_SIGHTS:
			return ClientProxy.holoIcons.getIcon("sights");
		default:
			return ClientProxy.holoIcons.getIcon("module");
		}
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}
