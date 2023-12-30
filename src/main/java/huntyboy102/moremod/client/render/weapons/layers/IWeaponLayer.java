
package huntyboy102.moremod.client.render.weapons.layers;

import huntyboy102.moremod.client.resources.data.WeaponMetadataSection;
import net.minecraft.item.ItemStack;

public interface IWeaponLayer {
	void renderLayer(WeaponMetadataSection weaponMeta, ItemStack weapon, float ticks);
}
