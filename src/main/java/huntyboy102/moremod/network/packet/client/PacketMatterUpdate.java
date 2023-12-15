
package huntyboy102.moremod.network.packet.client;

import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.TileEntityUpdatePacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketMatterUpdate extends TileEntityUpdatePacket {
	private int matter = 0;

	public PacketMatterUpdate() {
	}

	public PacketMatterUpdate(TileEntity tileentity) {
		super(tileentity.getPos());
		matter = tileentity.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null).getMatterStored();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		matter = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(matter);
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketMatterUpdate> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketMatterUpdate message, MessageContext ctx) {
			if (player != null && player.world != null) {
				TileEntity tileEntity = player.world.getTileEntity(message.pos);

				if (tileEntity != null && tileEntity.hasCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null)) {
					tileEntity.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null)
							.setMatterStored(message.matter);
				}
			}
		}
	}
}
