
package huntyboy102.moremod.api.starmap;

import huntyboy102.moremod.starmap.data.Planet;
import net.minecraft.item.ItemStack;

public interface IPlanetStatChange {
	float changeStat(ItemStack stack, Planet planet, PlanetStatType statType, float original);
}
