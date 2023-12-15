
package huntyboy102.moremod.client.render.weapons.modules;

import huntyboy102.moremod.client.RenderHandler;
import huntyboy102.moremod.client.resources.data.WeaponMetadataSection;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

public interface IModuleRender {
	void renderModule(WeaponMetadataSection weaponMeta, ItemStack weaponStack, ItemStack moduleStack, float ticks);

	void transformWeapon(WeaponMetadataSection weaponMeta, ItemStack weaponStack, ItemStack moduleStack, float ticks,
			float zoomValue);

	void onModelBake(TextureMap textureMap, RenderHandler renderHandler);

	void onTextureStich(TextureMap textureMap, RenderHandler renderHandler);
}
