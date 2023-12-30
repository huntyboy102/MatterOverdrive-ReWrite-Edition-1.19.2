
package huntyboy102.moremod.items;

import huntyboy102.moremod.items.includes.MOBaseItem;
import matteroverdrive.MatterOverdrive;

public class HoloSightsBase extends MOBaseItem {
	public HoloSightsBase(String name) {
		super(name);
		this.setMaxStackSize(8);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		setCreativeTab(MatterOverdrive.TAB_OVERDRIVE_MODULES);
	}
}