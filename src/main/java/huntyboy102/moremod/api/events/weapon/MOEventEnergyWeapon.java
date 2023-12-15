
package huntyboy102.moremod.api.events.weapon;

import huntyboy102.moremod.items.weapon.EnergyWeapon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

public class MOEventEnergyWeapon extends LivingEvent {
	public final ItemStack weaponStack;
	public final EnergyWeapon energyWeapon;

	public MOEventEnergyWeapon(ItemStack weaponStack) {
		this(weaponStack, null);
	}

	public MOEventEnergyWeapon(ItemStack weaponStack, EntityLivingBase shooter) {
		super(shooter);
		this.weaponStack = weaponStack;
		if (weaponStack.getItem() instanceof EnergyWeapon) {
			this.energyWeapon = (EnergyWeapon) weaponStack.getItem();
		} else {
			throw new RuntimeException("Weapon Stack's Item must be of type Energy Weapon");
		}
	}

	public static class Overheat extends MOEventEnergyWeapon {

		public Overheat(ItemStack weaponStack) {
			super(weaponStack);
		}

		public Overheat(ItemStack weaponStack, EntityLivingBase shooter) {
			super(weaponStack, shooter);
		}

		public boolean isCancelable() {
			return true;
		}
	}
}
