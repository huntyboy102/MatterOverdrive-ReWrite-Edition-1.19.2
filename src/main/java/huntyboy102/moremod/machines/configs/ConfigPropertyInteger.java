
package huntyboy102.moremod.machines.configs;

import net.minecraft.nbt.CompoundTag;

public class ConfigPropertyInteger extends ConfigPropertyAbstract {
	private final int min;
	private final int max;
	private int value;

	public ConfigPropertyInteger(String name, String unlocalizedName, int min, int max, int def) {
		super(name, unlocalizedName);
		this.min = min;
		this.max = max;
		this.value = def;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = (int) value;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.putInt(getUnlocalizedName(), value);
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		value = nbt.putInt(getUnlocalizedName());
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
}
