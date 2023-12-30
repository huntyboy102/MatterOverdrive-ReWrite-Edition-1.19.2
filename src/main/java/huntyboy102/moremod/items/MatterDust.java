
package huntyboy102.moremod.items;

import huntyboy102.moremod.items.includes.MOItemOre;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.matter.IMatterItem;
import huntyboy102.moremod.api.matter.IRecyclable;
import huntyboy102.moremod.util.MatterHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MatterDust extends MOItemOre implements IRecyclable, IMatterItem {
	final boolean isRefined;

	public MatterDust(String name, String oreDict, boolean refined) {
		super(name, oreDict);
		isRefined = refined;
	}

	@Override
	public void addDetails(ItemStack itemstack, EntityPlayer player, @Nullable World worldIn, List<String> infos) {
		if (!isRefined) {
			infos.add(
					TextFormatting.BLUE + "Potential Matter: " + MatterHelper.formatMatter(itemstack.getItemDamage()));
		}
	}

	public int getDamage(ItemStack stack) {
		TagCompountCheck(stack);
		return stack.getTagCompound().getInteger("Matter");
	}

	public void setMatter(ItemStack itemStack, int matter) {
		TagCompountCheck(itemStack);
		itemStack.getTagCompound().setInteger("Matter", matter);
	}

	@Override
	public boolean hasDetails(ItemStack itemStack) {
		return !isRefined;
	}

	@Override
	public ItemStack getOutput(ItemStack from) {
		ItemStack newItemStack = new ItemStack(MatterOverdrive.ITEMS.matter_dust_refined);
		MatterOverdrive.ITEMS.matter_dust_refined.setMatter(newItemStack, from.getItemDamage());
		return newItemStack;
	}

	@Override
	public int getRecycleMatter(ItemStack stack) {
		return stack.getItemDamage();
	}

	@Override
	public boolean canRecycle(ItemStack stack) {
		return stack.getItem() instanceof MatterDust && !((MatterDust) stack.getItem()).isRefined;
	}

	@Override
	public int getMatter(ItemStack itemStack) {
		return itemStack.getItem() instanceof MatterDust && ((MatterDust) itemStack.getItem()).isRefined
				? itemStack.getItemDamage()
				: 0;
	}
}
