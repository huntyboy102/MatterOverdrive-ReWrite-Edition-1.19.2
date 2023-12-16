
package huntyboy102.moremod.api.matter;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Used as a interface for all matter storage entities or components.
 */
public interface IMatterHandler extends INBTSerializable<CompoundTag> {
	/**
	 * @return the current matter stored.
	 */
	int getMatterStored();

	void setMatterStored(int amount);

	/**
	 * @return the maximum capacity.
	 */
	int getCapacity();

	/**
	 * @param capacity Sets the capacity
	 */
	void setCapacity(int capacity);

	/**
	 * Modifies the amount of matter stored by the given amount.
	 *
	 * @param amount The amount of matter to add/subtract
	 * @return The amount added/subtracted
	 */
	int modifyMatterStored(int amount);

	/**
	 * Used to receive matter.
	 *
	 * @param amount   the amount of received matter.
	 * @param simulate is this a simulation. No matter will be stored if this is
	 *                 true. Used to check if the given amount of matter can be
	 *                 received.
	 * @return the amount of matter received. This is the same, not matter if the
	 *         call is a simulation.
	 */
	int receiveMatter(int amount, boolean simulate);

	/**
	 * Used to extract matter.
	 *
	 * @param amount   the amount of matter extracted.
	 * @param simulate No matter will be stored if this is set to true. Used to
	 *                 check if the specified amount of matter can be extracted.
	 * @return the amount of matter extracted. This is the same, no matter if the
	 *         call is a simulation.
	 */
	int extractMatter(int amount, boolean simulate);

	@Override
	default CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("matter", getMatterStored());
		tag.putInt("capacity", getCapacity());
		return tag;
	}

	@Override
	default void deserializeNBT(CompoundTag tag) {
		setMatterStored(tag.getInt("matter"));
		setCapacity(tag.getInt("capacity"));
	}

}
