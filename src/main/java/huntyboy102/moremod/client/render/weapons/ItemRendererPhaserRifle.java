
package huntyboy102.moremod.client.render.weapons;

import huntyboy102.moremod.Reference;
import net.minecraft.util.ResourceLocation;

public class ItemRendererPhaserRifle extends WeaponItemRenderer {
	public static final String MODEL = Reference.PATH_MODEL + "item/phaser_rifle.obj";
	public static final String FLASH_TEXTURE = Reference.PATH_FX + "phaser_rifle_flash.png";

	public static ResourceLocation flashTexture;

	public ItemRendererPhaserRifle() {
		super(new ResourceLocation(MODEL));
		flashTexture = new ResourceLocation(FLASH_TEXTURE);
	}
}
