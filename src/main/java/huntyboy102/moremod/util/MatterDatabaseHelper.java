
package huntyboy102.moremod.util;

import huntyboy102.moremod.api.matter.IMatterDatabase;
import huntyboy102.moremod.api.matter.IMatterPatternStorage;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.util.Constants;

public class MatterDatabaseHelper {
	public static final int MAX_ITEM_PROGRESS = 100;
	public static final String PROGRESS_TAG_NAME = "scan_progress";
	public static final String ITEMS_TAG_NAME = "items";
	public static final String CAPACITY_TAG_NAME = "Capacity";

	public static void initTagCompound(ItemStack scanner) {
		CompoundTag tagCompound = new CompoundTag();
		scanner.setTag(tagCompound);
		initItemListTagCompound(scanner);

	}

	public static void initItemListTagCompound(ItemStack scanner) {
		ListTag items = new ListTag();
		scanner.setTag(ITEMS_TAG_NAME, items);
	}

	public static int getPatternCapacity(ItemStack storage) {
		if (storage.getTag() != null) {
			return storage.getTag().getShort(MatterDatabaseHelper.CAPACITY_TAG_NAME);
		}
		return 0;
	}

	public static boolean hasFreeSpace(ItemStack storage) {
		if (storage != null) {
			if (MatterHelper.isMatterPatternStorage(storage)) {
				IMatterPatternStorage patternStorage = (IMatterPatternStorage) storage.getItem();
				for (int i = 0; i < patternStorage.getCapacity(storage); i++) {
					ItemPattern itemPattern = patternStorage.getPatternAt(storage, i);
					if (itemPattern == null) {
						return true;
					}
				}
			}

		}
		return false;
	}

	public static ItemStack getFirstFreePatternStorage(IMatterDatabase database) {
		ItemStack[] patternStorages = database.getPatternStorageList();

		for (ItemStack patternStorage : patternStorages) {
			if (patternStorage != null) {
				if (hasFreeSpace(patternStorage)) {
					return patternStorage;
				}
			}
		}
		return null;
	}

	public static void addProgressToPatternStorage(ItemStack patternStorage, ItemStack item, int progress,
			boolean existingOnly) {
		if (!patternStorage.hasTag()) {
			initItemListTagCompound(patternStorage);
		}

		ListTag patternsTagList = patternStorage.getTag().getList(ITEMS_TAG_NAME,
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < patternsTagList.tagCount(); i++) {
			ItemPattern pattern = new ItemPattern(patternsTagList.getCompoundTagAt(i));
			if (areEqual(pattern.toItemStack(false), item)) {
				int oldProgress = patternsTagList.getCompoundTagAt(i).getByte(PROGRESS_TAG_NAME);
				patternsTagList.getCompoundTagAt(i).setByte(PROGRESS_TAG_NAME,
						(byte) Mth.clamp(oldProgress + progress, 0, MAX_ITEM_PROGRESS));
				return;
			}
		}

		if (!existingOnly) {
			ItemPattern pattern = new ItemPattern(item, progress);
			CompoundTag patternTag = new CompoundTag();
			pattern.writeToNBT(patternTag);
			patternsTagList.addTag(patternTag);
		}
	}

	public static int getItemStackProgress(ItemStack storage, ItemStack item) {
		ItemPattern itemPattern = getPatternFromStorage(storage, item);
		if (itemPattern != null) {
			return itemPattern.getProgress();
		}
		return -1;
	}

	public static ItemPattern getPatternFromStorage(ItemStack patternStorage, ItemStack item) {
		IMatterPatternStorage storage = (IMatterPatternStorage) patternStorage.getItem();
		for (int i = 0; i < storage.getCapacity(patternStorage); i++) {
			ItemPattern pattern = storage.getPatternAt(patternStorage, i);
			if (pattern != null && pattern.equals(item)) {
				return pattern;
			}
		}
		return null;
	}

	public static boolean areEqual(ItemStack one, ItemStack two) {
		if (one != null && two != null) {
			if (one.getItem() == two.getItem()) {
				if (one.getHasSubtypes() && two.getHasSubtypes()) {
					if (one.getDamageValue() == two.getDamageValue()) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean areEqual(CompoundTag one, CompoundTag two) {
		if (one == null || two == null) {
			return false;
		}

		return areEqual(new ItemStack(one), new ItemStack(two));
	}

	public static ItemStack GetItemStackFromWorld(LevelAccessor world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Item item = Item.getItemFromBlock(blockState.getBlock());
		if (item != null) {
			return new ItemStack(item, 1, blockState.getBlock().getMetaFromState(blockState));
		}
		return ItemStack.EMPTY;
	}

	public static ChatFormatting getPatternInfoColor(int progress) {
		ChatFormatting color;

		if (progress > 0 && progress <= 20) {
			color = ChatFormatting.RED;
		} else if (progress > 20 && progress <= 40) {
			color = ChatFormatting.GOLD;
		} else if (progress > 40 && progress <= 60) {
			color = ChatFormatting.YELLOW;
		} else if (progress > 40 && progress <= 80) {
			color = ChatFormatting.AQUA;
		} else if (progress > 80 && progress <= 100) {
			color = ChatFormatting.GREEN;
		} else {
			color = ChatFormatting.GREEN;
		}

		return color;
	}
}
