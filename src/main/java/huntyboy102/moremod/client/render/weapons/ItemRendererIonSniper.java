
package huntyboy102.moremod.client.render.weapons;

import huntyboy102.moremod.Reference;
import net.minecraft.util.ResourceLocation;

public class ItemRendererIonSniper extends WeaponItemRenderer {
	public static final String MODEL = Reference.PATH_MODEL + "item/ion_sniper.obj";

	public ItemRendererIonSniper() {
		super(new ResourceLocation(MODEL));
	}
}
