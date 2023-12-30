
package huntyboy102.moremod.items.tools;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.internal.ItemModelProvider;
import huntyboy102.moremod.client.ClientUtil;
import huntyboy102.moremod.init.MatterOverdriveItems;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;

/**
 * @author shadowfacts
 */
public class TritaniumSword extends ItemSword implements ItemModelProvider {

	public TritaniumSword(String name) {
		super(MatterOverdriveItems.TOOL_MATERIAL_TRITANIUM);
		setTranslationKey(Reference.MOD_ID + "." + name);
		setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
	}

	@Override
	public void initItemModel() {
		ClientUtil.registerModel(this, this.getRegistryName().toString());
	}

}
