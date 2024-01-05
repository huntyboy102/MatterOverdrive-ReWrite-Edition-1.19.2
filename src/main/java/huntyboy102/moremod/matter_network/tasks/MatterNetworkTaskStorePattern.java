
package huntyboy102.moremod.matter_network.tasks;

import huntyboy102.moremod.util.MatterDatabaseHelper;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.text.DecimalFormat;

public class MatterNetworkTaskStorePattern extends MatterNetworkTask {
	ItemStack itemStack;
	int progress;

	public MatterNetworkTaskStorePattern() {
		super();

	}

	public MatterNetworkTaskStorePattern(ItemStack itemStack, int progress) {
		this.itemStack = itemStack;
		this.progress = progress;
	}

	@Override
	protected void init() {
		setUnlocalizedName("store_pattern");
	}

	@Override
	public void readFromNBT(CompoundTag compound) {
		super.readFromNBT(compound);
		if (compound != null) {
			itemStack = new ItemStack(compound.getCompound("Item"));
			progress = compound.getInt(MatterDatabaseHelper.PROGRESS_TAG_NAME);
		}
	}

	@Override
	public void writeToNBT(CompoundTag compound) {
		super.writeToNBT(compound);
		if (compound != null) {
			CompoundTag itemComp = new CompoundTag();
			if (itemStack != null) {
				itemStack.writeToNBT(itemComp);
			}
			compound.put("Item", itemComp);
			compound.putInt(MatterDatabaseHelper.PROGRESS_TAG_NAME, progress);
		}
	}

	@Override
	public String getName() {
		return itemStack.getDisplayName() + " +" + DecimalFormat.getPercentInstance().format(progress / 100f);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isValid(Level world) {
		if (!super.isValid(world)) {
			return false;
		}

		return MatterHelper.getMatterAmountFromItem(itemStack) > 0;
	}

}
