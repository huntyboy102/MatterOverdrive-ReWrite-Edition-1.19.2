
package huntyboy102.moremod.api.weapon;

import net.minecraft.item.ItemStack;

public interface IWeaponColor extends IWeaponModule {
	int getColor(ItemStack module, ItemStack weapon);
}
