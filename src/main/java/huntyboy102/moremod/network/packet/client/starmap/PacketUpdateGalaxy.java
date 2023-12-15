
package huntyboy102.moremod.network.packet.client.starmap;

import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.data.Galaxy;
import huntyboy102.moremod.util.MOLog;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.PacketAbstract;
import huntyboy102.moremod.network.packet.client.AbstractClientPacketHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpdateGalaxy extends PacketAbstract {

	Galaxy galaxy;

	public PacketUpdateGalaxy() {

	}

	public PacketUpdateGalaxy(Galaxy galaxy) {
		this.galaxy = galaxy;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		galaxy = new Galaxy();
		galaxy.readFromBuffer(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try {
			galaxy.writeToBuffer(buf);
		} catch (Exception e) {
			MOLog.fatal("There was a problem writing the galaxy to buffer when sending to player", e);
		}
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketUpdateGalaxy> {

		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketUpdateGalaxy message, MessageContext ctx) {
			message.galaxy.setWorld(player.world);
			GalaxyClient.getInstance().setTheGalaxy(message.galaxy);
			GalaxyClient.getInstance().loadClaimedPlanets();
		}
	}
}
