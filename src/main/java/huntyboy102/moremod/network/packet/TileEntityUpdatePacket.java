
package huntyboy102.moremod.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityUpdatePacket extends PacketAbstract {
	public BlockPos pos;

	public TileEntityUpdatePacket() {
		super();
	}

	public TileEntityUpdatePacket(BlockPos pos) {
		this.pos = pos;
	}

	public TileEntityUpdatePacket(TileEntity entity) {
		this(entity.getPos());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	public TileEntity getTileEntity(World world) {
		return world.getTileEntity(pos);
	}
}
