
package huntyboy102.moremod.items;

import huntyboy102.moremod.items.includes.MOBaseItem;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SecurityProtocol extends MOBaseItem implements IAdvancedModelProvider {

	public static final String[] types = new String[] { "empty", "claim", "access", "remove" };

	public SecurityProtocol(String name) {
		super(name);
		setMaxStackSize(16);
	}

	@Override
	public String[] getSubNames() {
		return types;
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addDetails(ItemStack itemstack, EntityPlayer player, @Nullable World worldIn, List<String> infos) {

		if (itemstack.hasTagCompound()) {
			try {
				EntityPlayer entityPlayer = player.world
						.getPlayerEntityByUUID(UUID.fromString(itemstack.getTagCompound().getString("Owner")));
				if (entityPlayer != null) {
					String owner = entityPlayer.getGameProfile().getName();
					infos.add(TextFormatting.YELLOW + "Owner: " + owner);
				}
			} catch (Exception e) {
				infos.add(TextFormatting.RED + MOStringHelper.translateToLocal(getTranslationKey() + ".invalid"));
			}
		}
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister p_94581_1_) {
	 * icons = new IIcon[types.length];
	 * 
	 * for (int i = 0;i < types.length;i++) { icons[i] =
	 * p_94581_1_.registerIcon(this.getIconString() + "_" + types[i]); } }
	 * 
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public IIcon getIconFromDamage(int damage) { return
	 * icons[damage]; }
	 */

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey(stack) + "." + types[MathHelper.clamp(stack.getItemDamage(), 0, types.length)];
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);
		if (!itemStackIn.hasTagCompound()) {
			if (playerIn.isSneaking()) {
				TagCompountCheck(itemStackIn);
				itemStackIn.getTagCompound().setString("Owner", playerIn.getGameProfile().getId().toString());
				itemStackIn.setItemDamage(1);
				return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
			}
		} else if (itemStackIn.getTagCompound().getString("Owner").equals(playerIn.getGameProfile().getId().toString())
				|| playerIn.capabilities.isCreativeMode) {
			if (playerIn.isSneaking()) {
				int damage = itemStackIn.getItemDamage() + 1;
				if (damage >= types.length) {
					damage = 1;
				}

				itemStackIn.setItemDamage(damage);
				return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof MOTileEntityMachine) {
				if (stack.getItemDamage() == 1) {
					if (((MOTileEntityMachine) tileEntity).claim(stack)) {
						stack.shrink(1);
						return EnumActionResult.SUCCESS;
					}
				} else if (stack.getItemDamage() == 3) {
					if (((MOTileEntityMachine) tileEntity).unclaim(stack)) {
						stack.shrink(1);
						return EnumActionResult.SUCCESS;
					}
				}
			}
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public boolean hasDetails(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("Owner");
	}
}
