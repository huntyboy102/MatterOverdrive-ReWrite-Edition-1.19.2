
package huntyboy102.moremod.api.inventory;

import net.minecraft.world.item.ItemStack;

/**
 * Created by Simeon on 8/2/2015. Used by energy weapons as one use charging
 * items (Ammo).
 */
public interface IEnergyPack {
	/**
	 * The amount of energy the item restores.
	 *
	 * @param pack The Item Stack
	 * @return The amount of energy restored.
	 */
	int getEnergyAmount(ItemStack pack);
}
