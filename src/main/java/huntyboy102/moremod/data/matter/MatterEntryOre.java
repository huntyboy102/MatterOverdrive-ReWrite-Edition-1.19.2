
package huntyboy102.moremod.data.matter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class MatterEntryOre extends MatterEntryAbstract<String, ItemStack> {
	public MatterEntryOre() {
		super();
	}

	public MatterEntryOre(String s) {
		super(s);
	}

	@Override
	public void writeTo(DataOutput output) throws IOException {
		int cachedCount = 0;
		for (IMatterEntryHandler<?> handler : handlers) {
			if (handler instanceof ItemStackHandlerCachable) {
				cachedCount++;
			}
		}

		output.writeInt(cachedCount);

		for (IMatterEntryHandler<?> handler : handlers) {
			if (handler instanceof ItemStackHandlerCachable) {
				((ItemStackHandlerCachable) handler).writeTo(output);
			}
		}
	}

	@Override
	public void writeTo(CompoundTag tagCompound) {
		ListTag handlers = new ListTag();
		for (IMatterEntryHandler<?> handler : this.handlers) {
			if (handler instanceof ItemStackHandlerCachable) {
				CompoundTag handlerTag = new CompoundTag();
				((ItemStackHandlerCachable) handler).writeTo(handlerTag);
				handlers.add(handlerTag);
			}
		}
		tagCompound.put("Handlers", handlers);
	}

	@Override
	public void readFrom(DataInput input) throws IOException {
		clearAllCashed();
		int count = input.readInt();
		for (int i = 0; i < count; i++) {
			ItemStackHandlerCachable genericHandler = new ItemStackHandlerCachable();
			genericHandler.readFrom(input);
			handlers.add(genericHandler);
		}
	}

	@Override
	public void readFrom(CompoundTag tagCompound) {
		clearAllCashed();
		ListTag tagList = tagCompound.getList("Handlers", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			ItemStackHandlerCachable genericHandler = new ItemStackHandlerCachable();
			genericHandler.readFrom(tagList.getCompound(i));
			handlers.add(genericHandler);
		}
	}

	@Override
	public void readKey(String data) {
		key = data;
	}

	@Override
	public String writeKey() {
		return getKey();
	}

	@Override
	public boolean hasCached() {
		int cachedCount = 0;
		for (IMatterEntryHandler<?> handler : handlers) {
			if (handler instanceof ItemStackHandlerCachable) {
				cachedCount++;
			}
		}
		return cachedCount > 0;
	}

	public void clearAllCashed() {
		handlers.removeIf(
				itemStackIMatterEntryHandler -> itemStackIMatterEntryHandler instanceof ItemStackHandlerCachable);
	}
}
