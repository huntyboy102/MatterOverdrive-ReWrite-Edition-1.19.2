
package huntyboy102.moremod.items;

import huntyboy102.moremod.items.includes.MOBaseItem;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.matter.IMatterPatternStorage;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MatterDatabaseHelper;
import huntyboy102.moremod.util.MatterHelper;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PatternDrive extends MOBaseItem implements IMatterPatternStorage, IConfigSubscriber {
	/*
	 * private IIcon storageFull; private IIcon storagePartiallyFull;
	 */
	final int capacity;

	private static int driveStackSize = 1;

	public PatternDrive(String name, int capacity) {
		super(name);
		this.capacity = capacity;
		this.setMaxStackSize(driveStackSize);
	}

	@Override
	public int getDamage(ItemStack stack) {
		if (stack.hasTagCompound()) {
			if (stack.getTagCompound().getKeySet().size() > 0) {
				if (stack.getTagCompound().getKeySet().size() < getCapacity(stack)) {
					return 1;
				} else {
					return 2;
				}
			}
		}
		return 0;
	}

	@Override
	public boolean hasDetails(ItemStack itemStack) {
		return true;
	}

	@Override
	public void addDetails(ItemStack itemstack, EntityPlayer player, @Nullable World worldIn, List<String> infos) {
		if (itemstack.hasTagCompound()) {
			for (int i = 0; i < getCapacity(itemstack); i++) {
				ItemPattern pattern = getPatternAt(itemstack, i);
				if (pattern != null) {
					ItemStack stack = pattern.toItemStack(false);
					String displayName;
					try {
						displayName = stack.getDisplayName();
					} catch (Exception e) {
						displayName = "Unknown";
					}

					if (MatterHelper.getMatterAmountFromItem(stack) > 0) {
						infos.add(MatterDatabaseHelper.getPatternInfoColor(pattern.getProgress()) + displayName + " ["
								+ pattern.getProgress() + "%]");
					} else {
						infos.add(TextFormatting.RED + "[Invalid] "
								+ MatterDatabaseHelper.getPatternInfoColor(pattern.getProgress()) + displayName + " ["
								+ pattern.getProgress() + "%]");
					}
				}
			}
		}
	}

	@Override
	public void InitTagCompount(ItemStack stack) {
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setShort(MatterDatabaseHelper.CAPACITY_TAG_NAME, (short) capacity);
		NBTTagList itemList = new NBTTagList();
		tagCompound.setTag(MatterDatabaseHelper.ITEMS_TAG_NAME, itemList);
		stack.setTagCompound(tagCompound);
	}

	@Override
	public ItemPattern getPatternAt(ItemStack storage, int slot) {
		if (storage.getTagCompound() != null) {
			if (slot < getCapacity(storage) && storage.getTagCompound().hasKey("p" + slot)) {
				ItemPattern pattern = new ItemPattern(storage.getTagCompound().getCompoundTag("p" + slot));
				return pattern;
			}
		}
		return null;
	}

	@Override
	public void setItemPatternAt(ItemStack storage, int slot, ItemPattern itemPattern) {
		if (storage.getTagCompound() == null) {
			storage.setTagCompound(new NBTTagCompound());
		}

		if (itemPattern != null) {
			NBTTagCompound patternTag = new NBTTagCompound();
			itemPattern.writeToNBT(patternTag);
			storage.getTagCompound().setTag("p" + slot, patternTag);
		} else {
			storage.getTagCompound().removeTag("p" + slot);
		}
	}

	@Override
	public boolean increasePatternProgress(ItemStack itemStack, int slot, int amount) {
		return false;
	}

	@Override
	public int getCapacity(ItemStack item) {
		TagCompountCheck(item);
		return item.getTagCompound().getShort(MatterDatabaseHelper.CAPACITY_TAG_NAME);
	}

	public void clearStorage(ItemStack itemStack) {
		if (MatterHelper.isMatterPatternStorage(itemStack)) {
			itemStack.setTagCompound(null);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (playerIn.isSneaking()) {
			return new ActionResult<>(EnumActionResult.SUCCESS,
					new ItemStack(MatterOverdrive.ITEMS.pattern_drive, 1, 0));
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		driveStackSize = config.getInt("drive_stack_size", ConfigurationHandler.CATEGORY_MACHINES, 1,
				"The stack size of the Pattern Drive.");

		MOLog.log(Level.INFO, "Setting pattern drive stack size to %d.", driveStackSize);

		this.setMaxStackSize(driveStackSize);
	}

	private int getPatternsStored(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			return 0;
		}

		NBTTagCompound compound = stack.getTagCompound();

		int total = 0;

		for (int i = 0; i < getCapacity(stack); i++) {
			if (compound.hasKey("p" + i)) {
				total += 1;
			}
		}

		return total;
	}

	@Override
	public void initItemModel() {
		ModelLoader.setCustomMeshDefinition(this, stack -> {
			int patternsStored = getPatternsStored(stack);

			return new ModelResourceLocation(getRegistryName(),
					patternsStored == 2 ? "level=full" : patternsStored > 0 ? "level=partial" : "level=empty");
		});

		ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), "level=full"),
				new ModelResourceLocation(getRegistryName(), "level=partial"),
				new ModelResourceLocation(getRegistryName(), "level=empty"));
	}
}
