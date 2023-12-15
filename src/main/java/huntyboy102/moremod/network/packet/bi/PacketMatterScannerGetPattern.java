
package huntyboy102.moremod.network.packet.bi;

import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.TileEntityUpdatePacket;
import net.minecraft.util.math.BlockPos;

public class PacketMatterScannerGetPattern extends TileEntityUpdatePacket {
	int id;
	short damage;
	short scannerSlot;
	short type;

	public PacketMatterScannerGetPattern() {
		super();
	}

	public PacketMatterScannerGetPattern(BlockPos pos) {
		super(pos);
	}

	public PacketMatterScannerGetPattern(BlockPos pos, int id, short damage, short scannerSlot, short type) {
		this(pos);
		this.id = id;
		this.damage = damage;
		this.scannerSlot = scannerSlot;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		id = buf.readInt();
		damage = buf.readShort();
		scannerSlot = buf.readShort();
		type = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(id);
		buf.writeShort(damage);
		buf.writeShort(scannerSlot);
		buf.writeShort(type);
	}
}
