
package huntyboy102.moremod.data.matter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import huntyboy102.moremod.api.matter.IMatterEntry;
import net.minecraft.nbt.CompoundTag;

public abstract class MatterEntryAbstract<KEY, MAT> implements IMatterEntry<KEY, MAT> {
	protected final List<IMatterEntryHandler<MAT>> handlers;
	protected KEY key;

	public MatterEntryAbstract() {
		this.handlers = new ArrayList<>();
	}

	public MatterEntryAbstract(KEY key) {
		this();
		this.key = key;
	}

	@Override
	public int getMatter(MAT key) {
		int matter = 0;
		for (IMatterEntryHandler handler : handlers) {
			matter = handler.modifyMatter(key, matter);
			if (handler.finalModification(key)) {
				return matter;
			}
		}
		return matter;
	}

	public abstract void writeTo(DataOutput output) throws IOException;

	public abstract void writeTo(CompoundTag tagCompound);

	public abstract void readFrom(DataInput input) throws IOException;

	public abstract void readFrom(CompoundTag tagCompound);

	public abstract void readKey(String data);

	public abstract String writeKey();

	public abstract boolean hasCached();

	@Override
	public void addHandler(IMatterEntryHandler<MAT> handler) {
		handlers.add(handler);
		Collections.sort(handlers);
	}

	public void clearHandlers() {
		handlers.clear();
	}

	public KEY getKey() {
		return key;
	}
}
