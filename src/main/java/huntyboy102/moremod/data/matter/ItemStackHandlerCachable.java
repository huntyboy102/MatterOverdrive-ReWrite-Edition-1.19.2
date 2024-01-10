
package huntyboy102.moremod.data.matter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ItemStackHandlerCachable extends MatterEntryHandlerAbstract<ItemStack> {
	private boolean metadataAware;
	private boolean isFinalHandler;
	private int meta;
	private int matter;
	private boolean custom;

	public ItemStackHandlerCachable() {

	}

	public ItemStackHandlerCachable(int matter) {
		this.matter = matter;
	}

	public ItemStackHandlerCachable(int matter, boolean isFinalHandler) {
		this.matter = matter;
		this.isFinalHandler = isFinalHandler;
	}

	public ItemStackHandlerCachable(int matter, int meta) {
		this(matter);
		this.meta = (short) meta;
		this.metadataAware = true;
	}

	public ItemStackHandlerCachable(int matter, int meta, boolean isFinalHandler) {
		this(matter, meta);
		this.isFinalHandler = isFinalHandler;
	}

	@Override
	public int modifyMatter(ItemStack itemStack, int originalMatter) {
		if (metadataAware) {
			if (itemStack.getDamageValue() == meta) {
				return matter;
			}
		} else {
			return matter;
		}
		return originalMatter;
	}

	@Override
	public boolean finalModification(ItemStack itemStack) {
		return false;
	}

	public void writeTo(DataOutput output) throws IOException {
		output.writeBoolean(metadataAware);
		output.writeBoolean(isFinalHandler);
		output.writeShort(meta);
		output.writeInt(matter);
		output.writeByte(priority);
	}

	public void writeTo(CompoundTag tagCompound) {
		tagCompound.putBoolean("metaAware", metadataAware);
		tagCompound.putBoolean("final", isFinalHandler);
		tagCompound.putShort("meta", (short) meta);
		tagCompound.putInt("matter", matter);
		tagCompound.putByte("priority", (byte) priority);
	}

	public void readFrom(DataInput dataInput) throws IOException {
		metadataAware = dataInput.readBoolean();
		isFinalHandler = dataInput.readBoolean();
		meta = dataInput.readShort();
		matter = dataInput.readInt();
		priority = dataInput.readByte();
	}

	public void readFrom(CompoundTag tagCompound) {
		metadataAware = tagCompound.getBoolean("metaAware");
		isFinalHandler = tagCompound.getBoolean("final");
		meta = tagCompound.getShort("meta");
		matter = tagCompound.getInt("matter");
		priority = tagCompound.getByte("priority");
	}

	public boolean isCustom() {
		return custom;
	}

	public ItemStackHandlerCachable markCustom() {
		this.custom = true;
		return this;
	}
}
