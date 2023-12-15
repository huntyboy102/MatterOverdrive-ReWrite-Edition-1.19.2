
package huntyboy102.moremod.items;

import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.internal.ItemModelProvider;
import huntyboy102.moremod.client.ClientUtil;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;

/**
 * @author shadowfacts
 */
public class ItemRecordTransformation extends ItemRecord implements ItemModelProvider {

	public ItemRecordTransformation() {
		super("matteroverdrive.transformation", MatterOverdriveSounds.musicTransformation);
		setTranslationKey("record");
		setRegistryName("record_transformation");
		setCreativeTab(MatterOverdrive.TAB_OVERDRIVE);
	}

	@Override
	public SoundEvent getSound() {
		return super.getSound();
	}

	@Override
	public void initItemModel() {
		ClientUtil.registerModel(this, this.getRegistryName().toString());
	}

}
