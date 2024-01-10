
package huntyboy102.moremod.data.matter_network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;

public class ItemPatternMapping {
	private ItemPattern itemPattern;
	private BlockPos databaseId;
	private int storageId;
	private int patternId;

	public ItemPatternMapping(ByteBuf byteBuf) {
		itemPattern = ItemPattern.fromBuffer(byteBuf);
		databaseId = BlockPos.of(byteBuf.readLong());
		storageId = byteBuf.readByte();
		patternId = byteBuf.readByte();
	}

	public ItemPatternMapping(ItemPattern itemPattern, BlockPos databaseId, int storageId, int patternId) {
		this.databaseId = databaseId;
		this.itemPattern = itemPattern;
		this.storageId = storageId;
		this.patternId = patternId;
	}

	public ItemPattern getItemPattern() {
		return itemPattern;
	}

	public BlockPos getDatabaseId() {
		return databaseId;
	}

	public void writeToBuffer(ByteBuf byteBuf) {
		ItemPattern.writeToBuffer(byteBuf, itemPattern);
		byteBuf.writeLong(databaseId.asLong());
		byteBuf.writeByte(storageId);
		byteBuf.writeByte(patternId);
	}

	public int getStorageId() {
		return storageId;
	}

	public int getPatternId() {
		return patternId;
	}
}
