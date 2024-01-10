
package huntyboy102.moremod.data.matter_network;

import huntyboy102.moremod.util.MatterDatabaseHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ItemPattern {
	private int itemID;
	private int damage;
	private int progress;

	public ItemPattern() {

	}

	public ItemPattern(ItemStack itemStack) {
		this(itemStack, 0);
	}

	public ItemPattern(ItemStack itemStack, int progress) {
		this(Item.getId(itemStack.getItem()), itemStack.getDamageValue(), progress);
	}

	public ItemPattern(int itemID) {
		this(itemID, 0, 0);
	}

	public ItemPattern(int itemID, int damage) {
		this(itemID, damage, 0);
	}

	public ItemPattern(int itemID, int damage, int progress) {
		this.itemID = itemID;
		this.damage = damage;
		this.progress = progress;
	}

	public ItemPattern(CompoundTag tagCompound) {
		readFromNBT(tagCompound);
	}

	public static ItemPattern fromBuffer(ByteBuf byteBuf) {
		int itemID = byteBuf.readShort();
		if (itemID < 0) {
			return null;
		} else {
			ItemPattern pattern = new ItemPattern(itemID);
			pattern.progress = byteBuf.readByte();
			pattern.damage = byteBuf.readShort();
			return pattern;
		}
	}

	public static void writeToBuffer(ByteBuf byteBuf, ItemPattern itemPattern) {
		if (itemPattern == null) {
			byteBuf.writeShort(-1);
		} else {
			byteBuf.writeShort(itemPattern.itemID);
			byteBuf.writeByte(itemPattern.progress);
			byteBuf.writeShort(itemPattern.damage);
		}
	}

	public ItemStack toItemStack(boolean withInfo) {
		ItemStack itemStack = new ItemStack(Item.byId(itemID));
		itemStack.setDamageValue(damage);
		if (withInfo) {
			itemStack.setTag(new CompoundTag());
			itemStack.getTag().putByte(MatterDatabaseHelper.PROGRESS_TAG_NAME, (byte) progress);
		}
		return itemStack;
	}

	public void writeToNBT(CompoundTag nbtTagCompound) {
		nbtTagCompound.putShort("id", (short) itemID);
		nbtTagCompound.putByte(MatterDatabaseHelper.PROGRESS_TAG_NAME, (byte) progress);
		nbtTagCompound.putShort("Damage", (short) damage);
	}

	public void readFromNBT(CompoundTag nbtTagCompound) {
		itemID = nbtTagCompound.getShort("id");
		progress = nbtTagCompound.getByte(MatterDatabaseHelper.PROGRESS_TAG_NAME);
		damage = nbtTagCompound.getShort("Damage");
	}

	public int getItemID() {
		return itemID;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public float getProgressF() {
		return (float) progress / (float) MatterDatabaseHelper.MAX_ITEM_PROGRESS;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public Item getItem() {
		return Item.byId(getItemID());
	}

	public boolean equals(ItemStack itemStack) {
		if (itemStack != null) {
			return getDamage() == itemStack.getDamageValue() && getItemID() == Item.getId(itemStack.getItem());
		}
		return false;
	}

	public boolean equals(ItemPattern pattern) {
		return this.getItemID() == pattern.getItemID() && this.getDamage() == pattern.getDamage();
	}

	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			return true;
		}
		if (object instanceof ItemPattern) {
			return equals((ItemPattern) object);
		}
		return false;
	}

	public String getDisplayName() {
		return toItemStack(false).getDisplayName();
	}

	public ItemPattern copy() {
		ItemPattern pattern = new ItemPattern(itemID, damage, progress);
		return pattern;
	}

	@Override
	public int hashCode() {
		return itemID + damage + progress;
	}
}
