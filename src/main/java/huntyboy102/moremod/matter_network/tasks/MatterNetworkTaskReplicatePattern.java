
package huntyboy102.moremod.matter_network.tasks;

import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class MatterNetworkTaskReplicatePattern extends MatterNetworkTask {
	ItemPattern pattern;
	int amount;

	public MatterNetworkTaskReplicatePattern() {
		super();
		pattern = new ItemPattern();
	}

	public MatterNetworkTaskReplicatePattern(short itemID, short itemMetadata, byte amount) {
		pattern = new ItemPattern(itemID, itemMetadata);
		this.amount = amount;
	}

	public MatterNetworkTaskReplicatePattern(ItemPattern pattern, int amount) {
		this.pattern = pattern;
		this.amount = amount;
	}

	@Override
	protected void init() {
		setUnlocalizedName("replicate_pattern");
	}

	@Override
	public void readFromNBT(CompoundTag compound) {
		super.readFromNBT(compound);
		if (compound != null) {
			pattern.readFromNBT(compound.getCompound("Pattern"));
			amount = compound.getShort("amount");
		}
	}

	@Override
	public void writeToNBT(CompoundTag compound) {
		super.writeToNBT(compound);
		compound.putShort("amount", (short) amount);
		if (compound != null) {
			CompoundTag tagCompound = new CompoundTag();
			pattern.writeToNBT(tagCompound);
			compound.put("Pattern", tagCompound);
		}
	}

	@Override
	public String getName() {
		return String.format("[%s] %s", amount,
				MOStringHelper.translateToLocal(pattern.getItem() + ".name"));
	}

	public ItemPattern getPattern() {
		return pattern;
	}

	public boolean isValid(Level world) {
		if (!super.isValid(world)) {
			return false;
		}

		return MatterHelper.getMatterAmountFromItem(pattern.toItemStack(false)) > 0;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
