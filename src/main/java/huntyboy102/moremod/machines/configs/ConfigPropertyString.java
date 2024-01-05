
package huntyboy102.moremod.machines.configs;

import net.minecraft.nbt.CompoundTag;

import java.util.regex.Pattern;

public class ConfigPropertyString extends ConfigPropertyAbstract {
	private String value;
	private short maxLength = Short.MAX_VALUE;
	private Pattern pattern;

	public ConfigPropertyString(String key, String unlocalizedName, String def) {
		super(key, unlocalizedName);
		value = def;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value.toString();
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.putString(getKey(), value);
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		value = nbt.getString(getKey());
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	public short getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(short maxLength) {
		this.maxLength = maxLength;
	}

	public Pattern getPattern() {
		return this.pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}
