
package huntyboy102.moremod.network.packet.client;

import huntyboy102.moremod.tile.TileEntityMachinePacketQueue;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.TileEntityUpdatePacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSendQueueFlash extends TileEntityUpdatePacket {
	public PacketSendQueueFlash() {
		super();
	}

	public PacketSendQueueFlash(TileEntityMachinePacketQueue tileEntity) {
		super(tileEntity);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketSendQueueFlash> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketSendQueueFlash message, MessageContext ctx) {
			TileEntity tileEntity = message.getTileEntity(player.world);
			if (tileEntity instanceof TileEntityMachinePacketQueue) {
				((TileEntityMachinePacketQueue) tileEntity).flashTime = 5;
			}
		}
	}
}
