
package huntyboy102.moremod.data.matter;

import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MatterEntryEntity extends MatterEntryAbstract<Class<? extends Entity>, Entity> {
	public MatterEntryEntity(Class<? extends Entity> aClass) {
		super(aClass);
	}

	@Override
	public void writeTo(DataOutput output) throws IOException {

	}

	@Override
	public void writeTo(CompoundTag tagCompound) {

	}

	@Override
	public void readFrom(DataInput input) throws IOException {

	}

	@Override
	public void readFrom(CompoundTag tagCompound) {

	}

	@Override
	public void readKey(String data) {

	}

	@Override
	public String writeKey() {
		return null;
	}

	@Override
	public boolean hasCached() {
		return false;
	}
}
