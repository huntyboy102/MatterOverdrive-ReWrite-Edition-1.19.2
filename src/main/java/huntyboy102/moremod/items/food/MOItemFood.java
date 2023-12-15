
package huntyboy102.moremod.items.food;

import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.internal.ItemModelProvider;
import huntyboy102.moremod.client.ClientUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;

/**
 * @author shadowfacts
 */
public class MOItemFood extends ItemFood implements ItemModelProvider {

	public String name;

	public MOItemFood(String name, int amount, float saturation, boolean isWolfFood) {
		super(amount, saturation, isWolfFood);
		this.name = name;

		setTranslationKey(Reference.MOD_ID + "." + name);
		setRegistryName(name);

		setCreativeTab(MatterOverdrive.TAB_OVERDRIVE);
	}

	@Override
	public void initItemModel() {
		if (!getHasSubtypes())
			ClientUtil.registerModel(this, getRegistryName().toString());
		else {
			NonNullList<ItemStack> sub = NonNullList.create();
			getSubItems(CreativeTabs.SEARCH, sub);
			for (ItemStack stack : sub) {
				ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getMetadata(),
						new ModelResourceLocation(getRegistryName(), "inventory"));
			}
		}
	}

}
