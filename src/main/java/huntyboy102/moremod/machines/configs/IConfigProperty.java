
package huntyboy102.moremod.machines.configs;

import net.minecraft.nbt.CompoundTag;

/**
 * @autor Simeon
 * @since 8/16/2015
 */
public interface IConfigProperty {
	String getKey();

	String getUnlocalizedName();

	Object getValue();

	void setValue(Object value);

	void writeToNBT(CompoundTag nbt);

	void readFromNBT(CompoundTag nbt);

	Class<?> getType();
}
