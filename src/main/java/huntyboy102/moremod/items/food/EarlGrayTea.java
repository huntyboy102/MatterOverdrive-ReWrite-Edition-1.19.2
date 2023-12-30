
package huntyboy102.moremod.items.food;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EarlGrayTea extends MOItemFood {
	public EarlGrayTea(String name) {
		super(name, 4, 0.8F, false);
		setAlwaysEdible();
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		super.onItemUseFinish(stack, worldIn, entityLiving);

		if (!(entityLiving instanceof EntityPlayer)) {
			return stack;
		}
		if (!((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		if (!worldIn.isRemote) {
			entityLiving.curePotionEffects(stack);
		}

		if (stack.getCount() > 0) {
			((EntityPlayer) entityLiving).inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
		}

		return stack.getCount() <= 0 ? new ItemStack(Items.GLASS_BOTTLE) : stack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.DRINK;
	}
}