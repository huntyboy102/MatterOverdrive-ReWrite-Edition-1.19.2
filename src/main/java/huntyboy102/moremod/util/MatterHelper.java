
package huntyboy102.moremod.util;

import huntyboy102.moremod.items.MatterScanner;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.inventory.IUpgrade;
import huntyboy102.moremod.api.matter.IMatterHandler;
import huntyboy102.moremod.api.matter.IMatterItem;
import huntyboy102.moremod.api.matter.IMatterPatternStorage;
import huntyboy102.moremod.init.OverdriveFluids;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MatterHelper {
	public static final String MATTER_UNIT = " kM";

	public static boolean containsMatter(ItemStack item) {
		return getMatterAmountFromItem(item) > 0;
	}

	public static int getMatterAmountFromItem(ItemStack item) {
		if (item != null && !item.isEmpty()) {
			if (item.getItem() instanceof IMatterItem) {
				return ((IMatterItem) item.getItem()).getMatter(item);
			} else {
				return MatterOverdriveRewriteEdition.MATTER_REGISTRY.getMatter(item);
			}
		}
		return 0;
	}

	public static int getEnergyFromMatter(int multiply, ItemStack itemStack) {
		int matter = getMatterAmountFromItem(itemStack);
		return multiply * matter;
	}

	public static int getTotalEnergyFromMatter(int multiply, ItemStack itemStack, int time) {
		int matter = getMatterAmountFromItem(itemStack);
		return multiply * matter * time;
	}

	public static int Transfer(int amount, IMatterHandler from, IFluidHandler to) {
		int extract = from.extractMatter(amount, true);
		int recived = to.fill(new FluidStack(OverdriveFluids.matterPlasma, extract), true);
		from.extractMatter(recived, false);
		return recived;
	}

	private static Recipe GetRecipeOf(Level world, ItemStack item) {
		RecipeManager recipeManager = world.getRecipeManager();
		Iterable<Recipe<?>> recipes = recipeManager.getRecipes();

		for (Recipe<?> recipe : recipes) {
			if (recipe.getResultItem().sameItem(item)) {
				return recipe;
			}
		}

		return null;
	}

	public static boolean isMatterScanner(ItemStack item) {
		return item != null && item.getItem() != null && item.getItem() instanceof MatterScanner;
	}

	public static boolean isMatterPatternStorage(ItemStack item) {
		return item != null && item.getItem() != null && item.getItem() instanceof IMatterPatternStorage;
	}

	public static boolean isUpgrade(ItemStack itemStack) {
		return itemStack != null && itemStack.getItem() instanceof IUpgrade;
	}

	public static boolean CanScan(ItemStack stack) {
		if (MatterHelper.getMatterAmountFromItem(stack) <= 0) {
			return false;
		}

		Item item = stack.getItem();

		if (item instanceof BlockItem) {
			Block block = Block.getBlockFromItem(item);

			return block != Blocks.BEDROCK && block != Blocks.AIR;
		}

		return true;
	}

	public static String formatMatter(int matter) {
		return MOStringHelper.formatNumber(matter) + MATTER_UNIT;
	}

	public static String formatMatter(double matter) {
		return MOStringHelper.formatNumber(matter) + MATTER_UNIT;
	}

	public static String formatMatter(int matter, int capacity) {
		return MOStringHelper.formatNumber(matter) + " / " + MOStringHelper.formatNumber(capacity) + MATTER_UNIT;
	}

	public static boolean DropInventory(Level world, Inventory inventory, BlockPos pos) {
		if (inventory != null) {
			for (int i1 = 0; i1 < inventory.getContainerSize(); ++i1) {
				ItemStack itemstack = inventory.getItem(i1);

				if (!itemstack.isEmpty()) {
					float f = world.getRandom().nextFloat() * 0.8F + 0.1F;
					float f1 = world.getRandom().nextFloat() * 0.8F + 0.1F;
					float f2 = world.getRandom().nextFloat() * 0.8F + 0.1F;

					ItemEntity entityitem = new ItemEntity(world, (double) ((float) pos.getX() + f),
							((float) pos.getY() + f1), (double) ((float) pos.getZ() + f2), itemstack);

					if (itemstack.hasTag()) {
						entityitem.getItem().setTag(itemstack.getTag().copy());
					}

					float f3 = 0.05F;
					entityitem.xo = ((float) world.getRandom().nextGaussian() * f3);
					entityitem.yo = ((float) world.getRandom().nextGaussian() * f3 + 0.2F);
					entityitem.zo = ((float) world.getRandom().nextGaussian() * f3);
					world.spawnEntity(entityitem);
				}
			}
			return true;
		}

		return false;
	}

	public static void DrawMatterInfoTooltip(ItemStack itemStack, int speed, int energyPerTick, List<String> tooltips) {
		int matter = MatterHelper.getMatterAmountFromItem(itemStack);
		if (matter > 0) {
			tooltips.add(ChatFormatting.ITALIC.toString() + ChatFormatting.BLUE.toString() + "Matter: "
					+ MatterHelper.formatMatter(matter));
			tooltips.add(ChatFormatting.ITALIC.toString() + ChatFormatting.DARK_RED + "Power: "
					+ MOEnergyHelper.formatEnergy(speed * matter * energyPerTick));
		}
	}
}
