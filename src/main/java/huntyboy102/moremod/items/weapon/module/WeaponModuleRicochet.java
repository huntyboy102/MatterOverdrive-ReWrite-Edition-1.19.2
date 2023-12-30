
package huntyboy102.moremod.items.weapon.module;

import java.util.List;

import javax.annotation.Nullable;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.weapon.WeaponStats;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class WeaponModuleRicochet extends WeaponModuleBase {
	public WeaponModuleRicochet(String name) {
		super(name);
		applySlot(Reference.MODULE_OTHER);
		applyWeaponStat(0, WeaponStats.RICOCHET, 100);
	}

	@Override
	public String getModelPath() {
		return null;
	}

	@Override
	public ResourceLocation getModelTexture(ItemStack module) {
		return null;
	}

	@Override
	public String getModelName(ItemStack module) {
		return null;
	}
	@Override
	public void addDetails(ItemStack itemstack, EntityPlayer player, @Nullable World worldIn, List<String> infos) {
		//super.addDetails(itemstack, player, worldIn, infos);
		infos.add(TextFormatting.GREEN + "Ricochet Module.");
		infos.add(MOStringHelper.translateToLocal("moduleslot." + getSlot(itemstack) + ".name"));
	}
}
