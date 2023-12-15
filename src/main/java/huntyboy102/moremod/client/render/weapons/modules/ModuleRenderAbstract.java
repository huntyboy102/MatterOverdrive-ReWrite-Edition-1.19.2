
package huntyboy102.moremod.client.render.weapons.modules;

import huntyboy102.moremod.client.render.weapons.WeaponRenderHandler;

public abstract class ModuleRenderAbstract implements IModuleRender {
	protected final WeaponRenderHandler weaponRenderer;

	public ModuleRenderAbstract(WeaponRenderHandler weaponRenderer) {
		this.weaponRenderer = weaponRenderer;
	}
}
